package dev.lightdream.api.managers.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.lightdream.api.IAPI;
import dev.lightdream.api.annotations.DatabaseField;
import dev.lightdream.api.annotations.DatabaseTable;
import dev.lightdream.api.databases.DatabaseEntry;
import dev.lightdream.api.databases.User;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class HikariDatabaseManager extends DatabaseManager {

    private HikariDataSource ds;

    @SuppressWarnings("FieldMayBeFinal")

    public HikariDatabaseManager(IAPI api) {
        this.api = api;
        this.sqlConfig = api.getSQLConfig();
    }

    public void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(getDatabaseURL());
        config.setUsername(sqlConfig.username);
        config.setPassword(sqlConfig.password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
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
        PreparedStatement statement = getConnection().prepareStatement(sqlConfig.driver.selectAll);
        statement.setString(1, clazz.getAnnotation(DatabaseTable.class).table());

        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            T obj = clazz.newInstance();
            Field[] fields = obj.getClass().getFields();
            for (Field field : fields) {
                field.set(obj, rs.getString(field.getName()));
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
        PreparedStatement statement = getConnection().prepareStatement(
                sqlConfig.driver.select.replace("%placeholder%", placeholder.toString())
        );
        statement.setString(1, clazz.getAnnotation(DatabaseTable.class).table());
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            T obj = clazz.newInstance();
            Field[] fields = obj.getClass().getFields();
            for (Field field : fields) {
                field.set(obj, rs.getString(field.getName()));
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
                    getDataType(field) + " " +
                    (dbField.unique() ? "UNIQUE " : "") +
                    (dbField.autoGenerate() ? "AUTO_INCREMENT " : "") +
                    ",";
        }

        placeholder += ",";
        placeholder = placeholder.replace(",,", "");
        placeholder += ")";

        PreparedStatement statement = getConnection().prepareStatement(
                sqlConfig.driver.createTable.replace("%placeholder%", placeholder)
        );
        statement.setString(1, sqlConfig.database);
        statement.setString(2, clazz.getAnnotation(DatabaseTable.class).table());

        statement.executeUpdate();
    }

    @Override
    public void setup() {

    }

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
        if(entry.id!=0) {
            currentEntries = get(entry.getClass(), new HashMap<String, Object>() {{
                put("id", entry.id);
            }});
        }
        if (currentEntries.size() == 0) {
            StringBuilder placeholder = new StringBuilder();

            Field[] fields = entry.getClass().getFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(DatabaseField.class)) {
                    continue;
                }
                DatabaseField dbField = field.getAnnotation(DatabaseField.class);
                placeholder.append(formatQueryArgument(field.get(entry))).append(",");
            }

            placeholder.append(",");
            placeholder = new StringBuilder(placeholder.toString().replace(",,", ""));
            placeholder.append(")");

            PreparedStatement statement = getConnection().prepareStatement(
                    sqlConfig.driver.insert.replace("%placeholder%", placeholder.toString())
            );
            statement.setString(1, entry.getClass().getAnnotation(DatabaseTable.class).table());

            statement.executeUpdate();
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

        PreparedStatement statement = getConnection().prepareStatement(
                sqlConfig.driver.update.replace("%placeholder%", placeholder.toString())
        );
        statement.setString(1, entry.getClass().getAnnotation(DatabaseTable.class).table());
        statement.setInt(2, entry.id);

        statement.executeUpdate();
    }

    @SneakyThrows
    @Override
    public void delete(DatabaseEntry entry) {
        PreparedStatement statement = getConnection().prepareStatement(sqlConfig.driver.delete);
        statement.setString(1, entry.getClass().getAnnotation(DatabaseTable.class).table());
        statement.setInt(2, entry.id);
        statement.executeUpdate();
    }
}
