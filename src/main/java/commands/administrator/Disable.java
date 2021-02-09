package commands.administrator;

import java.awt.Color;

import org.apache.commons.lang3.StringUtils;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Disable extends ACommand {

    private ECommand mentionnedCommand;

    public Disable(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    public Disable(GuildMessageReceivedEvent event, ECommand command, PGuild guild, ECommand mentionnedCommand) {
        super(event, command, guild);
        this.mentionnedCommand = mentionnedCommand;
    }

    @Override
    public void execute() {
        if (mentionnedCommand == ECommand.JOINCHAN) {
            disableJoinChannel();
        }

        else if (mentionnedCommand == ECommand.JOINMESSAGE) {
            disableJoinMessage();
        }

        else if (mentionnedCommand == ECommand.JOINEMBED) {
            disableJoinEmbed();
        }

        else if (mentionnedCommand == ECommand.LEAVECHAN) {
            disableLeaveChannel();
        }

        else if (mentionnedCommand == ECommand.LEAVEMESSAGE) {
            disableLeaveMessage();
        }

        else if (mentionnedCommand == ECommand.LEAVEEMBED) {
            disableLeaveEmbed();
        }

        else if (mentionnedCommand == ECommand.CONTROLCHAN) {
            disableControlChan();
        }

        else if (mentionnedCommand == ECommand.DEFROLE) {
            disableDefaultRole();
        }

        else if (mentionnedCommand == ECommand.SHIELD) {
            disableShield();
        }

        else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.YELLOW);
            embed.setTitle("__**" + StringUtils.capitalize(getCommandName()) + "**__");
            // @formatter:off
            embed.addField("",
                    "`" + ECommand.JOINCHAN.getName() + "` " + "`" + ECommand.JOINMESSAGE.getName() + "` " + "`" + ECommand.JOINEMBED.getName() + "` " + "`" + ECommand.LEAVECHAN.getName()
                    + "` " + "`" + ECommand.LEAVEMESSAGE.getName() + "` " + "`" + ECommand.LEAVEEMBED.getName() + "` " + "`" + ECommand.CONTROLCHAN.getName() + "` " + "`" + ECommand.DEFROLE.getName() + "` " + "`"
                    + ECommand.SHIELD.getName() + "`",
                    false);
            // @formatter:on
            embed.addField("", "Example: `" + getGuildPrefix() + getCommandName() + " " + ECommand.JOINCHAN.getName() + "`", false);
            sendEmbed(embed);
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Disable the welcoming channel-message-box, leaving channel-message-box, the default role or the shield.\n\n"
                    + "Example: `" + getGuildPrefix() + getCommandName() + " " + ECommand.JOINCHAN.getName() + "`");
            // @formatter:on
        } else {
            // @formatter:off
            sendMessage(
                    "Disable the welcoming channel-message-embed, leaving channel-message-embed, the default role or the shield. "
                    + "**Example:** `" + getGuildPrefix() + getCommandName() + " " + ECommand.JOINCHAN.getName() + "`.");
            // @formatter:on
        }
    }

    private void disableJoinChannel() {
        if (getPGuild().getJoinChannel() != null) {
            ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
            ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
            PJoinChannel joinChannel = joinChannelDao.find(getPGuild().getJoinChannel());
            getPGuild().setJoinChannel(null);
            guildDao.update(getPGuild());
            joinChannelDao.delete(joinChannel);
            sendMessage("Welcoming channel is now **disabled**, you will no longer receive notifications when a member joins the server.");
        } else {
            sendMessage("Welcoming channel has already been **disabled**.");
        }
    }

    private void disableJoinMessage() {
        if (getPGuild().getJoinChannel() != null) {
            ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
            PJoinChannel joinChannel = joinChannelDao.find(getPGuild().getJoinChannel());
            if (joinChannel.getMessage() != null && !joinChannel.getMessage().isEmpty()) {
                joinChannel.setMessage(null);
                joinChannelDao.update(joinChannel);
                sendMessage("Welcoming message is now **disabled**, you will no longer receive a message when a member joins the server.");
            } else {
                sendMessage("Welcoming message has already been **disabled**.");
            }
        } else {
            sendMessage("In order to disable the welcoming message, please select your welcoming channel first using **" + getGuildPrefix() + ECommand.JOINCHAN.getName()
                    + " #aTextChannel** and set your message by using **" + getGuildPrefix() + ECommand.JOINMESSAGE.getName() + " your message**.");
        }
    }

    private void disableJoinEmbed() {
        if (getPGuild().getJoinChannel() != null) {
            ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
            PJoinChannel joinChannel = joinChannelDao.find(getPGuild().getJoinChannel());
            if (joinChannel.getEmbed()) {
                joinChannel.setEmbed(false);
                joinChannelDao.update(joinChannel);
                sendMessage("Welcoming box is now **disabled**, you will no longer receive a box when a member joins the server.");
            } else {
                sendMessage("Welcoming box has already been **disabled**.");
            }
        } else {
            sendMessage("In order to disable the welcoming box, please select your welcoming channel first using **" + getGuildPrefix() + ECommand.JOINCHAN.getName()
                    + " #aTextChannel** and enable the box by using **" + getGuildPrefix() + ECommand.JOINEMBED.getName() + " on**.");
        }
    }

    private void disableLeaveChannel() {
        if (getPGuild().getLeaveChannel() != null) {
            ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
            ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            PLeaveChannel leaveChannel = leaveChannelDao.find(getPGuild().getLeaveChannel());
            getPGuild().setLeaveChannel(null);
            guildDao.update(getPGuild());
            leaveChannelDao.delete(leaveChannel);
            sendMessage("Leaving channel is now **disabled**, you will no longer receive notifications when a member leaves the server.");
        } else {
            sendMessage("Leaving channel has already been **disabled**.");
        }
    }

    private void disableLeaveMessage() {
        if (getPGuild().getLeaveChannel() != null) {
            ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            PLeaveChannel leaveChannel = leaveChannelDao.find(getPGuild().getLeaveChannel());
            if (leaveChannel.getMessage() != null && !leaveChannel.getMessage().isEmpty()) {
                leaveChannel.setMessage(null);
                leaveChannelDao.update(leaveChannel);
                sendMessage("Leaving message is now **disabled**, you will no longer receive a message when a member leaves the server.");
            } else {
                sendMessage("Leaving message has already been **disabled**.");
            }
        } else {
            sendMessage("In order to disable the leaving message, please select your leaving channel first using **" + getGuildPrefix() + ECommand.LEAVECHAN.getName()
                    + " #aTextChannel** and set your message by using **" + getGuildPrefix() + ECommand.LEAVEMESSAGE.getName() + " your message**.");
        }
    }

    private void disableLeaveEmbed() {
        if (getPGuild().getLeaveChannel() != null) {
            ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            PLeaveChannel leaveChannel = leaveChannelDao.find(getPGuild().getLeaveChannel());
            if (leaveChannel.getEmbed()) {
                leaveChannel.setEmbed(false);
                leaveChannelDao.update(leaveChannel);
                sendMessage("Leaving box is now **disabled**, you will no longer receive a box when a member leaves the server.");
            } else {
                sendMessage("Leaving box has already been **disabled**.");
            }
        } else {
            sendMessage("In order to disable the leaving box, please select your leaving channel first using **" + getGuildPrefix() + ECommand.LEAVECHAN.getName()
                    + " #aTextChannel** and enable the box by using **" + getGuildPrefix() + ECommand.LEAVEEMBED.getName() + " on**.");
        }
    }

    private void disableControlChan() {
        if (getPGuild().getControlChannel() != null) {
            ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
            getPGuild().setControlChannel(null);
            guildDao.update(getPGuild());
            sendMessage("Control channel is now **disabled**.");
        } else {
            sendMessage("Control channel has already been **disabled**.");
        }
    }

    private void disableDefaultRole() {
        if (getPGuild().getDefaultRole() != null) {
            ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
            getPGuild().setDefaultRole(null);
            guildDao.update(getPGuild());
            sendMessage("Default role is now **disabled**, the bot will no longer add a role when a member joins the server.");
        } else {
            sendMessage("Default role has already been **disabled**.");
        }
    }

    private void disableShield() {
        if (getPGuild().getShield() != 0) {
            ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
            int oldShield = getPGuild().getShield();
            getPGuild().setShield(0);
            guildDao.update(getPGuild());
            sendMessage("Shield is now **disabled**, the bot will no longer kick accounts that have been created less than **" + oldShield + "** day(s) ago.");
        } else {
            sendMessage("Shield has already been **disabled**.");
        }
    }

}