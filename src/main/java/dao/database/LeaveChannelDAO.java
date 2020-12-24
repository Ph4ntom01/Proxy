package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.LeaveChannelPojo;
import factory.PojoFactory;

public class LeaveChannelDAO extends Dao<LeaveChannelPojo> {

    public LeaveChannelDAO(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean create(LeaveChannelPojo leaveChannel) {
        String query = "INSERT INTO `channels_leave`(`channel_id`) VALUES(?);";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, leaveChannel.getChannelId());
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
    public boolean delete(LeaveChannelPojo leaveChannel) {
        String query = "DELETE FROM `channels_leave` WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, leaveChannel.getChannelId());
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
    public boolean update(LeaveChannelPojo leaveChannel) {
        String query = "UPDATE `channels_leave` SET `message` = ?, `embed` = ? WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, leaveChannel.getMessage());
                pst.setBoolean(2, leaveChannel.getEmbed());
                pst.setString(3, leaveChannel.getChannelId());
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

    public boolean update(LeaveChannelPojo leaveChannel, String channelId) {
        String query = "UPDATE `channels_leave` SET `channel_id` = ?, `message` = ?, `embed` = ? WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, channelId);
                pst.setString(2, leaveChannel.getMessage());
                pst.setBoolean(3, leaveChannel.getEmbed());
                pst.setString(4, leaveChannel.getChannelId());
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
    public LeaveChannelPojo find(String channelId) {
        LeaveChannelPojo leaveChannel = null;
        String query = "SELECT `channel_id`, `message`, `embed` FROM `channels_leave` WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, channelId);
            try (ResultSet rs = pst.executeQuery();) {
                rs.next();
                leaveChannel = PojoFactory.getLeaveChannel();
                leaveChannel.setChannelId(rs.getString("channel_id"));
                leaveChannel.setMessage(rs.getString("message"));
                leaveChannel.setEmbed(rs.getBoolean("embed"));
            }
        } catch (SQLException e) {
        }
        return leaveChannel;
    }

}