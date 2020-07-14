package listeners.member;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import configuration.cache.Blacklist;
import configuration.constants.Permissions;
import dao.database.Dao;
import dao.pojo.ChannelJoinPojo;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import factory.DaoFactory;
import factory.PojoFactory;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class MemberJoin extends ListenerAdapter {

    private GuildPojo guild;

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!(boolean) Blacklist.getInstance().getUnchecked(event.getGuild().getId()) && !event.getUser().isBot()) {
            guild = ProxyUtils.getGuildFromCache(event.getGuild());
            LocalDate shield = LocalDate.now().minusDays(guild.getShield());
            LocalDate userTimeCreated = event.getUser().getTimeCreated().toLocalDate();

            if (userTimeCreated.isAfter(shield)) {
                try {
                    event.getMember().kick().queue();
                } catch (InsufficientPermissionException e) {
                } catch (HierarchyException e) {
                }
            } else {
                addMemberDatas(event);
                addDefRole(event);
                sendWelcomeMessage(event);
            }
        }
    }

    private void addMemberDatas(GuildMemberJoinEvent event) {
        Dao<MemberPojo> memberDao = DaoFactory.getMemberDAO();
        MemberPojo member = PojoFactory.getMember();
        member.setGuildId(event.getGuild().getId());
        member.setId(event.getUser().getId());
        member.setName(event.getUser().getName());
        member.setPermLevel(Permissions.USER.getLevel());
        memberDao.create(member);
    }

    private void addDefRole(GuildMemberJoinEvent event) {
        if (guild.getDefaultRole() != null && !guild.getDefaultRole().isEmpty()) {

            for (int i = 0; i < event.getGuild().getRoles().size(); i++) {

                if (event.getGuild().getRoles().get(i).getId().equals(guild.getDefaultRole())) {
                    try {
                        Role defaultRole = event.getGuild().getRoleById(guild.getDefaultRole());
                        event.getGuild().addRoleToMember(event.getMember(), defaultRole).queue();
                    } catch (HierarchyException e) {
                    } catch (ErrorResponseException e) {
                    }
                }
            }
        }
    }

    private void sendWelcomeMessage(GuildMemberJoinEvent event) {
        if (guild.getChannelJoin() != null) {
            Dao<ChannelJoinPojo> channelJoinDao = DaoFactory.getChannelJoinDAO();
            ChannelJoinPojo channelJoin = channelJoinDao.find(guild.getChannelJoin());
            try {
                if (channelJoin.getMessage() != null && !channelJoin.getEmbed()) {
                    event.getGuild().getTextChannelById(guild.getChannelJoin()).sendMessage(ProxyUtils.getMemberMessageEvent(channelJoin.getMessage(), event.getUser())).queue();
                }
                if (channelJoin.getEmbed()) {
                    event.getGuild().retrieveMemberById(event.getMember().getId()).queueAfter(1, TimeUnit.SECONDS, member -> {
                        ProxyEmbed embed = new ProxyEmbed();
                        embed.memberJoin(member);
                        event.getGuild().getTextChannelById(guild.getChannelJoin()).sendMessage(embed.getEmbed().build())
                                .append(ProxyUtils.getMemberMessageEvent(channelJoin.getMessage(), event.getUser())).queue();
                    });
                }
            } catch (InsufficientPermissionException e) {
            }
        }
    }

}