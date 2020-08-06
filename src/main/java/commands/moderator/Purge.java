package commands.moderator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.pojo.GuildPojo;
import listeners.commands.ModeratorListener;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class Purge extends ModeratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public Purge(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            int days = Integer.parseInt(ProxyUtils.getArgs(event.getMessage())[1]);
            if (days > 0 && days <= 30) {
                event.getGuild().prune(days).queue(members -> ProxyUtils.sendMessage(event.getChannel(), "**Kicked members**: " + members + "."));
            } else {
                ProxyUtils.sendMessage(event.getChannel(), "Please enter a number between **1** and **30**.");
            }
        } catch (IllegalArgumentException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Please enter a number between **1** and **30**.");

        } catch (InsufficientPermissionException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Missing permission: **" + Permission.KICK_MEMBERS.getName() + "**.");
        }
    }

    public void purgeByRole() {
        try {
            Role role = event.getGuild().getRoleById(ProxyUtils.getMentionnedEntity(MentionType.ROLE, event.getMessage(), ProxyUtils.getArgs(event.getMessage())[1]));
            int days = Integer.parseInt(ProxyUtils.getArgs(event.getMessage())[2]);
            if (days > 0 && days <= 30) {
                event.getGuild().prune(days, role).queue(members -> ProxyUtils.sendMessage(event.getChannel(), "**" + role.getName() + "**: " + members + "."));
            } else {
                ProxyUtils.sendMessage(event.getChannel(), "Please enter a number between **1** and **30**.");
            }
        } catch (InsufficientPermissionException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Missing permission: **" + Permission.KICK_MEMBERS.getName() + "**.");

        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.PURGE.getName(),
                    "Kick all the members who were offline for at least the number of days you set.\n"
                    + "Members can also be purged by role.\n\n"
                    + "Example: \n\n"
                    + "`" + guild.getPrefix() + Command.PURGE.getName() + " 5` "
                    + "`" + guild.getPrefix() + Command.PURGE.getName() + " 5 @aRole`\n\n"
                    + "*You can also mention a role by his ID*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event.getChannel(),
                    "Kick all the members who were offline for at least the number of days you set. "
                    + "**Example:** `" + guild.getPrefix() + Command.PURGE.getName() + " 5` or `" + guild.getPrefix() + Command.PURGE.getName() + " 5 @aRole`.");
            // @formatter:on
        }
    }

}