package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import configuration.constants.Permissions;
import dao.database.Dao;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import factory.DaoFactory;
import listeners.commands.AdministratorListener;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class SetPermission extends AdministratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;
    private Permissions permission;

    public SetPermission(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    public SetPermission(GuildMessageReceivedEvent event, GuildPojo guild, Permissions permission) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
        this.permission = permission;
    }

    @Override
    public void execute() {
        if (event.getAuthor().getId().equals(event.getGuild().getOwnerId())) {
            try {
                event.getGuild().retrieveMemberById(ProxyUtils.getMentionnedEntity(MentionType.USER, event.getMessage(), ProxyUtils.getArgs(event)[1]), false)
                        .queue(mentionnedMember -> {

                            if (mentionnedMember.getUser().isBot()) {
                                ProxyUtils.sendMessage(event, "Impossible to set a permission for a bot.");
                            } else {
                                String userTag = mentionnedMember.getUser().getAsTag();
                                MemberPojo member = ProxyUtils.getMemberFromCache(mentionnedMember);

                                if (permission.getLevel() == member.getPermLevel()) {
                                    ProxyUtils.sendMessage(event, "**" + userTag + "** already has this permission.");
                                } else {
                                    member.setPermLevel(permission.getLevel());
                                    Dao<MemberPojo> memberDao = DaoFactory.getMemberDAO();
                                    memberDao.update(member);
                                    ProxyUtils.sendMessage(event, "**" + userTag + "**" + " is now " + "**" + permission.getName().toLowerCase() + "**" + ".");
                                }
                            }
                        }, ContextException.here(acceptor -> ProxyUtils.sendMessage(event, "Invalid ID or mention.")));
            } catch (IllegalArgumentException | NullPointerException e) {
                ProxyUtils.sendMessage(event, "Invalid ID or mention.");
            }
        } else {
            ProxyUtils.sendMessage(event, "Only the guild owner has the ability to set a permission.");
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
            ProxyUtils.sendEmbed(event, embed);
            // @formatter:on
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event, "Set a permission for a mentionned member. "
                    + "**Example:** `" + guild.getPrefix() + Command.SETADMIN.getName() + " @aMember`.\n"
                    + "**This command can only be used by the server owner**.");
            // @formatter:on
        }
    }

}