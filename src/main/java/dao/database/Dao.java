package dao.database;

import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

interface SQLCloseable extends AutoCloseable {
    @Override
    public void close() throws SQLException;
}

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