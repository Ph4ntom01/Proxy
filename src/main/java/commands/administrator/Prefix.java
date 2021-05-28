package commands.administrator;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Prefix extends ACommand {

    public Prefix(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        super(event, args, command, guild);
    }

    public Prefix(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        if (getGuildPrefix().equalsIgnoreCase(getArgs()[1])) {
            sendMessage("Prefix " + "**" + getGuildPrefix() + "**" + " has already been defined.");
        } else {
            ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
            getPGuild().setPrefix(getArgs()[1]);
            guildDao.update(getPGuild());
            sendMessage("Prefix is now: " + "**" + getGuildPrefix() + "**");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Change the prefix of the server.\n\nExample: `" + getGuildPrefix() + getCommandName() + " $`");
        } else {
            sendMessage("Change the prefix of the server. **Example:** `" + getGuildPrefix() + getCommandName() + " $`.");
        }
    }

}