package commands.administrator;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Prefix extends ACommand {

    public Prefix(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    public Prefix(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        if (getPGuildPrefix().equalsIgnoreCase(getArgs()[1])) {
            sendMessage("Prefix " + "**" + getPGuildPrefix() + "**" + " has already been defined.");
        } else {
            ADao<PGuild> guildDao = DaoFactory.getPGuildDAO();
            getPGuild().setPrefix(getArgs()[1]);
            guildDao.update(getPGuild());
            sendMessage("Prefix is now: " + "**" + getPGuildPrefix() + "**");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Change the prefix of the server.\n\nExample: `" + getPGuildPrefix() + getCommandName() + " $`");
        } else {
            sendMessage("Change the prefix of the server. **Example:** `" + getPGuildPrefix() + getCommandName() + " $`.");
        }
    }

}