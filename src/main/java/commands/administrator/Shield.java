package commands.administrator;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Shield extends ACommand {

    public Shield(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    public Shield(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        int days = getIntArg(1);
        if (days >= 0 && days <= 30) {
            if (getPGuild().getShield() == days) {
                String message = (days == 0) ? ("Shield has already been **disabled**.") : ("Shield has already been set to **" + days + "** " + "day(s).");
                sendMessage(message);
            } else {
                // @formatter:off
                String message = (days == 0)
                        ? "Shield is now **disabled**, the bot will no longer kick accounts that have been created less than **" + getPGuild().getShield() + "** day(s) ago."
                        : "Shield is now set to **" + days + "** day(s).";
                // @formatter:on
                ADao<PGuild> guildDao = DaoFactory.getPGuildDAO();
                getPGuild().setShield(days);
                guildDao.update(getPGuild());
                sendMessage(message);
            }
        } else {
            sendMessage("Please enter a number between **0** and **30**. You can provide protection of **30** days maximum.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Provide protection against fake accounts who join your server, the bot will kick accounts that have "
                  + "been created less than **[0 - 30]** days ago.\n\nExample:\n\n`" + getPGuildPrefix() + getCommandName()
                  + " 0` *disables the shield.*\n`" + getPGuildPrefix() + getCommandName() + " 3` *kicks an account created less than 3 days*.");
            // @formatter:on
        } else {
            // @formatter:off
            sendMessage(
                    "Provide protection against fake accounts who join your server. "
                    + "**Example:** `" + getPGuildPrefix() + getCommandName() + " 0` *disables the shield*.");
            // @formatter:on
        }
    }

}