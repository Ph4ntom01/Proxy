package commands.user;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.pojo.GuildPojo;
import listeners.commands.UserListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class Uptime extends UserListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public Uptime(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        ProxyEmbed embed = new ProxyEmbed();
        embed.uptime();
        ProxyUtils.sendEmbed(event, embed);
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.UPTIME.getName(),
                    "Display the percentage of time the bot has been available.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.UPTIME.getName() + "`.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event, embed);
        } else {
            ProxyUtils.sendMessage(event, "Display the percentage of time the bot has been available. **Example:** `" + guild.getPrefix() + Command.UPTIME.getName() + "`.");
        }
    }

}