package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constant.Command;
import dao.database.Dao;
import dao.pojo.PGuild;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyUtils;

public class SetPrefix implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public SetPrefix(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        String prefix = ProxyUtils.getArgs(event.getMessage())[1];
        if (prefix.length() <= 2) {
            if (guild.getPrefix().equalsIgnoreCase(prefix)) {
                ProxyUtils.sendMessage(event.getChannel(), "Prefix " + "**" + guild.getPrefix() + "**" + " has already been defined.");
            } else {
                Dao<PGuild> guildDao = DaoFactory.getGuildDAO();
                guild.setPrefix(prefix);
                guildDao.update(guild);
                ProxyUtils.sendMessage(event.getChannel(), "Prefix is now: " + "**" + guild.getPrefix() + "**");
            }
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Two characters maximum are allowed.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.PREFIX.getName(),
                    "Change the prefix of the server.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.PREFIX.getName() + " $`",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Change the prefix of the server. **Example:** `" + guild.getPrefix() + Command.PREFIX.getName() + " $`.");
        }
    }

}