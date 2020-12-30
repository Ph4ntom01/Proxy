package commands.user;

import java.awt.Color;

import commands.CommandManager;
import configuration.constant.Command;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyUtils;

public class Ping implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public Ping(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        event.getJDA().getRestPing().queue(ping -> {
            ProxyEmbed embed = new ProxyEmbed();
            embed.ping(ping);
            ProxyUtils.sendEmbed(event.getChannel(), embed);
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
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event.getChannel(),
                    "Display the time in milliseconds that discord took to respond to a request. "
                    + "**Example:** `" + guild.getPrefix() + Command.PING.getName() + "`.");
            // @formatter:on
        }
    }

}