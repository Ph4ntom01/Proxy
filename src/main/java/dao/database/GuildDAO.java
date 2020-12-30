package dao.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.PGuild;
import factory.PojoFactory;

public class GuildDAO extends Dao<PGuild> {

    public GuildDAO(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean create(PGuild guild) {
        String guildQuery = "INSERT INTO guild(guild_id, name) VALUES(?, ?);";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement createGuild = conn.prepareStatement(guildQuery);) {
            createGuild.setString(1, guild.getId());
            createGuild.setString(2, guild.getName());
            createGuild.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(PGuild guild) {
        String gMemberQuery = "DELETE FROM guild_member WHERE guild_id = ?;";
        String orphanQuery = "CALL delete_orphan_members();";
        String guildQuery = "DELETE FROM guild WHERE guild_id = ?;";
        // @formatter:off
        try (Connection conn = this.dataSource.getConnection();
             PreparedStatement deleteMembers = conn.prepareStatement(gMemberQuery);
             CallableStatement deleteOrphans = conn.prepareCall(orphanQuery);
             PreparedStatement deleteGuild = conn.prepareStatement(guildQuery);
             SQLCloseable finish = () -> { if(!conn.getAutoCommit()) conn.rollback(); };) {
            
            conn.setAutoCommit(false);
            deleteMembers.setString(1, guild.getId());
            deleteMembers.executeUpdate();
            // Delete members from the member table if their id are no more referenced into the GuildMember table.
            deleteOrphans.executeUpdate();
            deleteGuild.setString(1, guild.getId());
            deleteGuild.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            // @formatter:on
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean update(PGuild guild) {
        String query = "UPDATE guild SET name = ?, join_channel_id = ?, leave_channel_id = ?, control_channel_id = ?, default_role_id = ?, prefix = ?, shield = ? WHERE guild_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, guild.getName());
            pst.setString(2, guild.getJoinChannel());
            pst.setString(3, guild.getLeaveChannel());
            pst.setString(4, guild.getControlChannel());
            pst.setString(5, guild.getDefaultRole());
            pst.setString(6, guild.getPrefix());
            pst.setInt(7, guild.getShield());
            pst.setString(8, guild.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public PGuild find(String guildId) {
        PGuild guild = null;
        String query = "SELECT guild_id, name, join_channel_id, leave_channel_id, control_channel_id, default_role_id, prefix, shield FROM guild WHERE guild_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, guildId);
            ResultSet rs = pst.executeQuery();
            rs.next();
            guild = PojoFactory.getGuild();
            guild.setId(rs.getString("guild_id"));
            guild.setName(rs.getString("name"));
            guild.setJoinChannel(rs.getString("join_channel_id"));
            guild.setLeaveChannel(rs.getString("leave_channel_id"));
            guild.setControlChannel(rs.getString("control_channel_id"));
            guild.setDefaultRole(rs.getString("default_role_id"));
            guild.setPrefix(rs.getString("prefix"));
            guild.setShield(rs.getInt("shield"));
        } catch (SQLException e) {
        }
        return guild;
    }

    public Set<PGuild> findGuilds() {
        Set<PGuild> guilds = null;
        String query = "SELECT guild_id, name, join_channel_id, leave_channel_id, control_channel_id, default_role_id, prefix, shield FROM guild;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            ResultSet rs = pst.executeQuery();
            guilds = new HashSet<>();
            while (rs.next()) {
                PGuild guild = PojoFactory.getGuild();
                guild.setId(rs.getString("guild_id"));
                guild.setName(rs.getString("name"));
                guild.setJoinChannel(rs.getString("join_channel_id"));
                guild.setLeaveChannel(rs.getString("leave_channel_id"));
                guild.setControlChannel(rs.getString("control_channel_id"));
                guild.setDefaultRole(rs.getString("default_role_id"));
                guild.setPrefix(rs.getString("prefix"));
                guild.setShield(rs.getInt("shield"));
                guilds.add(guild);
            }
        } catch (SQLException e) {
        }
        return guilds;
    }

}