package listeners.guild;

import dao.database.Dao;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.utility.ProxyCache;

public class GuildChannelDelete extends ListenerAdapter {

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        PGuild guild = ProxyCache.getGuild(event.getGuild());
        if (event.getChannel().getId().equals(guild.getJoinChannel())) {
            Dao<PGuild> guildDao = DaoFactory.getGuildDAO();
            Dao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
            PJoinChannel joinChannel = joinChannelDao.find(guild.getJoinChannel());
            guild.setJoinChannel(null);
            guildDao.update(guild);
            joinChannelDao.delete(joinChannel);
        }
        if (event.getChannel().getId().equals(guild.getLeaveChannel())) {
            Dao<PGuild> guildDao = DaoFactory.getGuildDAO();
            Dao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            PLeaveChannel leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());
            guild.setLeaveChannel(null);
            guildDao.update(guild);
            leaveChannelDao.delete(leaveChannel);
        }
        if (event.getChannel().getId().equals(guild.getControlChannel())) {
            Dao<PGuild> guildDao = DaoFactory.getGuildDAO();
            guild.setControlChannel(null);
            guildDao.update(guild);
        }
    }

}