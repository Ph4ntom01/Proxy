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

public class Shield extends AdministratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public Shield(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            int days = Integer.parseInt(ProxyUtils.getArgs(event)[1]);
            if (days >= 0 && days <= 30) {
                if (guild.getShield() == days) {
                    String message = (days == 0) ? ("Shield has already been **disabled**.") : ("Shield has already been set to **" + days + "** " + ProxyUtils.day(days) + ".");
                    ProxyUtils.sendMessage(event, message);
                } else {
                    String message = (days == 0)
                            ? "Shield is now **disabled**, the bot will no longer kick accounts that have been created less than **" + guild.getShield() + "** days ago."
                            : "Shield is now set to **" + days + "** " + ProxyUtils.day(days) + ".";
                    Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                    guild.setShield(days);
                    guildDao.update(guild);
                    ProxyUtils.sendMessage(event, message);
                }
            } else {
                ProxyUtils.sendMessage(event, "Please enter a number between **0** and **30**. You can provide protection of **30** days maximum.");
            }
        } catch (NumberFormatException e) {
            ProxyUtils.sendMessage(event, "Please enter a number between **0** and **30**. You can provide protection of **30** days maximum.");
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
            ProxyUtils.sendEmbed(event, embed);
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event,
                    "Provide protection against fake accounts who join your server. "
                    + "**Example:** `" + guild.getPrefix() + Command.SHIELD.getName() + " 0` *disables the shield*.");
            // @formatter:on
        }
    }

}