package factory;

import com.zaxxer.hikari.HikariDataSource;

import dao.database.DBService;
import dao.database.Dao;
import dao.database.GuildDAO;
import dao.database.JoinChannelDAO;
import dao.database.LeaveChannelDAO;
import dao.database.MemberDAO;
import dao.database.PermissionDAO;
import dao.pojo.GuildPojo;
import dao.pojo.JoinChannelPojo;
import dao.pojo.LeaveChannelPojo;
import dao.pojo.MemberPojo;
import dao.pojo.PermissionPojo;

public class DaoFactory {

    private static HikariDataSource dataSource = DBService.getInstance();

    private DaoFactory() {
    }

    public static Dao<GuildPojo> getGuildDAO() {
        return new GuildDAO(dataSource);
    }

    public static Dao<JoinChannelPojo> getJoinChannelDAO() {
        return new JoinChannelDAO(dataSource);
    }

    public static Dao<LeaveChannelPojo> getLeaveChannelDAO() {
        return new LeaveChannelDAO(dataSource);
    }

    public static Dao<MemberPojo> getMemberDAO() {
        return new MemberDAO(dataSource);
    }

    public static Dao<PermissionPojo> getPermissionDAO() {
        return new PermissionDAO(dataSource);
    }

}