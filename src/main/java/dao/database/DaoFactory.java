package dao.database;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.PBan;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import dao.pojo.PLogInvite;
import dao.pojo.PMember;

public class DaoFactory {

    private static HikariDataSource datasource = EDBService.INSTANCE.getDatasource();

    private DaoFactory() {}

    public static ADao<PGuild> getPGuildDAO() {
        return new GuildDAO(datasource);
    }

    public static ADao<PJoinChannel> getJoinChannelDAO() {
        return new JoinChannelDAO(datasource);
    }

    public static ADao<PLeaveChannel> getLeaveChannelDAO() {
        return new LeaveChannelDAO(datasource);
    }

    public static ADao<PGuildMember> getPGuildMemberDAO() {
        return new GuildMemberDAO(datasource);
    }

    public static ADao<PMember> getPMemberDAO() {
        return new MemberDAO(datasource);
    }

    public static ADao<PBan> getBanDAO() {
        return new BanDAO(datasource);
    }

    public static ADao<PLogInvite> getLogInviteDAO() {
        return new LogInviteDAO(datasource);
    }

}