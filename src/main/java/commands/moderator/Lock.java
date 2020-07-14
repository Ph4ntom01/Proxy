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

public class Lock extends ModeratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public Lock(GuildMessageReceivedEvent event, GuildPojo guild) {
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
                    if (role.getRole().isPublicRole() && (role.getManager().getInheritedPermissions().contains(Permission.MESSAGE_WRITE)
                            || role.getManager().getAllowedPermissions().contains(Permission.MESSAGE_WRITE))) {

                        role.getManager().deny(Permission.MESSAGE_WRITE).queue();
                        ProxyUtils.sendMessage(event, "Lock is now **enabled**.");
                    } else {
                        ProxyUtils.sendMessage(event, "Lock has already been **enabled**.");
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
            embed.help(Command.LOCK.getName(),
                    "Remove send message permission from @everyone for a mentionned text channel.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.LOCK.getName() + " #aTextChannel`\n"
                    + "*You can also mention a text channel by his ID*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event, embed);
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event,
                    "Remove send message permission from ***everyone's role***. "
                    + "**Example:** `" + guild.getPrefix() + Command.LOCK.getName() + " #aTextChannel`.");
            // @formatter:on
        }
    }

}