package dao.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.PGuild;

public class GuildDAO extends ADao<PGuild> {

    private static final Logger LOG = LoggerFactory.getLogger(GuildDAO.class);

    protected GuildDAO(HikariDataSource datasource) {
        super(datasource);
    }

    @Override
    public boolean create(PGuild pguild) {
        String query = "INSERT INTO guild(guild_id, name) VALUES(?, ?);";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, pguild.getId());
            pst.setString(2, pguild.getName());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(PGuild pguild) {
        String gMemberQuery = "DELETE FROM guild_member WHERE guild_id = ?;";
        String orphanQuery = "CALL delete_orphan_members();";
        String banQuery = "DELETE FROM ban WHERE guild_id = ?;";
        String guildQuery = "DELETE FROM guild WHERE guild_id = ?;";
        // @formatter:off
        try (Connection conn = datasource.getConnection();
             PreparedStatement deleteMembers = conn.prepareStatement(gMemberQuery);
             CallableStatement deleteOrphans = conn.prepareCall(orphanQuery);
             PreparedStatement deleteBans = conn.prepareStatement(banQuery);
             PreparedStatement deleteGuild = conn.prepareStatement(guildQuery);
             SQLCloseable finish = () -> { if(!conn.getAutoCommit()) conn.rollback(); };) {
            
            conn.setAutoCommit(false);
            // Delete members from the GuildMember table.
            deleteMembers.setLong(1, pguild.getId());
            deleteMembers.executeUpdate();
            // Delete members from the member table if their id's are no more referenced into the GuildMember table.
            deleteOrphans.executeUpdate();
            // Delete the banned members from the guild.
            deleteBans.setLong(1, pguild.getId());
            deleteBans.executeUpdate();
            // Delete the guild.
            deleteGuild.setLong(1, pguild.getId());
            deleteGuild.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            // @formatter:on
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean update(PGuild pguild) {
        String query = "UPDATE guild SET name = ?, join_channel_id = ?, leave_channel_id = ?, control_channel_id = ?, default_role_id = ?, prefix = ?, shield = ? WHERE guild_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, pguild.getName());
            setNLong(2, pst, pguild.getJoinChannel());
            setNLong(3, pst, pguild.getLeaveChannel());
            setNLong(4, pst, pguild.getControlChannel());
            setNLong(5, pst, pguild.getDefaultRole());
            pst.setString(6, pguild.getPrefix());
            pst.setInt(7, pguild.getShield());
            pst.setLong(8, pguild.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public PGuild find(Long... guildLong) {
        PGuild pguild = null;
        String query = "SELECT guild_id, name, join_channel_id, leave_channel_id, control_channel_id, default_role_id, prefix, shield FROM guild WHERE guild_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, guildLong[0]);
            ResultSet rs = pst.executeQuery();
            rs.next();
            pguild = new PGuild();
            pguild.setId(rs.getLong("guild_id"));
            pguild.setName(rs.getString("name"));
            pguild.setJoinChannel(rsGetLong(rs, "join_channel_id"));
            pguild.setLeaveChannel(rsGetLong(rs, "leave_channel_id"));
            pguild.setControlChannel(rsGetLong(rs, "control_channel_id"));
            pguild.setDefaultRole(rsGetLong(rs, "default_role_id"));
            pguild.setPrefix(rs.getString("prefix"));
            pguild.setShield(rs.getInt("shield"));
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return pguild;
    }

    public Set<PGuild> findPGuilds() {
        Set<PGuild> pguilds = null;
        String query = "SELECT guild_id, name, join_channel_id, leave_channel_id, control_channel_id, default_role_id, prefix, shield FROM guild;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            ResultSet rs = pst.executeQuery();
            pguilds = new HashSet<>();
            while (rs.next()) {
                PGuild pguild = new PGuild();
                pguild.setId(rs.getLong("guild_id"));
                pguild.setName(rs.getString("name"));
                pguild.setJoinChannel(rsGetLong(rs, "join_channel_id"));
                pguild.setLeaveChannel(rsGetLong(rs, "leave_channel_id"));
                pguild.setControlChannel(rsGetLong(rs, "control_channel_id"));
                pguild.setDefaultRole(rsGetLong(rs, "default_role_id"));
                pguild.setPrefix(rs.getString("prefix"));
                pguild.setShield(rs.getInt("shield"));
                pguilds.add(pguild);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return pguilds;
    }

}