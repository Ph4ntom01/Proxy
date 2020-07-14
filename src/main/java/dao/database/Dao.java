package dao.database;

import com.zaxxer.hikari.HikariDataSource;

public abstract class Dao<T> {

    protected HikariDataSource dataSource = null;

    protected Dao(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public abstract boolean create(T obj);

    public abstract boolean delete(T obj);

    public abstract boolean update(T obj);

    public abstract T find(String id);

}