package handlers.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class MemberModel {

    private static final Logger LOG = LoggerFactory.getLogger(MemberModel.class);

    protected void addDefRole(Guild guild, PGuild pguild, User user) {
        if (pguild.getDefaultRole() != null) {
            try {
                Role defaultRole = guild.getRoleById(pguild.getDefaultRole());
                guild.addRoleToMember(user.getId(), defaultRole).queue();
            } catch (HierarchyException | ErrorResponseException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    protected void join(Guild guild, User user, PGuild pguild, MemberView memberView) {
        if (pguild.getJoinChannel() != null) {
            ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
            PJoinChannel joinChannel = joinChannelDao.find(pguild.getJoinChannel());
            try {
                if (joinChannel.getMessage() != null && !joinChannel.getEmbed()) {
                    memberView.sendJoinMessage(guild, joinChannel, user);
                    // guild.getTextChannelById(joinChannel.getChannelId()).sendMessage(joinChannel.getMessage().replace(MEMBER_PATTERN,
                    // user.getName())).queue();

                } else if (joinChannel.getMessage() != null && joinChannel.getEmbed()) {
                    memberView.sendJoinMessage(guild, joinChannel, user, "join_channel_full");

                } else if (joinChannel.getMessage() == null && joinChannel.getEmbed()) {
                    memberView.sendJoinMessage(guild, joinChannel, user, "join_channel_only_box");
                }
            } catch (InsufficientPermissionException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    protected void remove(Guild guild, User user, PGuild pguild, MemberView memberView) {
        if (pguild.getLeaveChannel() != null) {
            ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            PLeaveChannel leaveChannel = leaveChannelDao.find(pguild.getLeaveChannel());
            try {
                if (leaveChannel.getMessage() != null && !leaveChannel.getEmbed()) {
                    memberView.sendLeaveMessage(guild, leaveChannel, user);
                    // guild.getTextChannelById(leaveChannel.getChannelId()).sendMessage(leaveChannel.getMessage().replace(MEMBER_PATTERN,
                    // user.getName())).queue();
                }

                else if (leaveChannel.getMessage() != null && leaveChannel.getEmbed()) {
                    memberView.sendLeaveMessage(guild, leaveChannel, user, "leave_channel_full");
                }

                else if (leaveChannel.getMessage() == null && leaveChannel.getEmbed()) {
                    memberView.sendLeaveMessage(guild, leaveChannel, user, "leave_channel_only_box");
                }
            } catch (InsufficientPermissionException e) {
                LOG.error(e.getMessage());
            }
        }
    }

}