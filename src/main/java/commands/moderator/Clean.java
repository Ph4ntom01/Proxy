package commands.moderator;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import commands.CommandManager;
import configuration.constant.Command;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyUtils;

public class Clean implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public Clean(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        int messages = Integer.parseInt(ProxyUtils.getArgs(event.getMessage())[1]);
        if (messages >= 2 && messages <= 100) {
            deleteMessages(messages);
        } else {
            deleteMessages(100);
        }
    }

    public void deleteMessages(int amount) {
        event.getChannel().getHistory().retrievePast(amount).queue(messages -> {
            try {
                event.getChannel().deleteMessages(messages).queue();
                event.getChannel().sendMessage("**" + messages.size() + "** deleted messages.").queueAfter(500, TimeUnit.MILLISECONDS,
                        response -> response.delete().queueAfter(1500, TimeUnit.MILLISECONDS));

            } catch (IllegalArgumentException e) {
                event.getMessage().delete().queue();

            } catch (InsufficientPermissionException e) {
                ProxyUtils.sendMessage(event.getChannel(), "Missing permission: **" + Permission.MESSAGE_MANAGE.getName() + "**.");
            }
        });
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.CLEAN.getName(),
                    "Quickly cleans the last 10 messages, or however many you specify.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.CLEAN.getName() + " 20`",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event.getChannel(),
                    "Quickly cleans the last 10 messages, or however many you specify. "
                    + "**Example:** `" + guild.getPrefix() + Command.CLEAN.getName() + " 20`.");
            // @formatter:on
        }
    }

}