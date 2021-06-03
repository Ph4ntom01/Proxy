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
    public boolean create(PGuild guild) {
        String query = "INSERT INTO guild(guild_id, name) VALUES(?, ?);";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, guild.getId());
            pst.setString(2, guild.getName());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(PGuild guild) {
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
            deleteMembers.setLong(1, guild.getId());
            deleteMembers.executeUpdate();
            // Delete members from the member table if their id's are no more referenced into the GuildMember table.
            deleteOrphans.executeUpdate();
            // Delete the banned members from the guild.
            deleteBans.setLong(1, guild.getId());
            deleteBans.executeUpdate();
            // Delete the guild.
            deleteGuild.setLong(1, guild.getId());
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
    public boolean update(PGuild guild) {
        String query = "UPDATE guild SET name = ?, join_channel_id = ?, leave_channel_id = ?, control_channel_id = ?, default_role_id = ?, prefix = ?, shield = ? WHERE guild_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, guild.getName());
            setNLong(2, pst, guild.getJoinChannel());
            setNLong(3, pst, guild.getLeaveChannel());
            setNLong(4, pst, guild.getControlChannel());
            setNLong(5, pst, guild.getDefaultRole());
            pst.setString(6, guild.getPrefix());
            pst.setInt(7, guild.getShield());
            pst.setLong(8, guild.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public PGuild find(Long... guildLong) {
        PGuild guild = null;
        String query = "SELECT guild_id, name, join_channel_id, leave_channel_id, control_channel_id, default_role_id, prefix, shield FROM guild WHERE guild_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, guildLong[0]);
            ResultSet rs = pst.executeQuery();
            rs.next();
            guild = new PGuild();
            guild.setId(rs.getLong("guild_id"));
            guild.setName(rs.getString("name"));
            guild.setJoinChannel(rsGetLong(rs, "join_channel_id"));
            guild.setLeaveChannel(rsGetLong(rs, "leave_channel_id"));
            guild.setControlChannel(rsGetLong(rs, "control_channel_id"));
            guild.setDefaultRole(rsGetLong(rs, "default_role_id"));
            guild.setPrefix(rs.getString("prefix"));
            guild.setShield(rs.getInt("shield"));
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return guild;
    }

    public Set<PGuild> findGuilds() {
        Set<PGuild> guilds = null;
        String query = "SELECT guild_id, name, join_channel_id, leave_channel_id, control_channel_id, default_role_id, prefix, shield FROM guild;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            ResultSet rs = pst.executeQuery();
            guilds = new HashSet<>();
            while (rs.next()) {
                PGuild guild = new PGuild();
                guild.setId(rs.getLong("guild_id"));
                guild.setName(rs.getString("name"));
                guild.setJoinChannel(rsGetLong(rs, "join_channel_id"));
                guild.setLeaveChannel(rsGetLong(rs, "leave_channel_id"));
                guild.setControlChannel(rsGetLong(rs, "control_channel_id"));
                guild.setDefaultRole(rsGetLong(rs, "default_role_id"));
                guild.setPrefix(rs.getString("prefix"));
                guild.setShield(rs.getInt("shield"));
                guilds.add(guild);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return guilds;
    }

}