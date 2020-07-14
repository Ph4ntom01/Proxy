package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.database.Dao;
import dao.pojo.GuildPojo;
import factory.DaoFactory;
import listeners.commands.AdministratorListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class SetPrefix extends AdministratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public SetPrefix(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        String prefix = ProxyUtils.getArgs(event)[1];
        if (prefix.length() <= 2) {
            if (guild.getPrefix().equalsIgnoreCase(prefix)) {
                ProxyUtils.sendMessage(event, "Prefix " + "**" + guild.getPrefix() + "**" + " has already been defined.");
            } else {
                Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                guild.setPrefix(prefix);
                guildDao.update(guild);
                ProxyUtils.sendMessage(event, "Prefix is now: " + "**" + guild.getPrefix() + "**");
            }
        } else {
            ProxyUtils.sendMessage(event, "Two characters maximum are allowed.");
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
            ProxyUtils.sendEmbed(event, embed);
        } else {
            ProxyUtils.sendMessage(event, "Change the prefix of the server. **Example:** `" + guild.getPrefix() + Command.PREFIX.getName() + " $`.");
        }
    }

}