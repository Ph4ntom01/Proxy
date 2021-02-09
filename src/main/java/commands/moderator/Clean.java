package commands.moderator;

import java.util.concurrent.TimeUnit;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class Clean extends ACommand {

    public Clean(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        super(event, args, command, guild);
    }

    public Clean(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        int messages = getIntArg(1);
        if (messages >= 2 && messages <= 100) {
            deleteMessages(messages);
        } else {
            deleteMessages(100);
        }
    }

    public void deleteMessages(int amount) {
        getChannel().getHistory().retrievePast(amount).queue(messages -> {
            try {
                getChannel().deleteMessages(messages).queue();
                getChannel().sendMessage("**" + messages.size() + "** deleted messages.").queueAfter(500, TimeUnit.MILLISECONDS, response -> response.delete().queueAfter(1500, TimeUnit.MILLISECONDS));

            } catch (IllegalArgumentException e) {
                getMessage().delete().queue();

            } catch (InsufficientPermissionException e) {
                sendMessage("Missing permission: **" + Permission.MESSAGE_MANAGE.getName() + "**.");
            }
        });
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Quickly cleans the last 10 messages, or however many you specify.\n\n" + "Example: `" + getGuildPrefix() + getCommandName() + " 20`");
        } else {
            sendMessage("Quickly cleans the last 10 messages, or however many you specify. **Example:** `" + getGuildPrefix() + getCommandName() + " 20`.");
        }
    }

}