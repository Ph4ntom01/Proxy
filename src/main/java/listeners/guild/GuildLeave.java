package listeners.guild;

import java.util.logging.Level;

import configuration.files.Config;
import configuration.files.Log;
import dao.database.Dao;
import dao.database.GuildDAO;
import dao.pojo.GuildPojo;
import dao.pojo.JoinChannelPojo;
import dao.pojo.LeaveChannelPojo;
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

        if (guild.getJoinChannel() != null) {
            Dao<JoinChannelPojo> joinChannelDao = DaoFactory.getJoinChannelDAO();
            JoinChannelPojo joinChannel = joinChannelDao.find(guild.getJoinChannel());
            guild.setJoinChannel(null);
            guildDao.update(guild);
            joinChannelDao.delete(joinChannel);
        }
        if (guild.getLeaveChannel() != null) {
            Dao<LeaveChannelPojo> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            LeaveChannelPojo leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());
            guild.setLeaveChannel(null);
            guildDao.update(guild);
            leaveChannelDao.delete(leaveChannel);
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