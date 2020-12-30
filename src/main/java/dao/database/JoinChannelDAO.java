package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.PJoinChannel;
import factory.PojoFactory;

public class JoinChannelDAO extends Dao<PJoinChannel> {

    public JoinChannelDAO(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean create(PJoinChannel joinChannel) {
        String query = "INSERT INTO join_channel(channel_id) VALUES(?);";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, joinChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(PJoinChannel joinChannel) {
        String query = "DELETE FROM join_channel WHERE channel_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, joinChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean update(PJoinChannel joinChannel) {
        String query = "UPDATE join_channel SET message = ?, embed = ? WHERE channel_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, joinChannel.getMessage());
            pst.setBoolean(2, joinChannel.getEmbed());
            pst.setString(3, joinChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean update(PJoinChannel joinChannel, String channelId) {
        String query = "UPDATE join_channel SET channel_id = ?, message = ?, embed = ? WHERE channel_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, channelId);
            pst.setString(2, joinChannel.getMessage());
            pst.setBoolean(3, joinChannel.getEmbed());
            pst.setString(4, joinChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public PJoinChannel find(String channelId) {
        PJoinChannel joinChannel = null;
        String query = "SELECT channel_id, message, embed FROM join_channel WHERE channel_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, channelId);
            ResultSet rs = pst.executeQuery();
            rs.next();
            joinChannel = PojoFactory.getJoinChannel();
            joinChannel.setChannelId(rs.getString("channel_id"));
            joinChannel.setMessage(rs.getString("message"));
            joinChannel.setEmbed(rs.getBoolean("embed"));
        } catch (SQLException e) {
        }
        return joinChannel;
    }

}