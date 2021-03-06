package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.PJoinChannel;

public class JoinChannelDAO extends ADao<PJoinChannel> {

    private static final Logger LOG = LoggerFactory.getLogger(JoinChannelDAO.class);

    protected JoinChannelDAO(HikariDataSource datasource) {
        super(datasource);
    }

    @Override
    public boolean create(PJoinChannel joinChannel) {
        String query = "INSERT INTO join_channel(channel_id) VALUES(?);";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, joinChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(PJoinChannel joinChannel) {
        String query = "DELETE FROM join_channel WHERE channel_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, joinChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean update(PJoinChannel joinChannel) {
        String query = "UPDATE join_channel SET message = ?, embed = ? WHERE channel_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, joinChannel.getMessage());
            pst.setBoolean(2, joinChannel.getEmbed());
            pst.setLong(3, joinChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean update(PJoinChannel joinChannel, Long channelId) {
        String query = "UPDATE join_channel SET channel_id = ?, message = ?, embed = ? WHERE channel_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setLong(1, channelId);
            pst.setString(2, joinChannel.getMessage());
            pst.setBoolean(3, joinChannel.getEmbed());
            pst.setLong(4, joinChannel.getChannelId());
            pst.executeUpdate();
        } catch (SQLException e) {
            LOG.error(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public PJoinChannel find(Long... channelLong) {
        PJoinChannel joinChannel = null;
        String query = "SELECT channel_id, message, embed FROM join_channel WHERE channel_id = ?;";
        try (Connection conn = datasource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            setNLong(1, pst, channelLong[0]);
            ResultSet rs = pst.executeQuery();
            rs.next();
            joinChannel = new PJoinChannel();
            // If the channel does not exists, no row is sent and an error occurs if we try to get a value from
            // the result set.
            // In that case, only the freshly instantiated channel object is returned without any setters.
            if (rs.getRow() != 0) {
                joinChannel.setChannelId(rs.getLong("channel_id"));
                joinChannel.setMessage(rs.getString("message"));
                joinChannel.setEmbed(rs.getBoolean("embed"));
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return joinChannel;
    }

}