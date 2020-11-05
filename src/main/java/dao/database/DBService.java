package dao.database;

import com.zaxxer.hikari.HikariDataSource;

import configuration.files.Config;
import factory.ConfigFactory;

public class DBService {

    private static volatile DBService instance;
    private static HikariDataSource dataSource;

    private DBService() {
        Config conf = ConfigFactory.datasource();
        dataSource = new HikariDataSource();
        dataSource.setDriverClassName("org.mariadb.jdbc.MariaDbDataSource");
        dataSource.setJdbcUrl(conf.getValue("jdbcUrl"));
        dataSource.setUsername(conf.getValue("username"));
        dataSource.setPassword(conf.getValue("password"));
        dataSource.addDataSourceProperty("useServerPrepStmts", "true");
    }

    public static HikariDataSource getInstance() {
        if (instance == null) {
            synchronized (DBService.class) {
                if (instance == null) {
                    instance = new DBService();
                }
            }
        }
        return dataSource;
    }

}