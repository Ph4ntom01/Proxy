package listeners.member;

import java.time.LocalDate;

import dao.database.Dao;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import dao.pojo.PLeaveChannel;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.utility.ProxyCache;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyString;

public class MemberLeave extends ListenerAdapter {

    private PGuild guild;

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        if (!event.getUser().isBot()) {
            guild = ProxyCache.getGuild(event.getGuild());
            LocalDate shield = LocalDate.now().minusDays(guild.getShield());
            LocalDate userTimeCreated = event.getUser().getTimeCreated().toLocalDate();

            if (!userTimeCreated.isAfter(shield)) {
                removeMemberData(event);
                sendLeaveMessage(event);
            }
        }
    }

    private void removeMemberData(GuildMemberRemoveEvent event) {
        Dao<PGuildMember> gMemberDao = DaoFactory.getGuildMemberDAO();
        PGuildMember gMember = gMemberDao.find(event.getGuild().getId() + "#" + event.getUser().getId());
        if (!gMember.isEmpty()) {
            gMemberDao.delete(gMember);
        }
    }

    private void sendLeaveMessage(GuildMemberRemoveEvent event) {
        if (guild.getLeaveChannel() != null) {
            Dao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            PLeaveChannel leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());
            try {
                if (leaveChannel.getMessage() != null && !leaveChannel.getEmbed()) {
                    event.getGuild().getTextChannelById(guild.getLeaveChannel()).sendMessage(ProxyString.getMemberMessageEvent(leaveChannel.getMessage(), event.getUser())).queue();

                } else if (leaveChannel.getMessage() != null && leaveChannel.getEmbed()) {
                    ProxyEmbed embed = new ProxyEmbed();
                    embed.controlGateEvent(event.getUser());
                    event.getGuild().getTextChannelById(guild.getLeaveChannel()).sendMessage(embed.getEmbed().build())
                            .append(ProxyString.getMemberMessageEvent(leaveChannel.getMessage(), event.getUser())).queue();

                } else if (leaveChannel.getMessage() == null && leaveChannel.getEmbed()) {
                    ProxyEmbed embed = new ProxyEmbed();
                    embed.controlGateEvent(event.getUser());
                    event.getGuild().getTextChannelById(guild.getLeaveChannel()).sendMessage(embed.getEmbed().build()).queue();
                }
            } catch (InsufficientPermissionException e) {
            }
        }
    }

}