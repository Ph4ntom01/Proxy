package commands.user;

import java.awt.Color;

import commands.CommandManager;
import configuration.constant.Command;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyUtils;

public class GuildInfo implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public GuildInfo(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        Guild gld = event.getGuild();
        gld.retrieveOwner().queue(owner -> {
            ProxyEmbed embed = new ProxyEmbed();
            embed.serverInfo(gld, guild, owner);
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        });
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.GUILDINFO.getName(),
                    "Display the server information.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.GUILDINFO.getName() + "`.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Display the server information. **Example:** `" + guild.getPrefix() + Command.GUILDINFO.getName() + "`.");
        }
    }

}