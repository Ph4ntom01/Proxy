package commands.moderator;

import java.util.Objects;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
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

public class Bans extends ACommand {

    private PGuildMember messageAuthor;

    public Bans(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild, PGuildMember messageAuthor) {
        super(event, args, command, guild);
        this.messageAuthor = messageAuthor;
    }

    public Bans(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        RestAction<Member> command = retrieveMentionnedMember(1, false);
        if (command == null) { return; }
        command.queue(mentionnedMember -> {
            if (mentionnedMember.getUser().isBot()) {
                ban(mentionnedMember);
            } else {
                ADao<PGuildMember> gMemberDao = DaoFactory.getGuildMemberDAO();
                PGuildMember mentionnedPGMember = gMemberDao.find(getGuild().getIdLong(), mentionnedMember.getIdLong());

                if (Objects.equals(mentionnedPGMember.getId(), getAuthor().getIdLong())) {
                    sendMessage("Impossible to ban yourself");

                } else if (messageAuthor.getPermission().getLevel() <= mentionnedPGMember.getPermission().getLevel()) {
                    sendMessage("Impossible to ban a member with the same or a higher permission than yours.");

                } else {
                    ban(mentionnedMember);
                }
            }
        }, ContextException.here(acceptor -> sendMessage("**" + getArgs()[1] + "** is not a member.")));
    }

    private void ban(Member mentionnedMember) {
        try {
            getGuild().ban(mentionnedMember.getId(), 1).queue();
            sendMessage("**" + mentionnedMember.getUser().getAsTag() + "** is successfully banned !");

        } catch (InsufficientPermissionException e) {
            sendMessage("Missing permission: **" + Permission.BAN_MEMBERS.getName() + "**.");

        } catch (HierarchyException e) {
            sendMessage("Impossible to ban a member with the same or a higher permission than yours.");

        } catch (IllegalArgumentException e) {
        } catch (ErrorResponseException e) {
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Ban a specified member from your server.\n\n"
                    + "Example: `" + getGuildPrefix() + getCommandName() + " @aMember`\n"
                    + "*You can also mention a member by his ID*.");
            // @formatter:on
        } else {
            sendMessage("Ban a specified member from your server. **Example:** `" + getGuildPrefix() + getCommandName() + " @aMember`.");
        }
    }

}