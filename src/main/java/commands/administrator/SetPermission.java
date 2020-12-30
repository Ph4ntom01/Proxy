package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constant.Command;
import configuration.constant.Permissions;
import dao.database.Dao;
import dao.pojo.PGuildMember;
import dao.pojo.PGuild;
import factory.DaoFactory;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import proxy.utility.ProxyCache;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyString;
import proxy.utility.ProxyUtils;

public class SetPermission implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;
    private Permissions permission;

    public SetPermission(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    public SetPermission(GuildMessageReceivedEvent event, PGuild guild, Permissions permission) {
        this.event = event;
        this.guild = guild;
        this.permission = permission;
    }

    @Override
    public void execute() {
        if (event.getAuthor().getId().equals(event.getGuild().getOwnerId())) {
            try {
                event.getGuild().retrieveMemberById(ProxyString.getMentionnedEntity(MentionType.USER, event.getMessage(), ProxyUtils.getArgs(event.getMessage())[1]), false).queue(mentionnedMember -> {

                    if (mentionnedMember.getUser().isBot()) {
                        ProxyUtils.sendMessage(event.getChannel(), "Impossible to set a permission for a bot.");
                    } else {
                        String userTag = mentionnedMember.getUser().getAsTag();
                        PGuildMember gMember = ProxyCache.getGuildMember(mentionnedMember);

                        if (permission.getLevel() == gMember.getPermId()) {
                            ProxyUtils.sendMessage(event.getChannel(), "**" + userTag + "** already has this permission.");
                        } else {
                            gMember.setPermId(permission.getLevel());
                            Dao<PGuildMember> gMemberDao = DaoFactory.getGuildMemberDAO();
                            gMemberDao.update(gMember);
                            ProxyUtils.sendMessage(event.getChannel(), "**" + userTag + "**" + " is now " + "**" + permission.getName().toLowerCase() + "**" + ".");
                        }
                    }
                }, ContextException.here(acceptor -> ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.")));
            } catch (IllegalArgumentException | NullPointerException e) {
                ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.");
            }
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Only the guild owner has the ability to set a permission.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            ProxyEmbed embed = new ProxyEmbed();
            embed.help(Command.SETADMIN.getName(),
                    "Set a permission for a mentionned member.\n\n- "
                    + Permissions.USER.getName() + ": `" + guild.getPrefix() + Command.SETUSER.getName() + "`\n\n- "
                    + Permissions.MODERATOR.getName() + ": `" + guild.getPrefix() + Command.SETMODO.getName() + "`\n\n- "
                    + Permissions.ADMINISTRATOR.getName() + ": `" + guild.getPrefix() + Command.SETADMIN.getName() + "`\n\n"
                    + "Example: `" + guild.getPrefix() + Command.SETADMIN.getName() + " @aMember`.\n\n"
                    + "*You can also mention a member by his ID*.\n"
                    + "**This command can only be used by the server owner**.",
                    Color.ORANGE);
            ProxyUtils.sendEmbed(event.getChannel(), embed);
            // @formatter:on
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event.getChannel(), "Set a permission for a mentionned member. "
                    + "**Example:** `" + guild.getPrefix() + Command.SETADMIN.getName() + " @aMember`.\n"
                    + "**This command can only be used by the server owner**.");
            // @formatter:on
        }
    }

}