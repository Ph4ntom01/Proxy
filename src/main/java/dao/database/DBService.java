package dao.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import configuration.file.Config;
import factory.ConfigFactory;

public enum DBService {

    INSTANCE;

    private final HikariDataSource dataSource;

    private DBService() {
        Config conf = ConfigFactory.getConf();
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(conf.getString("database.className"));
        config.setUsername(conf.getString("database.username"));
        config.setPassword(conf.getString("database.password"));
        config.addDataSourceProperty("databaseName", conf.getString("database.databaseName"));
        config.addDataSourceProperty("portNumber", conf.getLong("database.port"));
        config.addDataSourceProperty("serverName", conf.getString("database.server"));
        config.addDataSourceProperty("ssl", conf.getBoolean("database.ssl"));
        config.addDataSourceProperty("sslmode", conf.getString("database.sslMode"));
        config.addDataSourceProperty("sslrootcert", conf.getString("database.ca"));
        dataSource = new HikariDataSource(config);
    }

    public HikariDataSource getDatasource() {
        return dataSource;
    }

}