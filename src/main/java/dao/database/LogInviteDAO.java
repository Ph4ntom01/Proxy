package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;

import configuration.constant.EInviteState;
import dao.pojo.PLogInvite;
import net.dv8tion.jda.api.utils.data.DataObject;

public class LogInviteDAO extends ADao<PLogInvite> {

    private static final Logger LOG = LoggerFactory.getLogger(LogInviteDAO.class);

    protected LogInviteDAO(HikariDataSource datasource) {
        super(datasource);
    }

    @Override
    public boolean create(PLogInvite log) {
        String query = "INSERT INTO log_invite(guild) VALUES(to_jsonb(?::JSON));";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, log.getGuildLog());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
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

    public Set<DataObject> findLogs(EInviteState state) {
        Set<DataObject> logs = null;
        String query = "SELECT guild FROM log_invite WHERE guild->>'state' = ? ORDER BY guild->>'date' DESC LIMIT 3;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, state.getName());
            ResultSet rs = pst.executeQuery();
            // LinkedHashSet for insertion order.
            logs = new LinkedHashSet<>();
            while (rs.next()) {
                logs.add(DataObject.fromJson(rs.getString("guild")));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return logs;
    }

}