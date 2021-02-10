package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.PLeaveChannel;

public class LeaveChannelDAO extends ADao<PLeaveChannel> {

    protected LeaveChannelDAO(HikariDataSource datasource) {
        super(datasource);
    }

    @Override
    public boolean create(PLeaveChannel leaveChannel) {
        String query = "INSERT INTO leave_channel(channel_id) VALUES(?);";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, leaveChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(PLeaveChannel leaveChannel) {
        String query = "DELETE FROM leave_channel WHERE channel_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, leaveChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean update(PLeaveChannel leaveChannel) {
        String query = "UPDATE leave_channel SET message = ?, embed = ? WHERE channel_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, leaveChannel.getMessage());
            pst.setBoolean(2, leaveChannel.getEmbed());
            pst.setLong(3, leaveChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public boolean update(PLeaveChannel leaveChannel, Long channelId) {
        String query = "UPDATE leave_channel SET channel_id = ?, message = ?, embed = ? WHERE channel_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, channelId);
            pst.setString(2, leaveChannel.getMessage());
            pst.setBoolean(3, leaveChannel.getEmbed());
            pst.setLong(4, leaveChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    @Override
    public PLeaveChannel find(Long... channelLong) {
        PLeaveChannel leaveChannel = null;
        String query = "SELECT channel_id, message, embed FROM leave_channel WHERE channel_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            setNLong(1, pst, channelLong[0]);
            ResultSet rs = pst.executeQuery();
            rs.next();
            leaveChannel = new PLeaveChannel();
            // If the channel does not exists, no row is sent and an error occurs if we try to get a value from
            // the result set.
            // In that case, only the freshly instantiated channel object is returned without any setters.
            if (rs.getRow() != 0) {
                leaveChannel.setChannelId(rs.getLong("channel_id"));
                leaveChannel.setMessage(rs.getString("message"));
                leaveChannel.setEmbed(rs.getBoolean("embed"));
            }
        } catch (SQLException e) {
        }
        return leaveChannel;
    }

}