package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.PMember;

public class MemberDAO extends ADao<PMember> {

    private static final Logger LOG = LoggerFactory.getLogger(MemberDAO.class);

    protected MemberDAO(HikariDataSource datasource) {
        super(datasource);
    }

    @Override
    public boolean create(PMember pmember) {
        return false;
    }

    @Override
    public boolean delete(PMember pmember) {
        return false;
    }

    @Override
    public boolean update(PMember pmember) {
        String query = "UPDATE member SET name = ? WHERE member_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, pmember.getName());
            pst.setLong(2, pmember.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public PMember find(Long... memberLong) {
        PMember pmember = null;
        String query = "SELECT member_id, name FROM member WHERE member_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, memberLong[0]);
            ResultSet rs = pst.executeQuery();
            rs.next();
            pmember = new PMember();
            // If the user does not exists, no row is sent and an error occurs if we try to get a value from the
            // result set.
            // In that case, only the freshly instantiated member object is returned without any setters.
            if (rs.getRow() != 0) {
                pmember.setId(rs.getLong("member_id"));
                pmember.setName(rs.getString("name"));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return pmember;
    }

}