package commands.moderator;

import java.util.List;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.requests.RestAction;

public class Unban extends ACommand {

    public Unban(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        super(event, args, command, guild);
    }

    public Unban(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        RestAction<List<Ban>> command = retrieveBanlist();
        if (command == null) { return; }
        command.queue(banlist -> {
            if (banlist.isEmpty()) {
                sendMessage("No banned member.");
            } else {
                try {
                    banlist.stream().findAny().ifPresent(mentionnedMember -> getGuild().retrieveBanById(getArgs()[1]).queue(bannedMember -> {
                        if (mentionnedMember.getUser().getId().equals(bannedMember.getUser().getId())) {
                            getGuild().unban(mentionnedMember.getUser().getId()).queue();
                            sendMessage("**" + bannedMember.getUser().getName() + "** is successfully unbanned !");
                        }
                    }, ContextException.here(acceptor -> sendMessage("**" + getArgs()[1] + "** is not a member."))));
                } catch (IllegalArgumentException e) {
                    sendMessage("**" + getArgs()[1] + "** is not a member.");
                } catch (ErrorResponseException e) {
                }
            }
        });
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Unban any person who has previously been banned from this server, require an ID.\n\n"
                    + "Example: `" + getGuildPrefix() + getCommandName() + " 500688503617749023`");
            // @formatter:on
        } else {
            // @formatter:off
            sendMessage(
                    "Unban any person who has previously been banned from this server, require an ID. "
                    + "**Example:** `" + getGuildPrefix() + getCommandName() + " 500688503617749023`.");
            // @formatter:on
        }
    }

}