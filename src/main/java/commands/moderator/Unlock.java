package commands.moderator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.pojo.GuildPojo;
import listeners.commands.ModeratorListener;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class Unlock extends ModeratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public Unlock(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            TextChannel channel = event.getGuild().getTextChannelById(ProxyUtils.getMentionnedEntity(MentionType.CHANNEL, event.getMessage(), ProxyUtils.getArgs(event)[1]));
            channel.getRolePermissionOverrides().stream().findAny().ifPresent(role -> {
                try {
                    if (role.getRole().isPublicRole() && (role.getManager().getDeniedPermissions().contains(Permission.MESSAGE_WRITE))) {
                        role.getManager().clear(Permission.MESSAGE_WRITE).queue();
                        ProxyUtils.sendMessage(event, "Lock is now **disabled**.");
                    } else {
                        ProxyUtils.sendMessage(event, "Lock has already been **disabled**.");
                    }
                } catch (InsufficientPermissionException e) {
                    ProxyUtils.sendMessage(event, "Missing permission: **" + Permission.MANAGE_PERMISSIONS.getName() + "**.");
                }
            });
        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event, "Invalid ID or mention.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.UNLOCK.getName(),
                    "Remove lock from a channel by granting @everyone send message permission again.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.UNLOCK.getName() + " #aTextChannel`\n"
                    + "*You can also mention a text channel by his ID*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event, embed);
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event,
                    "Remove lock from a channel by granting ***everyone's role*** send message permission again. "
                    + "**Example:** `" + guild.getPrefix() + Command.UNLOCK.getName() + " #aTextChannel`.");
            // @formatter:on
        }
    }

}