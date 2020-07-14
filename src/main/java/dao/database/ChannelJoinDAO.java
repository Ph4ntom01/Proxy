package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.ChannelJoinPojo;
import factory.PojoFactory;

public class ChannelJoinDAO extends Dao<ChannelJoinPojo> {

    public ChannelJoinDAO(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean create(ChannelJoinPojo channelJoin) {
        String query = "INSERT INTO `channels_join`(`channel_id`) VALUES(?);";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, channelJoin.getChannelId());
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
    public boolean delete(ChannelJoinPojo channelJoin) {
        String query = "DELETE FROM `channels_join` WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, channelJoin.getChannelId());
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
    public boolean update(ChannelJoinPojo channelJoin) {
        String query = "UPDATE `channels_join` SET `message` = ?, `embed` = ? WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, channelJoin.getMessage());
                pst.setBoolean(2, channelJoin.getEmbed());
                pst.setString(3, channelJoin.getChannelId());
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

    public boolean update(ChannelJoinPojo channelJoin, String channelId) {
        String query = "UPDATE `channels_join` SET `channel_id` = ?, `message` = ?, `embed` = ? WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, channelId);
                pst.setString(2, channelJoin.getMessage());
                pst.setBoolean(3, channelJoin.getEmbed());
                pst.setString(4, channelJoin.getChannelId());
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
    public ChannelJoinPojo find(String channelId) {
        ChannelJoinPojo channelJoin = null;
        String query = "SELECT `channel_id`, `message`, `embed` FROM `channels_join` WHERE `channel_id` = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, channelId);
            try (ResultSet rs = pst.executeQuery();) {
                rs.next();
                channelJoin = PojoFactory.getChannelJoin();
                channelJoin.setChannelId(rs.getString("channel_id"));
                channelJoin.setMessage(rs.getString("message"));
                channelJoin.setEmbed(rs.getBoolean("embed"));
            }
        } catch (SQLException e) {
        }
        return channelJoin;
    }

}