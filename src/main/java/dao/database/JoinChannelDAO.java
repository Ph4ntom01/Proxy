package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.JoinChannelPojo;
import factory.PojoFactory;

public class JoinChannelDAO extends Dao<JoinChannelPojo> {

    public JoinChannelDAO(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean create(JoinChannelPojo joinChannel) {
        String query = "INSERT INTO `channels_join`(`channel_id`) VALUES(?);";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, joinChannel.getChannelId());
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
    public boolean delete(JoinChannelPojo joinChannel) {
        String query = "DELETE FROM `channels_join` WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, joinChannel.getChannelId());
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
    public boolean update(JoinChannelPojo joinChannel) {
        String query = "UPDATE `channels_join` SET `message` = ?, `embed` = ? WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, joinChannel.getMessage());
                pst.setBoolean(2, joinChannel.getEmbed());
                pst.setString(3, joinChannel.getChannelId());
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

    public boolean update(JoinChannelPojo joinChannel, String channelId) {
        String query = "UPDATE `channels_join` SET `channel_id` = ?, `message` = ?, `embed` = ? WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, channelId);
                pst.setString(2, joinChannel.getMessage());
                pst.setBoolean(3, joinChannel.getEmbed());
                pst.setString(4, joinChannel.getChannelId());
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
    public JoinChannelPojo find(String channelId) {
        JoinChannelPojo joinChannel = null;
        String query = "SELECT `channel_id`, `message`, `embed` FROM `channels_join` WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, channelId);
            try (ResultSet rs = pst.executeQuery();) {
                rs.next();
                joinChannel = PojoFactory.getJoinChannel();
                joinChannel.setChannelId(rs.getString("channel_id"));
                joinChannel.setMessage(rs.getString("message"));
                joinChannel.setEmbed(rs.getBoolean("embed"));
            }
        } catch (SQLException e) {
        }
        return joinChannel;
    }

}