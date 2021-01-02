package listeners.guild;

import java.util.logging.Level;

import configuration.cache.GuildCache;
import configuration.file.Config;
import configuration.file.Log;
import dao.database.Dao;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import factory.ConfigFactory;
import factory.DaoFactory;
import factory.StatsFactory;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.utility.ProxyCache;

public class GuildLeave extends ListenerAdapter {

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        Dao<PGuild> guildDao = DaoFactory.getGuildDAO();
        PGuild guild = ProxyCache.getGuild(event.getGuild());
        guildDao.delete(guild);
        if (guild.getJoinChannel() != null) {
            Dao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
            PJoinChannel joinChannel = joinChannelDao.find(guild.getJoinChannel());
            joinChannelDao.delete(joinChannel);
        }
        if (guild.getLeaveChannel() != null) {
            Dao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            PLeaveChannel leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());
            leaveChannelDao.delete(leaveChannel);
        }

        // Remove the guild from the cache.
        GuildCache.INSTANCE.getGuild().synchronous().invalidate(guild.getId());

        Config conf = ConfigFactory.getConf();
        StatsFactory.getDBL(conf).setStats(event.getJDA().getGuilds().size());
        Log log = new Log(GuildLeave.class.getName(), "guilds.log");
        log.log(Level.INFO, event.getGuild().getName() + "(" + event.getGuild().getId() + ")" + " left");
    }

}