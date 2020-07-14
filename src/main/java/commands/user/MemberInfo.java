package commands.user;

import java.awt.Color;
import java.util.List;

import com.google.common.collect.Lists;

import commands.CommandManager;
import configuration.constants.Command;
import configuration.constants.ID;
import configuration.constants.Permissions;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import listeners.commands.UserListener;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class MemberInfo extends UserListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public MemberInfo(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            event.getGuild().retrieveMemberById(ProxyUtils.getMentionnedEntity(MentionType.USER, event.getMessage(), ProxyUtils.getArgs(event)[1]), false).queue(member -> {
                if (member.getUser().isBot()) {
                    if (member.getId().equals(ID.PROXY.getId())) {
                        ProxyUtils.selfbotEmbed(event, guild);
                    } else {
                        ProxyEmbed embed = new ProxyEmbed();
                        embed.botInfo(member);
                        ProxyUtils.sendEmbed(event, embed);
                    }
                } else {
                    event.getGuild().retrieveMemberById(member.getId()).queue(user -> {
                        MemberPojo mentionnedMember = ProxyUtils.getMemberFromCache(user);
                        List<Permissions> permissions = Lists.newArrayList(Permissions.values());
                        ProxyEmbed embed = new ProxyEmbed();
                        embed.memberInfo(member, permissions.stream().filter(permission -> permission.getLevel() == mentionnedMember.getPermLevel()).findFirst().orElse(null));
                        ProxyUtils.sendEmbed(event, embed);
                    });
                }
            }, ContextException.here(acceptor -> ProxyUtils.sendMessage(event, "Invalid ID or mention.")));

        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event, "Invalid ID or mention.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.MEMBER_INFO.getName(),
                    "Display the member information.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.MEMBER_INFO.getName() + " @aMember`\n\n"
                    + "*You can also mention a member by his ID*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event, embed);
        } else {
            ProxyUtils.sendMessage(event, "Display the member information. **Example:** `" + guild.getPrefix() + Command.MEMBER_INFO.getName() + " @aMember`.");
        }
    }

}