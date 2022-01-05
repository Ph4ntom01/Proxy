package commands.guildowner;

import commands.ACommand;
import configuration.cache.EGuildMemberCache;
import configuration.constant.ECommand;
import configuration.constant.EID;
import configuration.constant.EPermission;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.requests.RestAction;

public class SetPermission extends ACommand {

    public SetPermission(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    public SetPermission(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        EPermission permission = EPermission.getPermission(getCommand());
        if (getAuthor().getId().equals(getGuild().getOwnerId())) {
            RestAction<Member> command = retrieveMentionnedMember(1, false);
            if (command == null) { return; }
            command.queue(mentionnedMember -> {
                if (mentionnedMember.getUser().isBot()) {
                    sendMessage("Impossible to set a permission for a bot.");
                } else if (mentionnedMember.isOwner()) {
                    sendMessage("Impossible to set a permission for yourself.");
                } else if (mentionnedMember.getId().equals(EID.BOT_OWNER.getId())) {
                    sendMessage("Impossible to set a permission for the bot owner.");
                } else {
                    String userTag = mentionnedMember.getUser().getAsTag();
                    EGuildMemberCache.INSTANCE.getPGuildMemberAsync(mentionnedMember).thenAcceptAsync(member -> {
                        if (permission == member.getPermission()) {
                            sendMessage("**" + userTag + "** already has this permission.");
                        } else {
                            member.setPermission(permission);
                            ADao<PGuildMember> gMemberDao = DaoFactory.getPGuildMemberDAO();
                            gMemberDao.update(member);
                            sendMessage("**" + userTag + "**" + " is now " + "**" + permission.getName().toLowerCase() + "**" + ".");
                        }
                    });
                }
            }, ContextException.here(acceptor -> sendMessage("**" + getArgs()[1] + "** is not a member.")));
        } else {
            sendMessage("Only the guild owner has the ability to set a permission.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Set a permission for a mentionned member.\n\n- "
                    + EPermission.USER.getName() + ": `" + getPGuildPrefix() + ECommand.SETUSER.getName() + "`\n\n- "
                    + EPermission.MODERATOR.getName() + ": `" + getPGuildPrefix() + ECommand.SETMODO.getName() + "`\n\n- "
                    + EPermission.ADMINISTRATOR.getName() + ": `" + getPGuildPrefix() + ECommand.SETADMIN.getName() + "`\n\n"
                    + "Example: `" + getPGuildPrefix() + ECommand.SETADMIN.getName() + " @aMember`.\n\n"
                    + "*You can also mention a member by his ID*.\n"
                    + "**This command can only be used by the server owner**.");
            // @formatter:on
        } else {
            // @formatter:off
            sendMessage("Set a permission for a mentionned member. "
                    + "**Example:** `" + getPGuildPrefix() + getCommandName() + " @aMember`.\n"
                    + "**This command can only be used by the guild owner**.");
            // @formatter:on
        }
    }

}