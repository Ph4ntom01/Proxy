package listeners.guild;

import java.util.logging.Level;

import configuration.file.Config;
import configuration.file.Log;
import dao.database.Dao;
import dao.pojo.PGuild;
import factory.ConfigFactory;
import factory.DaoFactory;
import factory.PojoFactory;
import factory.StatsFactory;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildJoin extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Dao<PGuild> guildDao = DaoFactory.getGuildDAO();
        PGuild guild = PojoFactory.getGuild();
        guild.setId(event.getGuild().getId());
        guild.setName(event.getGuild().getName());
        guildDao.create(guild);

        Config conf = ConfigFactory.getConf();
        StatsFactory.getDBL(conf).setStats(event.getJDA().getGuilds().size());
        Log log = new Log(GuildJoin.class.getName(), "guilds.log");
        log.log(Level.INFO, event.getGuild().getName() + "(" + event.getGuild().getId() + ")" + " joined");
    }

}