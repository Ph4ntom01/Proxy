package dao.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.zaxxer.hikari.HikariDataSource;

import configuration.constant.EPermission;
import dao.pojo.PGuildMember;
import dao.pojo.PMember;

public class GuildMemberDAO extends ADao<PGuildMember> {

    protected GuildMemberDAO(HikariDataSource datasource) {
        super(datasource);
    }

    @Override
    public boolean create(PGuildMember gMember) {
        String memberQuery = "INSERT INTO member(member_id, name) VALUES(?, ?);";
        String gMemberQuery = "INSERT INTO guild_member(guild_id, member_id, permission_id) VALUES(?, ?, ?);";
        // @formatter:off
        try (Connection conn = datasource.getConnection();
             PreparedStatement createMember = conn.prepareStatement(memberQuery);
             PreparedStatement createGMember = conn.prepareStatement(gMemberQuery);
             SQLCloseable finish = () -> { if (!conn.getAutoCommit()) conn.rollback(); };) {

            conn.setAutoCommit(false);
            ADao<PMember> memberDao = DaoFactory.getMemberDAO();
            if (memberDao.find(gMember.getId()).isEmpty()) {
                createMember.setLong(1, gMember.getId());
                createMember.setString(2, gMember.getName());
                createMember.executeUpdate();
            }
            createGMember.setLong(1, gMember.getGuildId());
            createGMember.setLong(2, gMember.getId());
            createGMember.setInt(3, gMember.getPermission().getLevel());
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
        try (Connection conn = datasource.getConnection();
             PreparedStatement deleteMembers = conn.prepareStatement(gMemberQuery);
             CallableStatement deleteOrphans = conn.prepareCall(orphansQuery);
             SQLCloseable finish = () -> { if(!conn.getAutoCommit()) conn.rollback(); };) {

            conn.setAutoCommit(false);
            deleteMembers.setLong(1, gMember.getGuildId());
            deleteMembers.setLong(2, gMember.getId());
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
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setInt(1, gMember.getPermission().getLevel());
            pst.setLong(2, gMember.getGuildId());
            pst.setLong(3, gMember.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public PGuildMember find(Long... userLong) {
        PGuildMember gMember = null;
        String query = "SELECT guild_id, member_id, permission_id FROM guild_member WHERE guild_id = ? AND member_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, userLong[0]);
            pst.setLong(2, userLong[1]);
            ResultSet rs = pst.executeQuery();
            rs.next();
            gMember = new PGuildMember();
            // If the user does not exists, no row is sent and an error occurs if we try to get a value from the
            // result set.
            // In that case, only the freshly instantiated member object is returned without any setters.
            if (rs.getRow() != 0) {
                gMember.setGuildId(rs.getLong("guild_id"));
                gMember.setId(rs.getLong("member_id"));
                gMember.setPermission(EPermission.getPermission(rs.getInt("permission_id")));
            }
        } catch (SQLException e) {
        }
        return gMember;
    }

    public Set<PGuildMember> findMembersByPerm(Long guildId, EPermission perm) {
        Set<PGuildMember> gMembers = null;
        String query = "SELECT guild_id, member_id, permission_id FROM guild_member WHERE guild_id = ? AND permission_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, guildId);
            pst.setInt(2, perm.getLevel());
            ResultSet rs = pst.executeQuery();
            gMembers = new HashSet<>();
            while (rs.next()) {
                PGuildMember gMember = new PGuildMember();
                gMember.setGuildId(rs.getLong("guild_id"));
                gMember.setId(rs.getLong("member_id"));
                gMember.setPermission(EPermission.getPermission(rs.getInt("permission_id")));
                gMembers.add(gMember);
            }
        } catch (SQLException e) {
        }
        return gMembers;
    }

}