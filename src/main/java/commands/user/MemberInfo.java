package commands.user;

import java.awt.Color;

import commands.CommandManager;
import configuration.constant.Command;
import configuration.constant.ID;
import configuration.constant.Permissions;
import dao.pojo.PGuildMember;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import proxy.utility.ProxyCache;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyString;
import proxy.utility.ProxyUtils;

public class MemberInfo implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public MemberInfo(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            event.getGuild().retrieveMemberById(ProxyString.getMentionnedEntity(MentionType.USER, event.getMessage(), ProxyUtils.getArgs(event.getMessage())[1]), false).queue(gMember -> {
                if (gMember.getUser().isBot()) {
                    if (gMember.getId().equals(ID.PROXY.getId())) {
                        ProxyUtils.selfbotEmbed(event.getJDA(), event.getGuild(), guild, event.getChannel());
                    } else {
                        ProxyEmbed embed = new ProxyEmbed();
                        embed.botInfo(gMember);
                        ProxyUtils.sendEmbed(event.getChannel(), embed);
                    }
                } else {
                    PGuildMember mentionnedMember = ProxyCache.getGuildMember(gMember);
                    ProxyEmbed embed = new ProxyEmbed();
                    Permissions memberPermission = null;
                    switch (mentionnedMember.getPermId()) {
                    case 3:
                        memberPermission = Permissions.ADMINISTRATOR;
                        break;
                    case 2:
                        memberPermission = Permissions.MODERATOR;
                        break;
                    default:
                        memberPermission = Permissions.USER;
                    }
                    embed.memberInfo(gMember, memberPermission);
                    ProxyUtils.sendEmbed(event.getChannel(), embed);
                }
            }, ContextException.here(acceptor -> ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.")));

        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.MEMBERINFO.getName(),
                    "Display the member information.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.MEMBERINFO.getName() + " @aMember`\n\n"
                    + "*You can also mention a member by his ID*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Display the member information. **Example:** `" + guild.getPrefix() + Command.MEMBERINFO.getName() + " @aMember`.");
        }
    }

}