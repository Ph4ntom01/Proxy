package factory;

import com.zaxxer.hikari.HikariDataSource;

import dao.database.ChannelJoinDAO;
import dao.database.ChannelLeaveDAO;
import dao.database.DBService;
import dao.database.Dao;
import dao.database.GuildDAO;
import dao.database.MemberDAO;
import dao.database.PermissionDAO;
import dao.pojo.ChannelJoinPojo;
import dao.pojo.ChannelLeavePojo;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import dao.pojo.PermissionPojo;

public class DaoFactory {

    private static HikariDataSource dataSource = DBService.getInstance();

    private DaoFactory() {
    }

    public static Dao<GuildPojo> getGuildDAO() {
        return new GuildDAO(dataSource);
    }

    public static Dao<ChannelJoinPojo> getChannelJoinDAO() {
        return new ChannelJoinDAO(dataSource);
    }

    public static Dao<ChannelLeavePojo> getChannelLeaveDAO() {
        return new ChannelLeaveDAO(dataSource);
    }

    public static Dao<MemberPojo> getMemberDAO() {
        return new MemberDAO(dataSource);
    }

    public static Dao<PermissionPojo> getPermissionDAO() {
        return new PermissionDAO(dataSource);
    }

}