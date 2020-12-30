package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constant.Command;
import dao.database.Dao;
import dao.pojo.PGuild;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyString;
import proxy.utility.ProxyUtils;

public class Shield implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public Shield(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            int days = Integer.parseInt(ProxyUtils.getArgs(event.getMessage())[1]);
            if (days >= 0 && days <= 30) {
                if (guild.getShield() == days) {
                    String message = (days == 0) ? ("Shield has already been **disabled**.") : ("Shield has already been set to **" + days + "** " + ProxyString.day(days) + ".");
                    ProxyUtils.sendMessage(event.getChannel(), message);
                } else {
                    String message = (days == 0)
                            ? "Shield is now **disabled**, the bot will no longer kick accounts that have been created less than **" + guild.getShield() + "** " + ProxyString.day(guild.getShield())
                                    + " ago."
                            : "Shield is now set to **" + days + "** " + ProxyString.day(days) + ".";
                    Dao<PGuild> guildDao = DaoFactory.getGuildDAO();
                    guild.setShield(days);
                    guildDao.update(guild);
                    ProxyUtils.sendMessage(event.getChannel(), message);
                }
            } else {
                ProxyUtils.sendMessage(event.getChannel(), "Please enter a number between **0** and **30**. You can provide protection of **30** days maximum.");
            }
        } catch (NumberFormatException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Please enter a number between **0** and **30**. You can provide protection of **30** days maximum.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.SHIELD.getName(),
                    "Provide protection against fake accounts who join your server, the bot will kick accounts that have "
                  + "been created less than **[0 - 30]** days ago.\n\nExample:\n\n`" + guild.getPrefix() + Command.SHIELD.getName()
                  + " 0` *disables the shield.*\n`" + guild.getPrefix() + Command.SHIELD.getName() + " 3` *kicks an account created less than 3 days*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event.getChannel(),
                    "Provide protection against fake accounts who join your server. "
                    + "**Example:** `" + guild.getPrefix() + Command.SHIELD.getName() + " 0` *disables the shield*.");
            // @formatter:on
        }
    }

}