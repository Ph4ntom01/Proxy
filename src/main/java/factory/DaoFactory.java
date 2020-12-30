package factory;

import com.zaxxer.hikari.HikariDataSource;

import dao.database.DBService;
import dao.database.Dao;
import dao.database.GuildDAO;
import dao.database.GuildMemberDAO;
import dao.database.JoinChannelDAO;
import dao.database.LeaveChannelDAO;
import dao.database.MemberDAO;
import dao.pojo.PGuildMember;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import dao.pojo.PMember;

public class DaoFactory {

    private static HikariDataSource dataSource = DBService.INSTANCE.getDatasource();

    private DaoFactory() {
    }

    public static Dao<PGuild> getGuildDAO() {
        return new GuildDAO(dataSource);
    }

    public static Dao<PJoinChannel> getJoinChannelDAO() {
        return new JoinChannelDAO(dataSource);
    }

    public static Dao<PLeaveChannel> getLeaveChannelDAO() {
        return new LeaveChannelDAO(dataSource);
    }

    public static Dao<PGuildMember> getGuildMemberDAO() {
        return new GuildMemberDAO(dataSource);
    }

    public static Dao<PMember> getMemberDAO() {
        return new MemberDAO(dataSource);
    }

}