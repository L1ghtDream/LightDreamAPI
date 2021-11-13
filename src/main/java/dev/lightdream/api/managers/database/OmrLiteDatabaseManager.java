package dev.lightdream.api.managers.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.db.DatabaseTypeUtils;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;
import com.j256.ormlite.table.TableUtils;
import dev.lightdream.api.IAPI;
import dev.lightdream.api.databases.DatabaseEntry;
import dev.lightdream.api.dto.LambdaExecutor;
import dev.lightdream.api.utils.Logger;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OmrLiteDatabaseManager extends DatabaseManager {

    public boolean triedConnecting = false;
    private ConnectionSource connectionSource;
    private DatabaseConnection databaseConnection;
    @SuppressWarnings("FieldMayBeFinal")
    private HashMap<Class<?>, List<DatabaseEntry>> cacheMap;
    @SuppressWarnings("FieldMayBeFinal")
    private HashMap<Class<?>, Dao<?, ?>> daoMap;

    @SneakyThrows
    @SuppressWarnings("unused")
    public OmrLiteDatabaseManager(IAPI api) {
        this.api = api;
        this.sqlConfig = api.getSQLConfig();

        connect();
    }

    @Override
    public void setup() {

    }

    @SneakyThrows
    public void connect() {
        this.cacheMap = new HashMap<>();
        this.daoMap = new HashMap<>();

        this.connectionSource = new JdbcConnectionSource(
                getDatabaseURL(),
                sqlConfig.username,
                sqlConfig.password,
                DatabaseTypeUtils.createDatabaseType(getDatabaseURL())
        );

        this.databaseConnection = connectionSource.getReadWriteConnection(null);
        setup();
    }


    @SuppressWarnings("unused")
    @SneakyThrows
    public void createTable(Class<?> clazz) {
        TableUtils.createTableIfNotExists(connectionSource, clazz);
    }

    @SneakyThrows
    public Dao<?, ?> createDao(Class<?> clazz) {
        daoMap.put(clazz, DaoManager.createDao(connectionSource, clazz));
        return daoMap.get(clazz);
    }

    @SneakyThrows
    public Dao<?, ?> getDao(Class<?> clazz) {
        if (!daoMap.containsKey(clazz)) {
            throw new Exception("The class '" + clazz.getSimpleName() + "' has not been setup. Use setup(" + clazz.getSimpleName() + ".class); in your database manager");
        }
        return daoMap.get(clazz);
    }

    @SuppressWarnings({"unused", "unchecked"})
    @SneakyThrows
    public void save(boolean cache) {
        Logger.info("Saving database tables to " + api.getDataFolder());
        if (cache) {
            cacheMap.forEach((clazz, list) -> {
                list.forEach(obj -> {
                    try {
                        ((Dao<DatabaseEntry, Integer>) getDao(clazz)).createOrUpdate(obj);
                    } catch (SQLException sqlException) {
                        sqlException.printStackTrace();
                    }
                });
                Logger.info("Saving table " + getDao(clazz).getTableName());
                try {
                    getDao(clazz).commit(databaseConnection);
                } catch (SQLException sqlException) {
                    sqlException.printStackTrace();
                }
            });
        } else {
            for (Dao<?, ?> dao : daoMap.values()) {
                Logger.info("Saving table " + dao.getTableName());
                dao.commit(databaseConnection);
            }
        }

    }

    public void save() {
        save(true);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public void save(DatabaseEntry entry, boolean cache) {
        if (cache) {
            List<DatabaseEntry> list = cacheMap.getOrDefault(entry.getClass(), new ArrayList<>());
            if (list.contains(entry)) {
                list.remove(entry);
            } else {
                ((Dao<DatabaseEntry, Integer>) daoMap.get(entry.getClass())).createOrUpdate(entry);
            }
            list.add(entry);
            cacheMap.put(entry.getClass(), list);
        } else {
            Logger.info("Saving " + entry);
            try {
                ((Dao<DatabaseEntry, Integer>) daoMap.get(entry.getClass())).createOrUpdate(entry);
                daoMap.get(entry.getClass()).commit(databaseConnection);
            } catch (Throwable e) {
                Logger.info("Seems like a duplicate, ignoring and using the local value");
            }
        }
    }

    public void save(DatabaseEntry entry) {
        save(entry, true);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> List<T> getAll(Class<T> clazz, boolean cache) {
        if (cache) {
            if (!cacheMap.containsKey(clazz)) {
                throw new Exception("The class '" + clazz.getSimpleName() + "' has not been setup. Use setup(" + clazz.getSimpleName() + ".class); in your database manager");
            }
            return (List<T>) cacheMap.get(clazz);
        } else {
            List<T> list = (List<T>) queryAll(clazz);
            for (T t : list) {
                ((DatabaseEntry) t).setAPI(api);
            }
            return list;
        }
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public List<Integer> getAllIDs(Class<?> clazz, boolean cache) {
        List<DatabaseEntry> entries;
        if (cache) {
            if (!cacheMap.containsKey(clazz)) {
                throw new Exception("The class '" + clazz.getSimpleName() + "' has not been setup. Use setup(" + clazz.getSimpleName() + ".class); in your database manager");
            }
            entries = cacheMap.get(clazz);
        } else {
            entries = (List<DatabaseEntry>) queryAll(clazz);
        }
        List<Integer> output = new ArrayList<>();
        for (DatabaseEntry databaseEntry : entries) {
            output.add(databaseEntry.id);
        }
        return output;
    }

    public List<?> queryAll(Class<?> clazz) {
        Logger.info("Getting from database");
        if (triedConnecting) {
            Logger.info("Already tried reconnecting. Returning empty list");
            return new ArrayList<>();
        }

        try {
            List<Object> output = new ArrayList<>();
            try {
                output.addAll(getDao(clazz).queryForAll());
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
            return output;
        } catch (Throwable t) {
            triedConnecting = true;
            Bukkit.getScheduler().runTaskLater(api.getPlugin(), () -> triedConnecting = false, 10 * 20L);
            Logger.error("Connection to the database has been closed with message %message%. Reconnecting.".replace("%message%", t.getMessage()));
            connect();
            return new ArrayList<>();
        }
    }

    @SuppressWarnings("unused")
    public List<Integer> getAllIDs(Class<?> clazz) {
        return getAllIDs(clazz, true);
    }

    public <T> List<T> getAll(Class<T> clazz) {
        return getAll(clazz, true);
    }

    @SuppressWarnings({"unused", "unchecked"})
    @SneakyThrows
    public void delete(DatabaseEntry entry) {
        List<DatabaseEntry> list = cacheMap.get(entry.getClass());
        list.remove(entry);
        cacheMap.put(entry.getClass(), list);
        ((Dao<DatabaseEntry, Integer>) daoMap.get(entry.getClass())).delete(entry);
    }


    @SuppressWarnings("unchecked")
    @SneakyThrows
    public void setup(Class<?> clazz) {
        Logger.info("Setting up " + clazz.getSimpleName() + " database table");
        createTable(clazz);
        createDao(clazz).setAutoCommit(databaseConnection, false);
        cacheMap.put(clazz, (List<DatabaseEntry>) getAll(clazz, false));
    }

    @Override
    public HashMap<Class<?>, String> getDataTypes() {
        return null;
    }

    @Override
    public HashMap<Class<?>, LambdaExecutor> getSerializeMap() {
        return null;
    }

    @Override
    public HashMap<Class<?>, LambdaExecutor> getDeserializeMap() {
        return null;
    }
}