package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.ChannelLeavePojo;
import factory.PojoFactory;

public class ChannelLeaveDAO extends Dao<ChannelLeavePojo> {

    public ChannelLeaveDAO(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean create(ChannelLeavePojo channelLeave) {
        String query = "INSERT INTO `channels_leave`(`channel_id`) VALUES(?);";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, channelLeave.getChannelId());
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
    public boolean delete(ChannelLeavePojo channelLeave) {
        String query = "DELETE FROM `channels_leave` WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, channelLeave.getChannelId());
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
    public boolean update(ChannelLeavePojo channelLeave) {
        String query = "UPDATE `channels_leave` SET `message` = ?, `embed` = ? WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, channelLeave.getMessage());
                pst.setBoolean(2, channelLeave.getEmbed());
                pst.setString(3, channelLeave.getChannelId());
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

    public boolean update(ChannelLeavePojo channelLeave, String channelId) {
        String query = "UPDATE `channels_leave` SET `channel_id` = ?, `message` = ?, `embed` = ? WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, channelId);
                pst.setString(2, channelLeave.getMessage());
                pst.setBoolean(3, channelLeave.getEmbed());
                pst.setString(4, channelLeave.getChannelId());
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
    public ChannelLeavePojo find(String channelId) {
        ChannelLeavePojo channelLeave = null;
        String query = "SELECT `channel_id`, `message`, `embed` FROM `channels_leave` WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, channelId);
            try (ResultSet rs = pst.executeQuery();) {
                rs.next();
                channelLeave = PojoFactory.getChannelLeave();
                channelLeave.setChannelId(rs.getString("channel_id"));
                channelLeave.setMessage(rs.getString("message"));
                channelLeave.setEmbed(rs.getBoolean("embed"));
            }
        } catch (SQLException e) {
        }
        return channelLeave;
    }

}