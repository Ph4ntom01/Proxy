package commands.administrator;

import java.util.Objects;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ControlChannel extends ACommand {

    public ControlChannel(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        super(event, args, command, guild);
    }

    public ControlChannel(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        TextChannel textChannel = retrieveMentionnedTextChannel(1);
        if (textChannel == null) { return; }
        if (Objects.equals(textChannel.getIdLong(), getPGuild().getControlChannel())) {
            sendMessage("The control channel for new members has already been set to " + textChannel.getAsMention() + ".");
        } else {
            if (getPGuild().getDefaultRole() == null) {
                sendMessage("In order to create your control channel, please select your default role first by using `" + getGuildPrefix() + ECommand.DEFROLE.getName() + " @aRole`.");
            } else {
                ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
                getPGuild().setControlChannel(textChannel.getIdLong());
                guildDao.update(getPGuild());
                sendMessage("The control channel for new members is now " + textChannel.getAsMention() + ".");
            }
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Invite the new member to accept the server's terms of use by having him add a reaction.\n\nDefault reaction is: :white_check_mark:\n\n"
                    + "Example: `" + getGuildPrefix() + getCommandName() + " #aTextChannel`\n\n"
                    + "*You can also mention a channel by his ID*.");
            // @formatter:on
        } else {
            // @formatter:off
            sendMessage("Invite the new member to accept the server's terms of use by having him add a reaction. "
                    + "Default reaction is: :white_check_mark:. **Example:** `" + getGuildPrefix() + getCommandName() + " #aTextChannel`.");
            // @formatter:on
        }
    }

}