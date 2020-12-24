package listeners.guild;

import configuration.cache.Blacklist;
import dao.database.Dao;
import dao.pojo.JoinChannelPojo;
import dao.pojo.LeaveChannelPojo;
import dao.pojo.GuildPojo;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.ProxyUtils;

public class GuildChannelDelete extends ListenerAdapter {

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        if (!(boolean) Blacklist.getInstance().getUnchecked(event.getGuild().getId())) {

            GuildPojo guild = ProxyUtils.getGuildFromCache(event.getGuild());

            if (event.getChannel().getId().equals(guild.getJoinChannel())) {
                Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                Dao<JoinChannelPojo> joinChannelDao = DaoFactory.getJoinChannelDAO();
                JoinChannelPojo joinChannel = joinChannelDao.find(guild.getJoinChannel());
                guild.setJoinChannel(null);
                guildDao.update(guild);
                joinChannelDao.delete(joinChannel);
            }
            if (event.getChannel().getId().equals(guild.getLeaveChannel())) {
                Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                Dao<LeaveChannelPojo> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
                LeaveChannelPojo leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());
                guild.setLeaveChannel(null);
                guildDao.update(guild);
                leaveChannelDao.delete(leaveChannel);
            }
            if (event.getChannel().getId().equals(guild.getControlChannel())) {
                Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                guild.setControlChannel(null);
                guildDao.update(guild);
            }
        }
    }

}