package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.PBan;

public class BanDAO extends ADao<PBan> {

    private static final Logger LOG = LoggerFactory.getLogger(BanDAO.class);

    protected BanDAO(HikariDataSource datasource) {
        super(datasource);
    }

    @Override
    public boolean create(PBan bMember) {
        String query = "INSERT INTO ban(guild_id, member_id, ban_date) VALUES(?, ?, ?);";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, bMember.getGuildId());
            pst.setLong(2, bMember.getId());
            pst.setTimestamp(3, bMember.getBanDate());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(PBan bMember) {
        String query = "DELETE FROM ban WHERE guild_id = ? AND member_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, bMember.getGuildId());
            pst.setLong(2, bMember.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean update(PBan bMember) {
        return false;
    }

    @Override
    public PBan find(Long... bMemberLong) {
        PBan bMember = null;
        String query = "SELECT guild_id, member_id, ban_date FROM ban WHERE guild_id = ? AND member_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, bMemberLong[0]);
            pst.setLong(2, bMemberLong[1]);
            ResultSet rs = pst.executeQuery();
            rs.next();
            bMember = new PBan();
            if (rs.getRow() != 0) {
                bMember.setGuildId(rs.getLong("guild_id"));
                bMember.setId(rs.getLong("member_id"));
                bMember.setBanDate(rs.getTimestamp("ban_date"));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return bMember;
    }

}