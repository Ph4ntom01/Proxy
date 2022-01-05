package commands.moderator;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class ResetChan extends ACommand {

    public ResetChan(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    public ResetChan(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        try {
            getChannel().createCopy().queue();
            getChannel().delete().queue();

        } catch (InsufficientPermissionException e) {
            sendMessage("Missing permission: **" + Permission.MANAGE_CHANNEL.getName() + "**.");

        } catch (ErrorResponseException e) {
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Create a copy and delete the current text channel.\n\nExample: `" + getPGuildPrefix() + getCommandName() + "`");
        } else {
            sendMessage("Create a copy and delete the current text channel. **Example:** `" + getPGuildPrefix() + getCommandName() + "`.");
        }
    }

}