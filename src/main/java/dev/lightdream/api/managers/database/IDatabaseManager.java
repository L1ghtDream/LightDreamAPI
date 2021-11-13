package dev.lightdream.api.managers.database;

import dev.lightdream.api.databases.DatabaseEntry;

import java.util.List;

public interface IDatabaseManager {

    void connect();

    <T> List<T> getAll(Class<T> clazz);

    void createTable(Class<?> clazz);

    void setup();

    void setup(Class<?> clazz);

    void save();

    void save(DatabaseEntry object, boolean cache);

    void save(DatabaseEntry object);

    void delete(DatabaseEntry entry);

}
