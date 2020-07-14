package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.MemberPojo;
import factory.PojoFactory;

public class MemberDAO extends Dao<MemberPojo> {

    public MemberDAO(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean create(MemberPojo member) {
        String query = "INSERT INTO `members`(`guild_id`, `member_id`, `member_name`, `permission_level`) VALUES(?, ?, ?, ?);";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, member.getGuildId());
                pst.setString(2, member.getId());
                pst.setString(3, member.getName());
                pst.setInt(4, member.getPermLevel());
                pst.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(MemberPojo member) {
        String query = "DELETE FROM `members` WHERE `guild_id` = ? AND `member_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, member.getGuildId());
                pst.setString(2, member.getId());
                pst.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean update(MemberPojo member) {
        String query = "UPDATE `members` SET `member_name` = ?, `permission_level` = ? WHERE `guild_id` = ? AND `member_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, member.getName());
                pst.setInt(2, member.getPermLevel());
                pst.setString(3, member.getGuildId());
                pst.setString(4, member.getId());
                pst.executeUpdate();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public MemberPojo find(String user) {
        MemberPojo member = null;
        String query = "SELECT `guild_id`, `member_id`, `member_name`, `permission_level` FROM `members` WHERE `guild_id` = ? AND `member_id` = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            String[] values = user.split("#");
            pst.setString(1, values[0]);
            pst.setString(2, values[1]);
            try (ResultSet rs = pst.executeQuery();) {
                rs.next();
                member = PojoFactory.getMember();
                member.setGuildId(rs.getString("guild_id"));
                member.setId(rs.getString("member_id"));
                member.setName(rs.getString("member_name"));
                member.setPermLevel(rs.getInt("permission_level"));
            }
        } catch (SQLException e) {
        }
        return member;
    }

}