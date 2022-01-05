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
    public boolean create(PBan bPmember) {
        String query = "INSERT INTO ban(guild_id, member_id, ban_date) VALUES(?, ?, ?);";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, bPmember.getGuildId());
            pst.setLong(2, bPmember.getId());
            pst.setTimestamp(3, bPmember.getBanDate());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(PBan bPmember) {
        String query = "DELETE FROM ban WHERE guild_id = ? AND member_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, bPmember.getGuildId());
            pst.setLong(2, bPmember.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean update(PBan bPmember) {
        return false;
    }

    @Override
    public PBan find(Long... bPmemberLong) {
        PBan bPmember = null;
        String query = "SELECT guild_id, member_id, ban_date FROM ban WHERE guild_id = ? AND member_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, bPmemberLong[0]);
            pst.setLong(2, bPmemberLong[1]);
            ResultSet rs = pst.executeQuery();
            rs.next();
            bPmember = new PBan();
            if (rs.getRow() != 0) {
                bPmember.setGuildId(rs.getLong("guild_id"));
                bPmember.setId(rs.getLong("member_id"));
                bPmember.setBanDate(rs.getTimestamp("ban_date"));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return bPmember;
    }

}