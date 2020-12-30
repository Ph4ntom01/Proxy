package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.PMember;
import factory.PojoFactory;

public class MemberDAO extends Dao<PMember> {

    public MemberDAO(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean create(PMember member) {
        return false;
    }

    @Override
    public boolean delete(PMember member) {
        return false;
    }

    @Override
    public boolean update(PMember member) {
        String query = "UPDATE member SET name = ? WHERE member_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, member.getName());
            pst.setString(2, member.getId());
            pst.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public PMember find(String memberId) {
        PMember member = null;
        String query = "SELECT member_id, name FROM member WHERE member_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, memberId);
            ResultSet rs = pst.executeQuery();
            rs.next();
            member = PojoFactory.getMember();
            member.setId(rs.getString("member_id"));
            member.setName(rs.getString("name"));
        } catch (SQLException e) {
        }
        return member;
    }

}