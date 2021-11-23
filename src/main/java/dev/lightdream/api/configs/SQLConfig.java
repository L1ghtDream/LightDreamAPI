package dev.lightdream.api.configs;

import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.UUID;

@SuppressWarnings("CanBeFinal")
@NoArgsConstructor
public class SQLConfig {

    public Driver driver = Driver.SQLITE;
    public String host = "localhost";
    public String database = "LightDreamAPI";
    public String username = "";
    public String password = "";
    public int port = 3306;
    public boolean useSSL = false;


    public enum Driver {
        MYSQL(
                "SELECT * FROM %table% WHERE %placeholder%",
                "SELECT * FROM %table% WHERE 1",
                "UPDATE %table% SET %placeholder% WHERE id=?",
                "INSERT INTO %table% (%placeholder-1%) VALUES(%placeholder-2%)",
                "CREATE TABLE IF NOT EXISTS %table% (%placeholder%, %keys%)",
                "DELETE FROM %table% WHERE id=?",
                new HashMap<Class<?>, String>() {{
                    put(int.class, "INT");
                    put(Integer.class, "INT");
                    put(String.class, "TEXT");
                    put(boolean.class, "BOOLEAN");
                    put(Boolean.class, "BOOLEAN");
                    put(float.class, "FLOAT");
                    put(Float.class, "FLOAT");
                    put(double.class, "DOUBLE");
                    put(Double.class, "DOUBLE");
                    put(UUID.class, "TEXT");
                    put(Long.class, "LONG");
                    put(long.class, "LONG");
                }},
                "AUTO_INCREMENT"
        ),
        MARIADB(MYSQL),
        SQLSERVER(MYSQL),
        POSTGRESQL(MYSQL),
        H2(MYSQL),
        SQLITE(
                "SELECT * FROM %table% WHERE %placeholder%",
                "SELECT * FROM %table% WHERE 1",
                "UPDATE %table% SET %placeholder% WHERE id=?",
                "INSERT INTO %table% (%placeholder-1%) VALUES(%placeholder-2%)",
                "CREATE TABLE IF NOT EXISTS %table% (%placeholder%)",
                "DELETE FROM %table% WHERE id=?",
                new HashMap<Class<?>, String>() {{
                    put(int.class, "INTEGER");
                    put(Integer.class, "INTEGER");
                    put(String.class, "TEXT");
                    put(boolean.class, "BOOLEAN");
                    put(Boolean.class, "BOOLEAN");
                    put(float.class, "REAL");
                    put(Float.class, "REAL");
                    put(double.class, "REAL");
                    put(Double.class, "REAL");
                    put(UUID.class, "TEXT");
                }},
                "PRIMARY KEY AUTOINCREMENT"
        );

        public String select;
        public String selectAll;
        public String update;
        public String insert;
        public String createTable;
        public String delete;
        public HashMap<Class<?>, String> dataTypes;
        public String autoIncrement;

        Driver(String select, String selectAll, String update, String insert, String createTable, String delete, HashMap<Class<?>, String> dataTypes, String autoIncrement) {
            this.select = select;
            this.selectAll = selectAll;
            this.update = update;
            this.insert = insert;
            this.createTable = createTable;
            this.delete = delete;
            this.dataTypes = dataTypes;
            this.autoIncrement = autoIncrement;
        }

        Driver(Driver driver) {
            this.select = driver.select;
            this.selectAll = driver.selectAll;
            this.update = driver.update;
            this.insert = driver.insert;
            this.createTable = driver.createTable;
            this.delete = driver.delete;
            this.dataTypes = driver.dataTypes;
            this.autoIncrement = driver.autoIncrement;
        }

    }
}
