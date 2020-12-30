package dao.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.zaxxer.hikari.HikariDataSource;

import configuration.constant.Permissions;
import dao.pojo.PGuildMember;
import dao.pojo.PMember;
import factory.DaoFactory;
import factory.PojoFactory;

public class GuildMemberDAO extends Dao<PGuildMember> {

    public GuildMemberDAO(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean create(PGuildMember gMember) {
        String memberQuery = "INSERT INTO member(member_id, name) VALUES(?, ?);";
        String gMemberQuery = "INSERT INTO guild_member(guild_id, member_id, permission_id) VALUES(?, ?, ?);";
        // @formatter:off
        try (Connection conn = this.dataSource.getConnection();
             PreparedStatement createMember = conn.prepareStatement(memberQuery);
             PreparedStatement createGMember = conn.prepareStatement(gMemberQuery);
             SQLCloseable finish = () -> { if (!conn.getAutoCommit()) conn.rollback(); };) {

            conn.setAutoCommit(false);
            Dao<PMember> memberDao = DaoFactory.getMemberDAO();
            if (memberDao.find(gMember.getId()).isEmpty()) {
                createMember.setString(1, gMember.getId());
                createMember.setString(2, gMember.getName());
                createMember.executeUpdate();
            }
            createGMember.setString(1, gMember.getGuildId());
            createGMember.setString(2, gMember.getId());
            createGMember.setInt(3, gMember.getPermId());
            createGMember.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            // @formatter:on
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(PGuildMember gMember) {
        String gMemberQuery = "DELETE FROM guild_member WHERE guild_id = ? AND member_id = ?;";
        String orphansQuery = "CALL delete_orphan_members();";
        // @formatter:off
        try (Connection conn = this.dataSource.getConnection();
             PreparedStatement deleteMembers = conn.prepareStatement(gMemberQuery);
             CallableStatement deleteOrphans = conn.prepareCall(orphansQuery);
             SQLCloseable finish = () -> { if(!conn.getAutoCommit()) conn.rollback(); };) {

            conn.setAutoCommit(false);
            deleteMembers.setString(1, gMember.getGuildId());
            deleteMembers.setString(2, gMember.getId());
            deleteMembers.executeUpdate();
            // Delete a member from the member table if his id is no more referenced into the GuildMember table.
            deleteOrphans.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
            // @formatter:on
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean update(PGuildMember gMember) {
        String query = "UPDATE guild_member SET permission_id = ? WHERE guild_id = ? AND member_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setInt(1, gMember.getPermId());
            pst.setString(2, gMember.getGuildId());
            pst.setString(3, gMember.getId());
            pst.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public PGuildMember find(String user) {
        PGuildMember gMember = null;
        String query = "SELECT guild_id, member_id, permission_id FROM guild_member WHERE guild_id = ? AND member_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            String[] values = user.split("#");
            pst.setString(1, values[0]);
            pst.setString(2, values[1]);
            ResultSet rs = pst.executeQuery();
            rs.next();
            gMember = PojoFactory.getGuildMember();
            gMember.setGuildId(rs.getString("guild_id"));
            gMember.setId(rs.getString("member_id"));
            gMember.setPermId(rs.getInt("permission_id"));
        } catch (SQLException e) {
        }
        return gMember;
    }

    public Set<PGuildMember> findMembersByPerm(String guildId, Permissions perm) {
        Set<PGuildMember> gMembers = null;
        String query = "SELECT guild_id, member_id, permission_id FROM guild_member WHERE guild_id = ? AND permission_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, guildId);
            pst.setInt(2, perm.getLevel());
            ResultSet rs = pst.executeQuery();
            gMembers = new HashSet<>();
            while (rs.next()) {
                PGuildMember gMember = PojoFactory.getGuildMember();
                gMember.setGuildId(rs.getString("guild_id"));
                gMember.setId(rs.getString("member_id"));
                gMember.setPermId(rs.getInt("permission_id"));
                gMembers.add(gMember);
            }
        } catch (SQLException e) {
        }
        return gMembers;
    }

}