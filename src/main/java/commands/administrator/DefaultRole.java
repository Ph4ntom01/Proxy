package commands.administrator;

import java.util.Objects;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class DefaultRole extends ACommand {

    public DefaultRole(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    public DefaultRole(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        Role role = retrieveMentionnedRole(1);
        if (role == null) { return; }
        if (Objects.equals(role.getIdLong(), getPGuild().getDefaultRole())) {
            sendMessage("Default role **" + role.getName() + "** has already been defined.");
        } else {
            ADao<PGuild> guildDao = DaoFactory.getPGuildDAO();
            getPGuild().setDefaultRole(role.getIdLong());
            guildDao.update(getPGuild());
            sendMessage("Default role is now **" + role.getName() + "**.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Automatically assign a role when a member joins the server.\n\n"
                    + "Example: `" + getPGuildPrefix() + getCommandName() + " @aRole`\n\n"
                    + "*You can also mention a role by his ID*.");
            // @formatter:on
        } else {
            sendMessage("Automatically assign a role when a member joins the server. **Example:** `" + getPGuildPrefix() + getCommandName() + " @aRole`.");
        }
    }

}