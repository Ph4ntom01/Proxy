package commands.moderator;

import java.awt.Color;
import java.util.List;

import commands.CommandManager;
import configuration.constant.Command;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyUtils;

public class Unban implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public Unban(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            event.getGuild().retrieveBanList().queue(banlist -> unban(banlist));
        } catch (InsufficientPermissionException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Missing permission: **" + Permission.BAN_MEMBERS.getName() + "**.");
        }
    }

    private void unban(List<Ban> banlist) {
        try {
            if (banlist.isEmpty()) {
                ProxyUtils.sendMessage(event.getChannel(), "No banned member.");
            } else {
                banlist.stream().findAny().ifPresent(mbr -> {
                    event.getGuild().retrieveBanById(ProxyUtils.getArgs(event.getMessage())[1]).queue(bannedMember -> {

                        if (mbr.getUser().getId().equals(bannedMember.getUser().getId())) {
                            event.getGuild().unban(mbr.getUser().getId()).queue();
                            ProxyUtils.sendMessage(event.getChannel(), "**" + bannedMember.getUser().getName() + "** is successfully unbanned !");
                        }
                    }, ContextException.here(e -> ProxyUtils.sendMessage(event.getChannel(), "Invalid ID.")));
                });
            }
        } catch (IllegalArgumentException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Invalid ID.");
        } catch (ErrorResponseException e) {
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.UNBAN.getName(),
                    "Unban any person who has previously been banned from this server, require an ID.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.UNBAN.getName() + " 500688503617749023`",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event.getChannel(),
                    "Unban any person who has previously been banned from this server, require an ID. "
                    + "**Example:** `" + guild.getPrefix() + Command.UNBAN.getName() + " 500688503617749023`.");
            // @formatter:on
        }
    }

}