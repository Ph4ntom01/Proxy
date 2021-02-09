package commands.moderator;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class Purge extends ACommand {

    public Purge(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        super(event, args, command, guild);
    }

    public Purge(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        int days = getIntArg(1);
        if (days > 0 && days <= 30) {
            try {
                getGuild().prune(days).queue(members -> sendMessage("**Kicked members**: " + members + "."));
            } catch (InsufficientPermissionException e) {
                sendMessage("Missing permission: **" + Permission.KICK_MEMBERS.getName() + "**.");
            }
        } else {
            sendMessage("Please enter a number between **1** and **30**.");
        }
    }

    public void purgeByRole() {
        Role role = retrieveMentionnedRole(2);
        if (role == null) { return; }
        int days = getIntArg(1);
        if (days > 0 && days <= 30) {
            try {
                getGuild().prune(days, role).queue(members -> sendMessage("**" + role.getName() + "**: " + members + "."));
            } catch (InsufficientPermissionException e) {
                sendMessage("Missing permission: **" + Permission.KICK_MEMBERS.getName() + "**.");
            } catch (IllegalArgumentException e) {
                sendMessage("Please enter a valid role.");
            }
        } else {
            sendMessage("Please enter a number between **1** and **30**.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
           sendHelpEmbed(
                    "Kick all the members who were offline for at least the number of days you set.\n"
                    + "Members can also be purged by role.\n\n"
                    + "Example: \n\n"
                    + "`" + getGuildPrefix() + getCommandName() + " 5` "
                    + "`" + getGuildPrefix() + getCommandName() + " 5 @aRole`\n\n"
                    + "*You can also mention a role by his ID*.");
            // @formatter:on
        } else {
            // @formatter:off
            sendMessage(
                    "Kick all the members who were offline for at least the number of days you set. "
                    + "**Example:** `" + getGuildPrefix() + getCommandName() + " 5` or `" + getGuildPrefix() + getCommandName() + " 5 @aRole`.");
            // @formatter:on
        }
    }

}