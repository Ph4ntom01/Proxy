package commands.administrator;

import java.util.Objects;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.database.JoinChannelDAO;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class JoinChannel extends ACommand {

    public JoinChannel(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    public JoinChannel(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
        PJoinChannel joinChannel = joinChannelDao.find(getPGuild().getJoinChannel());
        TextChannel textChannel = retrieveMentionnedTextChannel(1);
        if (textChannel == null) { return; }
        if (Objects.equals(textChannel.getIdLong(), getPGuild().getJoinChannel()) && Objects.equals(textChannel.getIdLong(), joinChannel.getChannelId())) {
            sendMessage("The default channel for new members has already been set to " + textChannel.getAsMention() + ".");
        } else if (getPGuild().getJoinChannel() == null && joinChannel.getChannelId() == null) {
            ADao<PGuild> guildDao = DaoFactory.getPGuildDAO();
            joinChannel = new PJoinChannel();
            joinChannel.setChannelId(textChannel.getIdLong());
            getPGuild().setJoinChannel(textChannel.getIdLong());
            joinChannelDao.create(joinChannel);
            guildDao.update(getPGuild());
            sendMessage("The default channel for new members is now " + textChannel.getAsMention() + ".");
        } else {
            // No need to update the guild table because ON UPDATE CASCADE is defined to the foreign key.
            ((JoinChannelDAO) joinChannelDao).update(joinChannel, textChannel.getIdLong());
            sendMessage("The default channel for new members is now " + textChannel.getAsMention() + ".");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Send notification when a member joins the server.\n\n"
                    + "Example: `" + getPGuildPrefix() + ECommand.JOINCHAN.getName() + " #aTextChannel`\n\n"
                    + "*You can also mention a channel by his ID*.");
            // @formatter:on
        } else {
            sendMessage("Send notification when a member joins the server. **Example:** `" + getPGuildPrefix() + getCommandName() + " #aTextChannel`.");
        }
    }

}