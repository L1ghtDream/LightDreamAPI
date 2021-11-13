package dev.lightdream.api.managers.database;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.configs.SQLConfig;
import dev.lightdream.api.dto.LambdaExecutor;
import dev.lightdream.api.utils.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public abstract class DatabaseManager implements IDatabaseManager {

    public IAPI api;
    public SQLConfig sqlConfig;
    public HashMap<Class<?>, LambdaExecutor> deserializeMap = new HashMap<Class<?>, LambdaExecutor>() {{
        put(UUID.class, (object) -> UUID.fromString(object.toString()));
    }};
    public HashMap<Class<?>, LambdaExecutor> serializeMap = new HashMap<Class<?>, LambdaExecutor>() {{
        put(String.class, (object) -> "\"" + object.toString() + "\"");
        put(UUID.class, (object) -> "\"" + object.toString() + "\"");
    }};

    public @NotNull String getDatabaseURL() {
        switch (sqlConfig.driver) {
            case MYSQL:
            case MARIADB:
            case POSTGRESQL:
                return "jdbc:" + sqlConfig.driver.toString().toLowerCase() + "://" + sqlConfig.host + ":" + sqlConfig.port + "/" + sqlConfig.database + "?useSSL=" + sqlConfig.useSSL + "&autoReconnect=true";
            case SQLSERVER:
                return "jdbc:sqlserver://" + sqlConfig.host + ":" + sqlConfig.port + ";databaseName=" + sqlConfig.database;
            case H2:
                return "jdbc:h2:file:" + sqlConfig.database;
            case SQLITE:
                return "jdbc:sqlite:" + new File(api.getDataFolder(), sqlConfig.database + ".db");
            default:
                throw new UnsupportedOperationException("Unsupported driver (how did we get here?): " + sqlConfig.driver.name());
        }
    }

    public String getDataType(Class<?> clazz) {
        String dbDataType = sqlConfig.driver.dataTypes.get(clazz);

        if (dbDataType == null) {
            HashMap<Class<?>, String> additionalDataTypes = getDataTypes();

            if (additionalDataTypes == null) {
                Logger.error("DataType " + clazz.getSimpleName() + " is not a supported data type");
                return "";
            }

            dbDataType = additionalDataTypes.get(clazz);
        }

        if (dbDataType != null) {
            return dbDataType;
        }

        Logger.error("DataType " + clazz.getSimpleName() + " is not a supported data type");
        return "";
    }

    public abstract HashMap<Class<?>, String> getDataTypes();

    public abstract HashMap<Class<?>, LambdaExecutor> getSerializeMap();

    public String formatQueryArgument(Object object) {
        if (object == null) {
            return "NULL";
        }
        Class<?> clazz = object.getClass();
        String output = null;
        if (serializeMap.get(clazz) != null) {
            output = (String) serializeMap.get(clazz).execute(object);
        }

        if (output == null) {
            HashMap<Class<?>, LambdaExecutor> additionalSerializeMap = getSerializeMap();

            if (additionalSerializeMap == null) {
                return object.toString();
            }
            if (additionalSerializeMap.get(clazz) != null) {
                output = (String) additionalSerializeMap.get(clazz).execute(object);
            }
        }

        if (output != null) {
            return output;
        }

        return object.toString();
    }

    public abstract HashMap<Class<?>, LambdaExecutor> getDeserializeMap();

    public Object getObject(Class<?> clazz, Object object) {
        Object output = null;
        if (deserializeMap.get(clazz) != null) {
            output = deserializeMap.get(clazz).execute(object);
        }
        if (output == null) {
            HashMap<Class<?>, LambdaExecutor> additionalDeserializeMap = getDeserializeMap();

            if (additionalDeserializeMap == null) {
                return object;
            }
            if (additionalDeserializeMap.get(clazz) != null) {
                output = additionalDeserializeMap.get(clazz).execute(object);
            }
        }

        if (output != null) {
            return output;
        }

        return object;
    }


}
