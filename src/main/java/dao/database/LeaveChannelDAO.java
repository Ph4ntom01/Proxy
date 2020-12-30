package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.PLeaveChannel;
import factory.PojoFactory;

public class LeaveChannelDAO extends Dao<PLeaveChannel> {

    public LeaveChannelDAO(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean create(PLeaveChannel leaveChannel) {
        String query = "INSERT INTO leave_channel(channel_id) VALUES(?);";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, leaveChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(PLeaveChannel leaveChannel) {
        String query = "DELETE FROM leave_channel WHERE channel_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, leaveChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean update(PLeaveChannel leaveChannel) {
        String query = "UPDATE leave_channel SET message = ?, embed = ? WHERE channel_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, leaveChannel.getMessage());
            pst.setBoolean(2, leaveChannel.getEmbed());
            pst.setString(3, leaveChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean update(PLeaveChannel leaveChannel, String channelId) {
        String query = "UPDATE leave_channel SET channel_id = ?, message = ?, embed = ? WHERE channel_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, channelId);
            pst.setString(2, leaveChannel.getMessage());
            pst.setBoolean(3, leaveChannel.getEmbed());
            pst.setString(4, leaveChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public PLeaveChannel find(String channelId) {
        PLeaveChannel leaveChannel = null;
        String query = "SELECT channel_id, message, embed FROM leave_channel WHERE channel_id = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, channelId);
            ResultSet rs = pst.executeQuery();
            rs.next();
            leaveChannel = PojoFactory.getLeaveChannel();
            leaveChannel.setChannelId(rs.getString("channel_id"));
            leaveChannel.setMessage(rs.getString("message"));
            leaveChannel.setEmbed(rs.getBoolean("embed"));
        } catch (SQLException e) {
        }
        return leaveChannel;
    }

}