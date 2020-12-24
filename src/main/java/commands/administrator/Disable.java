package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.database.Dao;
import dao.pojo.GuildPojo;
import dao.pojo.JoinChannelPojo;
import dao.pojo.LeaveChannelPojo;
import factory.DaoFactory;
import listeners.commands.AdministratorListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class Disable extends AdministratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;
    private Command command;

    public Disable(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    public Disable(GuildMessageReceivedEvent event, GuildPojo guild, Command command) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
        this.command = command;
    }

    @Override
    public void execute() {
        if (command == Command.JOINCHAN) {
            disableJoinChannel();
        }

        else if (command == Command.JOINMESSAGE) {
            disableJoinMessage();
        }

        else if (command == Command.JOINEMBED) {
            disableJoinEmbed();
        }

        else if (command == Command.LEAVECHAN) {
            disableLeaveChannel();
        }

        else if (command == Command.LEAVEMESSAGE) {
            disableLeaveMessage();
        }

        else if (command == Command.LEAVEEMBED) {
            disableLeaveEmbed();
        }

        else if (command == Command.CONTROLCHAN) {
            disableControlChan();
        }

        else if (command == Command.DEFROLE) {
            disableDefaultRole();
        }

        else if (command == Command.SHIELD) {
            disableShield();
        }

        else {
            ProxyEmbed embed = new ProxyEmbed();
            embed.disable(guild.getPrefix());
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.DISABLE.getName(), 
                    "Disable the welcoming channel-message-box, leaving channel-message-box, the default role or the shield.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.DISABLE.getName() + " " + Command.JOINCHAN.getName() + "`",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event.getChannel(),
                    "Disable the welcoming channel-message-embed, leaving channel-message-embed, the default role or the shield. "
                    + "**Example:** `" + guild.getPrefix() + Command.DISABLE.getName() + " " + Command.JOINCHAN.getName() + "`.");
            // @formatter:on
        }
    }

    private void disableJoinChannel() {
        if (guild.getJoinChannel() != null && !guild.getJoinChannel().isEmpty()) {
            Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
            Dao<JoinChannelPojo> joinChannelDao = DaoFactory.getJoinChannelDAO();
            JoinChannelPojo joinChannel = joinChannelDao.find(guild.getJoinChannel());
            guild.setJoinChannel(null);
            guildDao.update(guild);
            joinChannelDao.delete(joinChannel);
            ProxyUtils.sendMessage(event.getChannel(), "Welcoming channel is now **disabled**, you will no longer receive notifications when a member joins the server.");
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Welcoming channel has already been **disabled**.");
        }
    }

    private void disableJoinMessage() {
        if (guild.getJoinChannel() != null && !guild.getJoinChannel().isEmpty()) {

            Dao<JoinChannelPojo> joinChannelDao = DaoFactory.getJoinChannelDAO();
            JoinChannelPojo joinChannel = joinChannelDao.find(guild.getJoinChannel());

            if (joinChannel.getMessage() != null && !joinChannel.getMessage().isEmpty()) {
                joinChannel.setMessage(null);
                joinChannelDao.update(joinChannel);
                ProxyUtils.sendMessage(event.getChannel(), "Welcoming message is now **disabled**, you will no longer receive a message when a member joins the server.");
            } else {
                ProxyUtils.sendMessage(event.getChannel(), "Welcoming message has already been **disabled**.");
            }
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "In order to disable the welcoming message, please select your welcoming channel first using **" + guild.getPrefix() + Command.JOINCHAN.getName()
                    + " #aTextChannel** and set your message by using **" + guild.getPrefix() + Command.JOINMESSAGE.getName() + " your message**.");
        }
    }

    private void disableJoinEmbed() {
        if (guild.getJoinChannel() != null && !guild.getJoinChannel().isEmpty()) {

            Dao<JoinChannelPojo> joinChannelDao = DaoFactory.getJoinChannelDAO();
            JoinChannelPojo joinChannel = joinChannelDao.find(guild.getJoinChannel());

            if (joinChannel.getEmbed()) {
                joinChannel.setEmbed(false);
                joinChannelDao.update(joinChannel);
                ProxyUtils.sendMessage(event.getChannel(), "Welcoming box is now **disabled**, you will no longer receive a box when a member joins the server.");
            } else {
                ProxyUtils.sendMessage(event.getChannel(), "Welcoming box has already been **disabled**.");
            }
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "In order to disable the welcoming box, please select your welcoming channel first using **" + guild.getPrefix() + Command.JOINCHAN.getName()
                    + " #aTextChannel** and enable the box by using **" + guild.getPrefix() + Command.JOINEMBED.getName() + " on**.");
        }
    }

    private void disableLeaveChannel() {
        if (guild.getLeaveChannel() != null && !guild.getLeaveChannel().isEmpty()) {
            Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
            Dao<LeaveChannelPojo> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            LeaveChannelPojo leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());
            guild.setLeaveChannel(null);
            guildDao.update(guild);
            leaveChannelDao.delete(leaveChannel);
            ProxyUtils.sendMessage(event.getChannel(), "Leaving channel is now **disabled**, you will no longer receive notifications when a member leaves the server.");
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Leaving channel has already been **disabled**.");
        }
    }

    private void disableLeaveMessage() {
        if (guild.getLeaveChannel() != null && !guild.getLeaveChannel().isEmpty()) {

            Dao<LeaveChannelPojo> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            LeaveChannelPojo leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());

            if (leaveChannel.getMessage() != null && !leaveChannel.getMessage().isEmpty()) {
                leaveChannel.setMessage(null);
                leaveChannelDao.update(leaveChannel);
                ProxyUtils.sendMessage(event.getChannel(), "Leaving message is now **disabled**, you will no longer receive a message when a member leaves the server.");
            } else {
                ProxyUtils.sendMessage(event.getChannel(), "Leaving message has already been **disabled**.");
            }
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "In order to disable the leaving message, please select your leaving channel first using **" + guild.getPrefix() + Command.LEAVECHAN.getName()
                    + " #aTextChannel** and set your message by using **" + guild.getPrefix() + Command.LEAVEMESSAGE.getName() + " your message**.");
        }
    }

    private void disableLeaveEmbed() {
        if (guild.getLeaveChannel() != null && !guild.getLeaveChannel().isEmpty()) {

            Dao<LeaveChannelPojo> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            LeaveChannelPojo leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());

            if (leaveChannel.getEmbed()) {
                leaveChannel.setEmbed(false);
                leaveChannelDao.update(leaveChannel);
                ProxyUtils.sendMessage(event.getChannel(), "Leaving box is now **disabled**, you will no longer receive a box when a member leaves the server.");
            } else {
                ProxyUtils.sendMessage(event.getChannel(), "Leaving box has already been **disabled**.");
            }
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "In order to disable the leaving box, please select your leaving channel first using **" + guild.getPrefix() + Command.LEAVECHAN.getName()
                    + " #aTextChannel** and enable the box by using **" + guild.getPrefix() + Command.LEAVEEMBED.getName() + " on**.");
        }
    }

    private void disableControlChan() {
        if (guild.getControlChannel() != null) {
            Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
            guild.setControlChannel(null);
            guildDao.update(guild);
            ProxyUtils.sendMessage(event.getChannel(), "Control channel is now **disabled**.");
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Control channel has already been **disabled**.");
        }
    }

    private void disableDefaultRole() {
        if (guild.getDefaultRole() != null && !guild.getDefaultRole().isEmpty()) {
            Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
            guild.setDefaultRole(null);
            guildDao.update(guild);
            ProxyUtils.sendMessage(event.getChannel(), "Default role is now **disabled**, the bot will no longer add a role when a member joins the server.");
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Default role has already been **disabled**.");
        }
    }

    private void disableShield() {
        if (guild.getShield() != 0) {
            Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
            int oldShield = guild.getShield();
            guild.setShield(0);
            guildDao.update(guild);
            ProxyUtils.sendMessage(event.getChannel(),
                    "Shield is now **disabled**, the bot will no longer kick accounts that have been created less than **" + oldShield + "** " + ProxyUtils.day(oldShield) + " ago.");
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Shield has already been **disabled**.");
        }
    }

}