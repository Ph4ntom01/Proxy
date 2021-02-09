package commands.administrator;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import dao.pojo.PLeaveChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LeaveMessage extends ACommand {

    public LeaveMessage(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        super(event, args, command, guild);
    }

    public LeaveMessage(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        if (getPGuild().getLeaveChannel() != null) {
            ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            PLeaveChannel leaveChannel = leaveChannelDao.find(getPGuild().getLeaveChannel());
            leaveChannel.setMessage(getLeaveMessage(getArgs()));
            leaveChannelDao.update(leaveChannel);
            sendMessage("Leaving message has been successfully defined.");
        } else {
            sendMessage("In order to create a leaving message, please select your leaving channel first using `" + getGuildPrefix() + ECommand.LEAVECHAN.getName() + " #aTextChannel`.");
        }
    }

    private String getLeaveMessage(String[] args) {
        StringBuilder leaveMessage = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            leaveMessage.append(args[i]);
            leaveMessage.append(" ");
        }
        return leaveMessage.deleteCharAt(leaveMessage.length() - 1).toString();
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Set the leaving message.\n\nExample: `" + getGuildPrefix() + getCommandName()
                    + " Bye bye [member] !`\n\n*Add `[member]` if you want the bot to mention the leaving member*.");
            // @formatter:on
        } else {
            // @formatter:off
            sendMessage(
                    "Set the leaving message. **Example:** `" + getGuildPrefix() + getCommandName()
                    + " Bye bye [member] !`\n*Add `[member]` if you want the bot to mention the leaving member*.");
            // @formatter:on
        }
    }

}