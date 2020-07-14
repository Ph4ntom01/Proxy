package listeners.guild;

import configuration.cache.Blacklist;
import dao.database.Dao;
import dao.pojo.ChannelJoinPojo;
import dao.pojo.ChannelLeavePojo;
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

            if (event.getChannel().getId().equals(guild.getChannelJoin())) {
                Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                Dao<ChannelJoinPojo> channelJoinDao = DaoFactory.getChannelJoinDAO();
                ChannelJoinPojo channelJoin = channelJoinDao.find(guild.getChannelJoin());
                guild.setChannelJoin(null);
                guildDao.update(guild);
                channelJoinDao.delete(channelJoin);
            }
            if (event.getChannel().getId().equals(guild.getChannelLeave())) {
                Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                Dao<ChannelLeavePojo> channelLeaveDao = DaoFactory.getChannelLeaveDAO();
                ChannelLeavePojo channelLeave = channelLeaveDao.find(guild.getChannelLeave());
                guild.setChannelLeave(null);
                guildDao.update(guild);
                channelLeaveDao.delete(channelLeave);
            }
        }
    }

}