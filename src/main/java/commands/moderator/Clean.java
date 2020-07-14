package commands.moderator;

import java.awt.Color;
import java.util.concurrent.TimeUnit;

import commands.CommandManager;
import configuration.constants.Command;
import dao.pojo.GuildPojo;
import listeners.commands.ModeratorListener;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class Clean extends ModeratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public Clean(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        int messages = Integer.parseInt(ProxyUtils.getArgs(event)[1]);
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
                ProxyUtils.sendMessage(event, "Missing permission: **" + Permission.MESSAGE_MANAGE.getName() + "**.");
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
            ProxyUtils.sendEmbed(event, embed);
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event,
                    "Quickly cleans the last 10 messages, or however many you specify. "
                    + "**Example:** `" + guild.getPrefix() + Command.CLEAN.getName() + " 20`.");
            // @formatter:on
        }
    }

}