package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.PLogInvite;

public class LogInviteDAO extends ADao<PLogInvite> {

    protected LogInviteDAO(HikariDataSource datasource) {
        super(datasource);
    }

    @Override
    public boolean create(PLogInvite log) {
        String query = "INSERT INTO log_invite(guild_id, invite, guild) VALUES(?, ?, to_jsonb(?::JSON));";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, log.getId());
            pst.setBoolean(2, log.getInviteFlag());
            pst.setString(3, log.getGuildLog());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(PLogInvite log) {
        return false;
    }

    @Override
    public boolean update(PLogInvite log) {
        return false;
    }

    @Override
    public PLogInvite find(Long... guildLong) {
        return null;
    }

    public Set<PLogInvite> findLogs(boolean inviteFlag) {
        Set<PLogInvite> logs = null;
        String query = "SELECT guild_id, invite, guild FROM log_invite WHERE invite = ? ORDER BY log_date DESC LIMIT 3;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setBoolean(1, inviteFlag);
            ResultSet rs = pst.executeQuery();
            logs = new LinkedHashSet<>();
            while (rs.next()) {
                PLogInvite log = new PLogInvite();
                log.setId(rs.getLong("guild_id"));
                log.setInviteFlag(rs.getBoolean("invite"));
                log.setGuildLog(rs.getString("guild"));
                logs.add(log);
            }
        } catch (SQLException e) {
        }
        return logs;
    }

}