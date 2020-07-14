package dao.database;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.PermissionPojo;

public class PermissionDAO extends Dao<PermissionPojo> {

    public PermissionDAO(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean create(PermissionPojo obj) {
        return false;
    }

    @Override
    public boolean delete(PermissionPojo obj) {
        return false;
    }

    @Override
    public boolean update(PermissionPojo obj) {
        return false;
    }

    @Override
    public PermissionPojo find(String id) {
        return null;
    }

}