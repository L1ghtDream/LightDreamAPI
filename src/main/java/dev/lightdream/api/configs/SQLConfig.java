package dev.lightdream.api.configs;

import lombok.NoArgsConstructor;

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

    @Override
    public String toString() {
        return "SQLConfig{" +
                "driver=" + driver +
                ", host='" + host + '\'' +
                ", database='" + database + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", port=" + port +
                ", useSSL=" + useSSL +
                '}';
    }

    public enum Driver {
        MYSQL(
                "SELECT * FROM ? WHERE %placeholder%",
                "SELECT * FROM ? WHERE 1",
                "UPDATE ? SET %placeholder% WHERE id=?",
                "INSERT INTO ? VALUES(%placeholder%)",
                "CREATE TABLE IF NOT EXISTS ? (%placeholder%)",
                "DELETE FROM ? WHERE id=?"
        ),
        MARIADB(MYSQL),
        SQLSERVER(MYSQL),
        POSTGRESQL(MYSQL),
        H2(MYSQL),
        SQLITE(MYSQL);

        public String select;
        public String selectAll;
        public String update;
        public String insert;
        public String createTable;
        public String delete;

        Driver(String select, String selectAll, String update, String insert, String createTable, String delete) {
            this.select = select;
            this.selectAll = selectAll;
            this.update = update;
            this.insert = insert;
            this.createTable = createTable;
            this.delete=delete;
        }

        Driver(Driver driver) {
            this.select = driver.select;
            this.selectAll = driver.selectAll;
            this.update = driver.update;
            this.insert = driver.insert;
            this.createTable = driver.createTable;
            this.delete=driver.delete;
        }

    }
}
