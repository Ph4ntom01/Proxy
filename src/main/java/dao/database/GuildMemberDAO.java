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

import configuration.constant.EPermission;
import dao.pojo.PGuildMember;
import dao.pojo.PMember;

public class GuildMemberDAO extends ADao<PGuildMember> {

    private static final Logger LOG = LoggerFactory.getLogger(GuildMemberDAO.class);

    protected GuildMemberDAO(HikariDataSource datasource) {
        super(datasource);
    }

    @Override
    public boolean create(PGuildMember pguildMember) {
        String memberQuery = "INSERT INTO member(member_id, name) VALUES(?, ?);";
        String guildMemberQuery = "INSERT INTO guild_member(guild_id, member_id, permission_id) VALUES(?, ?, ?);";
        // @formatter:off
        try (Connection conn = datasource.getConnection();
             PreparedStatement createMember = conn.prepareStatement(memberQuery);
             PreparedStatement createGMember = conn.prepareStatement(guildMemberQuery);
             SQLCloseable finish = () -> { if (!conn.getAutoCommit()) conn.rollback(); };) {

            conn.setAutoCommit(false);
            ADao<PMember> memberDao = DaoFactory.getPMemberDAO();
            if (memberDao.find(pguildMember.getId()).isEmpty()) {
                createMember.setLong(1, pguildMember.getId());
                createMember.setString(2, pguildMember.getName());
                createMember.executeUpdate();
            }
            createGMember.setLong(1, pguildMember.getGuildId());
            createGMember.setLong(2, pguildMember.getId());
            createGMember.setInt(3, pguildMember.getPermission().getLevel());
            createGMember.executeUpdate();
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
    public boolean delete(PGuildMember pguildMember) {
        String gMemberQuery = "DELETE FROM guild_member WHERE guild_id = ? AND member_id = ?;";
        String orphansQuery = "CALL delete_orphan_members();";
        // @formatter:off
        try (Connection conn = datasource.getConnection();
             PreparedStatement deleteMembers = conn.prepareStatement(gMemberQuery);
             CallableStatement deleteOrphans = conn.prepareCall(orphansQuery);
             SQLCloseable finish = () -> { if(!conn.getAutoCommit()) conn.rollback(); };) {

            conn.setAutoCommit(false);
            deleteMembers.setLong(1, pguildMember.getGuildId());
            deleteMembers.setLong(2, pguildMember.getId());
            deleteMembers.executeUpdate();
            // Delete a member from the member table if his id is no more referenced into the GuildMember table.
            deleteOrphans.executeUpdate();
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
    public boolean update(PGuildMember pguildMember) {
        String query = "UPDATE guild_member SET permission_id = ? WHERE guild_id = ? AND member_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setInt(1, pguildMember.getPermission().getLevel());
            pst.setLong(2, pguildMember.getGuildId());
            pst.setLong(3, pguildMember.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
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
            LOG.error(e.getMessage());
        }
        return gMember;
    }

    public Set<PGuildMember> findPGuildMembersByPerm(Long guildId, EPermission perm) {
        Set<PGuildMember> pguildMembers = null;
        String query = "SELECT guild_id, member_id, permission_id FROM guild_member WHERE guild_id = ? AND permission_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, guildId);
            pst.setInt(2, perm.getLevel());
            ResultSet rs = pst.executeQuery();
            pguildMembers = new HashSet<>();
            while (rs.next()) {
                PGuildMember pguildMember = new PGuildMember();
                pguildMember.setGuildId(rs.getLong("guild_id"));
                pguildMember.setId(rs.getLong("member_id"));
                pguildMember.setPermission(EPermission.getPermission(rs.getInt("permission_id")));
                pguildMembers.add(pguildMember);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return pguildMembers;
    }

}