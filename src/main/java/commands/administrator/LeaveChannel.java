package commands.administrator;

import java.util.Objects;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.database.LeaveChannelDAO;
import dao.pojo.PGuild;
import dao.pojo.PLeaveChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LeaveChannel extends ACommand {

    public LeaveChannel(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    public LeaveChannel(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
        PLeaveChannel leaveChannel = leaveChannelDao.find(getPGuild().getLeaveChannel());
        TextChannel textChannel = retrieveMentionnedTextChannel(1);
        if (textChannel == null) { return; }
        if (Objects.equals(textChannel.getIdLong(), getPGuild().getLeaveChannel()) && Objects.equals(textChannel.getIdLong(), leaveChannel.getChannelId())) {
            sendMessage("The default channel for leaving members has already been set to " + textChannel.getAsMention() + ".");
        } else if (getPGuild().getLeaveChannel() == null && leaveChannel.getChannelId() == null) {
            ADao<PGuild> guildDao = DaoFactory.getPGuildDAO();
            leaveChannel = new PLeaveChannel();
            leaveChannel.setChannelId(textChannel.getIdLong());
            getPGuild().setLeaveChannel(textChannel.getIdLong());
            leaveChannelDao.create(leaveChannel);
            guildDao.update(getPGuild());
            sendMessage("The default channel for leaving members is now " + textChannel.getAsMention() + ".");
        } else {
            // No need to update the guild table because ON UPDATE CASCADE is defined to the foreign key.
            ((LeaveChannelDAO) leaveChannelDao).update(leaveChannel, textChannel.getIdLong());
            sendMessage("The default channel for leaving members is now " + textChannel.getAsMention() + ".");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Send notification when a member leaves the server.\n\n"
                    + "Example: `" + getPGuildPrefix() + getCommandName() + " #aTextChannel`\n\n"
                    + "*You can also mention a channel by his ID*.");
            // @formatter:on
        } else {
            sendMessage("Send notification when a member leaves the server. **Example:** `" + getPGuildPrefix() + getCommandName() + " #aTextChannel`.");
        }
    }

}