package listeners.member;

import java.time.LocalDate;

import configuration.cache.Blacklist;
import dao.database.Dao;
import dao.pojo.ChannelLeavePojo;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class MemberLeave extends ListenerAdapter {

    private GuildPojo guild;

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        if (!(boolean) Blacklist.getInstance().getUnchecked(event.getGuild().getId()) && !event.getUser().isBot()) {
            guild = ProxyUtils.getGuildFromCache(event.getGuild());
            LocalDate shield = LocalDate.now().minusDays(guild.getShield());
            LocalDate userTimeCreated = event.getUser().getTimeCreated().toLocalDate();

            if (!userTimeCreated.isAfter(shield)) {
                removeMemberDatas(event);
                sendLeaveMessage(event);
            }
        }
    }

    private void removeMemberDatas(GuildMemberRemoveEvent event) {
        Dao<MemberPojo> memberDao = DaoFactory.getMemberDAO();
        MemberPojo member = memberDao.find(event.getGuild().getId() + "#" + event.getUser().getId());
        memberDao.delete(member);
    }

    private void sendLeaveMessage(GuildMemberRemoveEvent event) {
        if (guild.getChannelLeave() != null) {
            Dao<ChannelLeavePojo> channelLeaveDao = DaoFactory.getChannelLeaveDAO();
            ChannelLeavePojo channelLeave = channelLeaveDao.find(guild.getChannelLeave());
            try {
                if (channelLeave.getMessage() != null && !channelLeave.getEmbed()) {
                    event.getGuild().getTextChannelById(guild.getChannelLeave()).sendMessage(ProxyUtils.getMemberMessageEvent(channelLeave.getMessage(), event.getUser())).queue();

                } else if (channelLeave.getMessage() != null && channelLeave.getEmbed()) {
                    ProxyEmbed embed = new ProxyEmbed();
                    embed.controlGateEvent(event.getUser());
                    event.getGuild().getTextChannelById(guild.getChannelLeave()).sendMessage(embed.getEmbed().build())
                            .append(ProxyUtils.getMemberMessageEvent(channelLeave.getMessage(), event.getUser())).queue();

                } else if (channelLeave.getMessage() == null && channelLeave.getEmbed()) {
                    ProxyEmbed embed = new ProxyEmbed();
                    embed.controlGateEvent(event.getUser());
                    event.getGuild().getTextChannelById(guild.getChannelLeave()).sendMessage(embed.getEmbed().build()).queue();
                }
            } catch (InsufficientPermissionException e) {
            }
        }
    }

}