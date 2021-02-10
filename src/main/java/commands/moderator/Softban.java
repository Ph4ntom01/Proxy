package commands.moderator;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import commands.ACommand;
import configuration.cache.EGuildMemberCache;
import configuration.constant.ECommand;
import configuration.constant.EID;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;

public class Softban extends ACommand {

    private PGuildMember messageAuthor;

    public Softban(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild, PGuildMember messageAuthor) {
        super(event, args, command, guild);
        this.messageAuthor = messageAuthor;
    }

    public Softban(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        RestAction<Member> command = retrieveMentionnedMember(1, false);
        if (command == null) { return; }
        command.queue(mentionnedMember -> {
            if (mentionnedMember.getUser().isBot()) {
                softban(mentionnedMember);
            } else {
                PGuildMember mentionnedPGMember = EGuildMemberCache.INSTANCE.getGuildMember(mentionnedMember);

                if (Objects.equals(mentionnedPGMember.getId(), getAuthor().getIdLong())) {
                    sendMessage("Impossible to kick yourself.");

                } else if (messageAuthor.getPermission().getLevel() <= mentionnedPGMember.getPermission().getLevel()) {
                    sendMessage("Impossible to kick a member with the same or a higher permission than yours.");

                } else {
                    softban(mentionnedMember);
                }
            }
        }, ContextException.here(acceptor -> sendMessage("**" + getArgs()[1] + "** is not a member.")));
    }

    private void softban(Member mentionnedMember) {
        try {
            getGuild().ban(mentionnedMember.getId(), 1).queue();
            getGuild().unban(mentionnedMember.getId()).queueAfter(500, TimeUnit.MILLISECONDS);
            sendMessage("**" + mentionnedMember.getUser().getAsTag() + "** is successfully kicked !");

        } catch (InsufficientPermissionException e) {
            sendMessage("Missing permission: **" + Permission.BAN_MEMBERS.getName() + "**.");

        } catch (HierarchyException e) {
            sendMessage("**" + mentionnedMember.getUser().getAsTag() + "** has the same or a higher permission than <@" + EID.BOT_ID.getId() + "> !");

        } catch (IllegalArgumentException e) {
        } catch (ErrorResponseException e) {
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Kick a specified member and deletes his past messages since one day.\n\n"
                    + "Example: `" + getGuildPrefix() + getCommandName() + " @aMember`\n"
                    + "*You can also mention a member by his ID*.");
            // @formatter:on
        } else {
            // @formatter:off
            sendMessage(
                    "Kick a specified member and deletes his past messages since one day. "
                    + "**Example:** `" + getGuildPrefix() + getCommandName() + " @aMember`.");        
            // @formatter:on
        }
    }

}