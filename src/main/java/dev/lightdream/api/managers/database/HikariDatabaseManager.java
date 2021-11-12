package dev.lightdream.api.managers.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lightdream.api.IAPI;
import dev.lightdream.api.annotations.DatabaseField;
import dev.lightdream.api.annotations.DatabaseTable;
import dev.lightdream.api.databases.DatabaseEntry;
import dev.lightdream.api.utils.Debugger;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public abstract class HikariDatabaseManager extends DatabaseManager {

    private HikariDataSource ds;

    @SuppressWarnings("FieldMayBeFinal")

    public HikariDatabaseManager(IAPI api) {
        this.api = api;
        this.sqlConfig = api.getSQLConfig();
        connect();
    }

    public void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getDatabaseURL());
        config.setUsername(sqlConfig.username);
        config.setPassword(sqlConfig.password);
        config.setConnectionTestQuery("SELECT 1");
        config.setMaxLifetime(60000);
        config.setIdleTimeout(45000);
        config.setMaximumPoolSize(50);
        switch (sqlConfig.driver) {
            case SQLITE:
                config.setDriverClassName("org.sqlite.JDBC");
                config.addDataSourceProperty("dataSourceClassName", "org.sqlite.SQLiteDataSource");
                break;
        }
        ds = new HikariDataSource(config);
        setup();
    }

    @SneakyThrows
    public Connection getConnection() {
        return ds.getConnection();
    }

    @SuppressWarnings("SqlNoDataSourceInspection")
    @SneakyThrows
    @Override
    public <T> List<T> getAll(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(DatabaseTable.class)) {
            api.getLogger().severe("Class " + clazz.getSimpleName() + " is not annotated as a database table");
            return new ArrayList<>();
        }

        List<T> output = new ArrayList<>();

        ResultSet rs = executeQuery(
                sqlConfig.driver.selectAll.replace("%table%", clazz.getAnnotation(DatabaseTable.class).table()),
                new ArrayList<>()
        );
        while (rs.next()) {
            T obj = clazz.newInstance();
            Field[] fields = obj.getClass().getFields();
            for (Field field : fields) {
                field.set(obj, getObject(field.getType(), rs.getObject(field.getName())));
            }
            output.add(obj);
        }
        return output;
    }

    @SneakyThrows
    public <T> List<T> get(Class<T> clazz, HashMap<String, Object> queries) {
        if (queries.size() == 0) {
            return getAll(clazz);
        }

        if (!clazz.isAnnotationPresent(DatabaseTable.class)) {
            api.getLogger().severe("Class " + clazz.getSimpleName() + " is not annotated as a database table");
            return new ArrayList<>();
        }

        StringBuilder placeholder = new StringBuilder();
        for (String key : queries.keySet()) {
            Object value = queries.get(key);
            placeholder.append(key).append("=").append(formatQueryArgument(value)).append(" AND ");
        }
        placeholder.append(" ");
        placeholder = new StringBuilder(placeholder.toString().replace(" AND  ", ""));

        List<T> output = new ArrayList<>();
        ResultSet rs = executeQuery(
                sqlConfig.driver.select.replace("%placeholder%", placeholder.toString())
                        .replace("%table%", clazz.getAnnotation(DatabaseTable.class).table()),
                new ArrayList<>()
        );
        while (rs.next()) {
            T obj = clazz.newInstance();
            Field[] fields = obj.getClass().getFields();
            for (Field field : fields) {
                field.set(obj, getObject(field.getType(), rs.getString(field.getName())));
            }
            output.add(obj);
        }
        return output;
    }

    @SuppressWarnings("StringConcatenationInLoop")
    @SneakyThrows
    @Override
    public void createTable(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(DatabaseTable.class)) {
            api.getLogger().severe("Class " + clazz.getSimpleName() + " is not annotated as a database table");
            return;
        }

        Object obj = clazz.newInstance();
        String placeholder = "";

        Field[] fields = obj.getClass().getFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DatabaseField.class)) {
                continue;
            }
            DatabaseField dbField = field.getAnnotation(DatabaseField.class);
            placeholder += dbField.columnName() + " " +
                    getDataType(field.getType()) + " " +
                    (dbField.unique() ? "UNIQUE " : "") +
                    (dbField.autoGenerate() ? sqlConfig.driver.autoIncrement : "") +
                    ",";
        }

        placeholder += ",";
        placeholder = placeholder.replace(",,", "");

        executeUpdate(
                sqlConfig.driver.createTable.replace("%placeholder%", placeholder)
                        .replace("%table%", clazz.getAnnotation(DatabaseTable.class).table()),
                new ArrayList<>()
        );
    }

    @Override
    public abstract void setup();

    @Override
    public void setup(Class<?> clazz) {
        createTable(clazz);
        //todo implement cache
    }

    @Override
    public void save() {
        //todo implement cache
        //todo
    }

    @SneakyThrows
    @Override
    public void save(DatabaseEntry entry, boolean cache) {
        if (!entry.getClass().isAnnotationPresent(DatabaseTable.class)) {
            //todo logger
            return;
        }
        List<? extends DatabaseEntry> currentEntries = new ArrayList<>();
        if (entry.id != 0) {
            currentEntries = get(entry.getClass(), new HashMap<String, Object>() {{
                put("id", entry.id);
            }});
        }
        if (currentEntries.size() == 0) {
            StringBuilder placeholder1 = new StringBuilder();
            StringBuilder placeholder2 = new StringBuilder();

            Field[] fields = entry.getClass().getFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(DatabaseField.class)) {
                    continue;
                }
                DatabaseField databaseField = field.getAnnotation(DatabaseField.class);

                if (databaseField.autoGenerate()) {
                    continue;
                }

                String columnName = databaseField.columnName();
                placeholder1.append(columnName).append(",");
                placeholder2.append(formatQueryArgument(field.get(entry))).append(",");
            }

            placeholder1.append(",");
            placeholder2.append(",");

            placeholder1 = new StringBuilder(placeholder1.toString().replace(",,", ""));
            placeholder2 = new StringBuilder(placeholder2.toString().replace(",,", ""));

            executeUpdate(
                    sqlConfig.driver.insert
                            .replace("%placeholder-1%", placeholder1.toString())
                            .replace("%placeholder-2%", placeholder2.toString())
                            .replace("%table%", entry.getClass().getAnnotation(DatabaseTable.class).table()),
                    new ArrayList<>(

                    ));
            return;
        }

        StringBuilder placeholder = new StringBuilder();

        Field[] fields = entry.getClass().getFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DatabaseField.class)) {
                continue;
            }
            DatabaseField dbField = field.getAnnotation(DatabaseField.class);
            placeholder.append(field.getName()).append("=").append(formatQueryArgument(field.get(entry)));
        }

        placeholder.append(",");
        placeholder = new StringBuilder(placeholder.toString().replace(",,", ""));
        placeholder.append(")");

        executeUpdate(
                sqlConfig.driver.update.replace("%placeholder%", placeholder.toString())
                        .replace("%table%", entry.getClass().getAnnotation(DatabaseTable.class).table()),
                Arrays.asList(entry.id));
    }

    @SneakyThrows
    @Override
    public void delete(DatabaseEntry entry) {
        executeUpdate(
                sqlConfig.driver.delete.replace("%table%", entry.getClass().getAnnotation(DatabaseTable.class).table()),
                Arrays.asList(entry.id));
    }

    @SneakyThrows
    private void executeUpdate(String sql, List<Object> values) {

        PreparedStatement statement = getConnection().prepareStatement(sql);

        for (int i = 0; i < values.size(); i++) {
            statement.setObject(i + 1, values.get(i));
        }

        statement.executeUpdate();
    }

    @SneakyThrows
    private ResultSet executeQuery(String sql, List<Object> values) {
        PreparedStatement statement = getConnection().prepareStatement(sql);

        for (int i = 0; i < values.size(); i++) {
            statement.setObject(i + 1, values.get(i));
        }

        return statement.executeQuery();
    }
}
