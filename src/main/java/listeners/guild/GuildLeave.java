package listeners.guild;

import java.util.logging.Level;

import configuration.files.Config;
import configuration.files.Log;
import dao.database.Dao;
import dao.database.GuildDAO;
import dao.pojo.ChannelJoinPojo;
import dao.pojo.ChannelLeavePojo;
import dao.pojo.GuildPojo;
import factory.ConfigFactory;
import factory.DaoFactory;
import factory.StatsFactory;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildLeave extends ListenerAdapter {

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
        GuildPojo guild = guildDao.find(event.getGuild().getId());

        if (guild.getChannelJoin() != null) {
            Dao<ChannelJoinPojo> channelJoinDao = DaoFactory.getChannelJoinDAO();
            ChannelJoinPojo channelJoin = channelJoinDao.find(guild.getChannelJoin());
            guild.setChannelJoin(null);
            guildDao.update(guild);
            channelJoinDao.delete(channelJoin);
        }
        if (guild.getChannelLeave() != null) {
            Dao<ChannelLeavePojo> channelLeaveDao = DaoFactory.getChannelLeaveDAO();
            ChannelLeavePojo channelLeave = channelLeaveDao.find(guild.getChannelLeave());
            guild.setChannelLeave(null);
            guildDao.update(guild);
            channelLeaveDao.delete(channelLeave);
        }
        guild.setMembers(((GuildDAO) guildDao).findMembers(event.getGuild().getId()));
        ((GuildDAO) guildDao).delete(guild.getMembers());
        guildDao.delete(guild);

        Config conf = ConfigFactory.tokens();
        StatsFactory.getDBL(conf).setStats(event.getJDA().getGuilds().size());
        Log log = new Log(GuildLeave.class.getName(), "guilds.log");
        log.log(Level.INFO, event.getGuild().getName() + "(" + event.getGuild().getId() + ")" + " left");
    }

}