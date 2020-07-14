package commands.user;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.pojo.GuildPojo;
import listeners.commands.UserListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class Ping extends UserListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public Ping(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        event.getJDA().getRestPing().queue(ping -> {
            ProxyEmbed embed = new ProxyEmbed();
            embed.ping(ping);
            ProxyUtils.sendEmbed(event, embed);
        });
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.PING.getName(),
                    "Display the time in milliseconds that discord took to respond to a request.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.PING.getName() + "`.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event, embed);
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event,
                    "Display the time in milliseconds that discord took to respond to a request. "
                    + "**Example:** `" + guild.getPrefix() + Command.PING.getName() + "`.");
            // @formatter:on
        }
    }

}