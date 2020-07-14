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
        dataSource.setJdbcUrl(conf.getValue("jdbcUrl"));
        dataSource.setUsername(conf.getValue("username"));
        dataSource.setPassword(conf.getValue("password"));
        dataSource.addDataSourceProperty("cachePrepStmts", "true");
        dataSource.addDataSourceProperty("prepStmtCacheSize", "250");
        dataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        dataSource.addDataSourceProperty("useServerPrepStmts", "true");
        dataSource.addDataSourceProperty("useLocalSessionState", "true");
        dataSource.addDataSourceProperty("rewriteBatchedStatements", "true");
        dataSource.addDataSourceProperty("cacheResultSetMetadata", "true");
        dataSource.addDataSourceProperty("cacheServerConfiguration", "true");
        dataSource.addDataSourceProperty("elideSetAutoCommits", "true");
        dataSource.addDataSourceProperty("maintainTimeStats", "false");
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