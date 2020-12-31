package dao.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import configuration.file.Config;
import factory.ConfigFactory;

public enum DBService {

    INSTANCE;

    private final HikariDataSource dataSource;

    private DBService() {
        Config conf = ConfigFactory.getDatasource();
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(conf.getValue("className"));
        config.setUsername(conf.getValue("username"));
        config.setPassword(conf.getValue("password"));
        config.addDataSourceProperty("databaseName", conf.getValue("database"));
        config.addDataSourceProperty("portNumber", conf.getValue("port"));
        config.addDataSourceProperty("serverName", conf.getValue("server"));
        config.addDataSourceProperty("ssl", conf.getValue("ssl"));
        config.addDataSourceProperty("sslmode", conf.getValue("sslmode"));
        config.addDataSourceProperty("sslrootcert", conf.getValue("ca"));
        dataSource = new HikariDataSource(config);
    }

    public HikariDataSource getDatasource() {
        return dataSource;
    }

}