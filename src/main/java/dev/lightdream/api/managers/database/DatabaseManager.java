package dev.lightdream.api.managers.database;

import dev.lightdream.api.IAPI;
import dev.lightdream.api.configs.SQLConfig;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Field;

public abstract class DatabaseManager implements IDatabaseManager {

    public IAPI api;
    public SQLConfig sqlConfig;

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

    public String getDataType(Field field) {
        String name = field.getType().getSimpleName();
        switch (name) {
            case "int":
            case "Integer":
                return "INT";
            case "String":
                return "TEXT";
            case "boolean":
            case "Boolean":
                return "BOOLEAN";
            case "float":
            case "Float":
                return "FLOAT";
            case "double":
            case "Double":
                return "DOUBLE";
            default:
                api.getLogger().severe("Datatype not recognised");
                return "";
        }
    }

    public String formatQueryArgument(Object object) {
        String name = object.getClass().getSimpleName();
        switch (name) {
            case "int":
            case "Integer":
            case "boolean":
            case "Boolean":
            case "float":
            case "Float":
            case "double":
            case "Double":
                return object.toString();
            case "String":
                return "\"" + object + "\"";
            default:
                api.getLogger().severe("Datatype not recognised");
                return "\"\"";
        }
    }
}
