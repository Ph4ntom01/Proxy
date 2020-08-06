package dao.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import com.zaxxer.hikari.HikariDataSource;

import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import factory.PojoFactory;

public class GuildDAO extends Dao<GuildPojo> {

    public GuildDAO(HikariDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean create(GuildPojo guild) {
        String query = "INSERT INTO `guilds`(`guild_id`, `guild_name`) VALUES(?, ?);";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, guild.getId());
                pst.setString(2, guild.getName());
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

    public boolean create(Set<MemberPojo> members) {
        String query = "INSERT INTO `members`(`guild_id`, `member_id`, `member_name`, `permission_level`) VALUES(?, ?, ?, ?)";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                for (MemberPojo member : members) {
                    pst.setString(1, member.getGuildId());
                    pst.setString(2, member.getId());
                    pst.setString(3, member.getName());
                    pst.setInt(4, member.getPermLevel());
                    pst.addBatch();
                }
                pst.executeBatch();
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
    public boolean delete(GuildPojo guild) {
        String query = "DELETE FROM `guilds` WHERE `guild_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, guild.getId());
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

    public boolean delete(Set<MemberPojo> members) {
        String query = "DELETE FROM `members` WHERE `guild_id` = ? AND `member_id` = ?";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                for (MemberPojo member : members) {
                    pst.setString(1, member.getGuildId());
                    pst.setString(2, member.getId());
                    pst.addBatch();
                }
                pst.executeBatch();
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
    public boolean update(GuildPojo guild) {
        String query = "UPDATE `guilds` SET `guild_name` = ?, `channel_join` = ?, `channel_leave` = ?, `channel_control` = ?,`default_role` = ?, `prefix` = ?, `shield` = ? WHERE `guild_id` = ?;";
        try (Connection conn = this.dataSource.getConnection();) {
            conn.setAutoCommit(false);
            try (PreparedStatement pst = conn.prepareStatement(query);) {
                pst.setString(1, guild.getName());
                pst.setString(2, guild.getChannelJoin());
                pst.setString(3, guild.getChannelLeave());
                pst.setString(4, guild.getChannelControl());
                pst.setString(5, guild.getDefaultRole());
                pst.setString(6, guild.getPrefix());
                pst.setInt(7, guild.getShield());
                pst.setString(8, guild.getId());
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
    public GuildPojo find(String guildId) {
        GuildPojo guild = null;
        String query = "SELECT `guild_id`, `guild_name`, `channel_join`, `channel_leave`, `channel_control`, `default_role`, `prefix`, `shield` FROM `guilds` WHERE `guild_id` = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, guildId);
            try (ResultSet rs = pst.executeQuery();) {
                rs.next();
                guild = PojoFactory.getGuild();
                guild.setId(rs.getString("guild_id"));
                guild.setName(rs.getString("guild_name"));
                guild.setChannelJoin(rs.getString("channel_join"));
                guild.setChannelLeave(rs.getString("channel_leave"));
                guild.setChannelControl(rs.getString("channel_control"));
                guild.setDefaultRole(rs.getString("default_role"));
                guild.setPrefix(rs.getString("prefix"));
                guild.setShield(rs.getInt("shield"));
            }
        } catch (SQLException e) {
        }
        return guild;
    }

    public Set<GuildPojo> findGuilds() {
        Set<GuildPojo> guilds = null;
        String query = "SELECT `guild_id`, `guild_name`, `channel_join`, `channel_leave`, `channel_control`, `default_role`, `prefix`, `shield` FROM `guilds`;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            try (ResultSet rs = pst.executeQuery();) {
                guilds = new HashSet<>();
                while (rs.next()) {
                    GuildPojo guild = PojoFactory.getGuild();
                    guild.setId(rs.getString("guild_id"));
                    guild.setName(rs.getString("guild_name"));
                    guild.setChannelJoin(rs.getString("channel_join"));
                    guild.setChannelLeave(rs.getString("channel_leave"));
                    guild.setChannelControl(rs.getString("channel_control"));
                    guild.setDefaultRole(rs.getString("default_role"));
                    guild.setPrefix(rs.getString("prefix"));
                    guild.setShield(rs.getInt("shield"));
                    guilds.add(guild);
                }
            }
        } catch (SQLException e) {
        }
        return guilds;
    }

    public Set<MemberPojo> findMembers(String guildId) {
        Set<MemberPojo> members = null;
        String query = "SELECT `guild_id`, `member_id`, `member_name`, `permission_level` FROM `members` WHERE `guild_id` = ?;";
        try (Connection conn = this.dataSource.getConnection(); PreparedStatement pst = conn.prepareStatement(query);) {
            pst.setString(1, guildId);
            try (ResultSet rs = pst.executeQuery();) {
                members = new HashSet<>();
                while (rs.next()) {
                    MemberPojo member = PojoFactory.getMember();
                    member.setGuildId(rs.getString("guild_id"));
                    member.setId(rs.getString("member_id"));
                    member.setName(rs.getString("member_name"));
                    member.setPermLevel(rs.getInt("permission_level"));
                    members.add(member);
                }
            }
        } catch (SQLException e) {
        }
        return members;
    }

}