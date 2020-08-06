package commands.moderator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.pojo.GuildPojo;
import listeners.commands.ModeratorListener;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class Slowmode extends ModeratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public Slowmode(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            int slowmode = Integer.parseInt(ProxyUtils.getArgs(event.getMessage())[1]);
            if (slowmode == 0) {
                if (event.getChannel().getSlowmode() == 0) {
                    ProxyUtils.sendMessage(event.getChannel(), "Slowmode has already been **disabled** !");
                } else {
                    event.getChannel().getManager().setSlowmode(slowmode).queue();
                    ProxyUtils.sendMessage(event.getChannel(), "Slowmode is now **disabled** !");
                }
            } else if (slowmode == event.getChannel().getSlowmode()) {
                ProxyUtils.sendMessage(event.getChannel(), "This amount has already been defined.");

            } else if (slowmode > 0 && slowmode <= 30) {
                event.getChannel().getManager().setSlowmode(slowmode).queue();
                ProxyUtils.sendMessage(event.getChannel(), "Slowmode is now **enabled** !" + " (" + slowmode + "s).");
            } else {
                ProxyUtils.sendMessage(event.getChannel(), "Please enter a number between **0** and **30**.");
            }
        } catch (InsufficientPermissionException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Missing permission: **" + Permission.MESSAGE_MANAGE.getName() + " **or** " + Permission.MANAGE_CHANNEL.getName() + "**.");

        } catch (IllegalStateException e) {
        } catch (NumberFormatException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Please enter a number between **0** and **30**.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.SLOWMODE.getName(),
                    "Change the slowmode amount on the current channel.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.SLOWMODE.getName() + " 5`",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Change the slowmode amount on the current channel. **Example:** `" + guild.getPrefix() + Command.SLOWMODE.getName() + " 5`.");
        }
    }

}