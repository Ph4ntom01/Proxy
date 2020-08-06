package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.database.Dao;
import dao.pojo.ChannelJoinPojo;
import dao.pojo.ChannelLeavePojo;
import dao.pojo.GuildPojo;
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
            disableChannelJoin();
        }

        else if (command == Command.JOINMESSAGE) {
            disableJoinMessage();
        }

        else if (command == Command.JOINEMBED) {
            disableJoinEmbed();
        }

        else if (command == Command.LEAVECHAN) {
            disableChannelLeave();
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

    private void disableChannelJoin() {
        if (guild.getChannelJoin() != null && !guild.getChannelJoin().isEmpty()) {
            Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
            Dao<ChannelJoinPojo> channelJoinDao = DaoFactory.getChannelJoinDAO();
            ChannelJoinPojo channelJoin = channelJoinDao.find(guild.getChannelJoin());
            guild.setChannelJoin(null);
            guildDao.update(guild);
            channelJoinDao.delete(channelJoin);
            ProxyUtils.sendMessage(event.getChannel(), "Welcoming channel is now **disabled**, you will no longer receive notifications when a member joins the server.");
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Welcoming channel has already been **disabled**.");
        }
    }

    private void disableJoinMessage() {
        if (guild.getChannelJoin() != null && !guild.getChannelJoin().isEmpty()) {

            Dao<ChannelJoinPojo> channelJoinDao = DaoFactory.getChannelJoinDAO();
            ChannelJoinPojo channelJoin = channelJoinDao.find(guild.getChannelJoin());

            if (channelJoin.getMessage() != null && !channelJoin.getMessage().isEmpty()) {
                channelJoin.setMessage(null);
                channelJoinDao.update(channelJoin);
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
        if (guild.getChannelJoin() != null && !guild.getChannelJoin().isEmpty()) {

            Dao<ChannelJoinPojo> channelJoinDao = DaoFactory.getChannelJoinDAO();
            ChannelJoinPojo channelJoin = channelJoinDao.find(guild.getChannelJoin());

            if (channelJoin.getEmbed()) {
                channelJoin.setEmbed(false);
                channelJoinDao.update(channelJoin);
                ProxyUtils.sendMessage(event.getChannel(), "Welcoming box is now **disabled**, you will no longer receive a box when a member joins the server.");
            } else {
                ProxyUtils.sendMessage(event.getChannel(), "Welcoming box has already been **disabled**.");
            }
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "In order to disable the welcoming box, please select your welcoming channel first using **" + guild.getPrefix() + Command.JOINCHAN.getName()
                    + " #aTextChannel** and enable the box by using **" + guild.getPrefix() + Command.JOINEMBED.getName() + " on**.");
        }
    }

    private void disableChannelLeave() {
        if (guild.getChannelLeave() != null && !guild.getChannelLeave().isEmpty()) {
            Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
            Dao<ChannelLeavePojo> channelLeaveDao = DaoFactory.getChannelLeaveDAO();
            ChannelLeavePojo channelLeave = channelLeaveDao.find(guild.getChannelLeave());
            guild.setChannelLeave(null);
            guildDao.update(guild);
            channelLeaveDao.delete(channelLeave);
            ProxyUtils.sendMessage(event.getChannel(), "Leaving channel is now **disabled**, you will no longer receive notifications when a member leaves the server.");
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Leaving channel has already been **disabled**.");
        }
    }

    private void disableLeaveMessage() {
        if (guild.getChannelLeave() != null && !guild.getChannelLeave().isEmpty()) {

            Dao<ChannelLeavePojo> channelLeaveDao = DaoFactory.getChannelLeaveDAO();
            ChannelLeavePojo channelLeave = channelLeaveDao.find(guild.getChannelLeave());

            if (channelLeave.getMessage() != null && !channelLeave.getMessage().isEmpty()) {
                channelLeave.setMessage(null);
                channelLeaveDao.update(channelLeave);
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
        if (guild.getChannelLeave() != null && !guild.getChannelLeave().isEmpty()) {

            Dao<ChannelLeavePojo> channelLeaveDao = DaoFactory.getChannelLeaveDAO();
            ChannelLeavePojo channelLeave = channelLeaveDao.find(guild.getChannelLeave());

            if (channelLeave.getEmbed()) {
                channelLeave.setEmbed(false);
                channelLeaveDao.update(channelLeave);
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
        if (guild.getChannelControl() != null) {
            Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
            guild.setChannelControl(null);
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