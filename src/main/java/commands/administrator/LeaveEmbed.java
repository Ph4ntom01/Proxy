package commands.administrator;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import dao.pojo.PLeaveChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LeaveEmbed extends ACommand {

    public LeaveEmbed(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    public LeaveEmbed(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        if (getPGuild().getLeaveChannel() != null) {
            ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            PLeaveChannel leaveChannel = leaveChannelDao.find(getPGuild().getLeaveChannel());
            if (getArgs()[1].equalsIgnoreCase("on")) {
                if (leaveChannel.getEmbed()) {
                    sendMessage("Leaving box has already been **enabled**.");
                } else if (!leaveChannel.getEmbed()) {
                    leaveChannel.setEmbed(true);
                    leaveChannelDao.update(leaveChannel);
                    sendMessage("Leaving box is now **enabled**.");
                }
            } else if (getArgs()[1].equalsIgnoreCase("off")) {
                if (!leaveChannel.getEmbed()) {
                    sendMessage("Leaving box has already been **disabled**.");
                } else if (leaveChannel.getEmbed()) {
                    leaveChannel.setEmbed(false);
                    leaveChannelDao.update(leaveChannel);
                    sendMessage("Leaving box is now **disabled**, you will no longer receive a box when a member leaves the server.");
                }
            } else {
                sendMessage("Please specify **on** or **off**.");
            }
        } else {
            sendMessage("In order to create a leaving box, please select your leaving channel first using `" + getPGuildPrefix() + ECommand.LEAVECHAN.getName() + " #aTextChannel`.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Set the leaving box.\n\n"
                    + "Example:\n\n`" + getPGuildPrefix() + getCommandName() + " on` *enables the leaving box*.\n"
                    + "`" + getPGuildPrefix() + getCommandName() + " off` *disables the leaving box*.");
            // @formatter:on
        } else {
            sendMessage("Set the leaving box. **Example:** `" + getPGuildPrefix() + getCommandName() + " on` *enables the leaving box*.");
        }
    }

}