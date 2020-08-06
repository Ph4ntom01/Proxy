package commands.user;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.pojo.GuildPojo;
import listeners.commands.UserListener;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class Banlist extends UserListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public Banlist(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            event.getGuild().retrieveBanList().queue(banlist -> {
                if (banlist.isEmpty()) {
                    ProxyUtils.sendMessage(event.getChannel(), "No banned member.");
                } else {
                    ProxyEmbed embed = new ProxyEmbed();
                    embed.banList(banlist, guild.getPrefix());
                    ProxyUtils.sendEmbed(event.getChannel(), embed);
                }
            });
        } catch (InsufficientPermissionException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Missing permission: **" + Permission.BAN_MEMBERS.getName() + "**.");
        }
    }

    public void consultBannedMember() {
        try {
            event.getGuild().retrieveBanList().queue(banlist -> {
                try {
                    int value = Integer.parseInt(ProxyUtils.getArgs(event.getMessage())[1]);
                    if (value > 0 && value <= banlist.size()) {
                        if (banlist.isEmpty()) {
                            ProxyUtils.sendMessage(event.getChannel(), "No banned member.");
                        } else {
                            ProxyEmbed embed = new ProxyEmbed();
                            embed.bannedMemberInfo(banlist, value - 1);
                            ProxyUtils.sendEmbed(event.getChannel(), embed);
                        }
                    } else {
                        ProxyUtils.sendMessage(event.getChannel(), "Please enter a number from the banlist.");
                    }
                } catch (NumberFormatException e) {
                    // @formatter:off
                    ProxyUtils.sendMessage(event.getChannel(),
                            "To consult the banlist, use the command: `" + guild.getPrefix() + Command.BANLIST.getName()
                            + "`, you can also consult the banned member information by using the command "
                            + "**" + guild.getPrefix() + Command.BANLIST.getName() + " [a number from the banlist]**.");
                    // @formatter:on
                }
            });
        } catch (InsufficientPermissionException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Missing permission: **" + Permission.BAN_MEMBERS.getName() + "**.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.BANLIST.getName(),
                    "Display the banned members.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.BANLIST.getName() + "`",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Display the banned members. **Example:** `" + guild.getPrefix() + Command.BANLIST.getName() + "`.");
        }
    }

}