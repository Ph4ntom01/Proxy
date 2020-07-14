package commands.moderator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import configuration.constants.Permissions;
import dao.database.Dao;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import factory.DaoFactory;
import listeners.commands.ModeratorListener;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class Bans extends ModeratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;
    private MemberPojo author;

    public Bans(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    public Bans(GuildMessageReceivedEvent event, GuildPojo guild, MemberPojo author) {
        super(event, guild, author);
        this.event = event;
        this.guild = guild;
        this.author = author;
    }

    @Override
    public void execute() {
        try {
            event.getGuild().retrieveMemberById(ProxyUtils.getMentionnedEntity(MentionType.USER, event.getMessage(), ProxyUtils.getArgs(event)[1]), false).queue(member -> {
                try {
                    if (member.getUser().isBot()) {
                        ban(member);
                    } else {
                        Dao<MemberPojo> memberDao = DaoFactory.getMemberDAO();
                        MemberPojo memberPojo = memberDao.find(event.getGuild().getId() + "#" + member.getId());

                        if (memberPojo.getId().equals(event.getAuthor().getId())) {
                            ProxyUtils.sendMessage(event, "Impossible to ban yourself");

                        } else if (((author.getPermLevel() == Permissions.MODERATOR.getLevel()) && (memberPojo.getPermLevel() == Permissions.MODERATOR.getLevel()))
                                || ((author.getPermLevel() == Permissions.ADMINISTRATOR.getLevel()) && memberPojo.getPermLevel() == Permissions.ADMINISTRATOR.getLevel())) {
                            ProxyUtils.sendMessage(event, "Impossible to ban a member with the same or a higher permission than yours.");

                        } else if (author.getPermLevel() == Permissions.MODERATOR.getLevel() && (memberPojo.getPermLevel() == Permissions.ADMINISTRATOR.getLevel())) {
                            ProxyUtils.sendMessage(event, "Impossible to ban an administrator.");
                        } else {
                            ban(member);
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    ProxyUtils.sendMessage(event, "Invalid ID or mention.");
                }
            }, ContextException.here(acceptor -> ProxyUtils.sendMessage(event, "Invalid ID or mention.")));

        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event, "Invalid ID or mention.");
        }
    }

    private void ban(Member mentionnedMember) {
        try {
            event.getGuild().ban(mentionnedMember.getId(), 1).queue();
            ProxyUtils.sendMessage(event, "**" + mentionnedMember.getUser().getAsTag() + "** is successfully banned !");

        } catch (InsufficientPermissionException e) {
            ProxyUtils.sendMessage(event, "Missing permission: **" + Permission.BAN_MEMBERS.getName() + "**.");

        } catch (HierarchyException e) {
            ProxyUtils.sendMessage(event, "Impossible to ban a member with the same or a higher permission than yours.");

        } catch (IllegalArgumentException e) {
        } catch (ErrorResponseException e) {
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.BAN.getName(),
                    "Ban a specified member from your server.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.BAN.getName() + " @aMember`\n"
                    + "*You can also mention a member by his ID*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event, embed);
        } else {
            ProxyUtils.sendMessage(event, "Ban a specified member from your server. **Example:** `" + guild.getPrefix() + Command.BAN.getName() + " @aMember`.");
        }
    }

}