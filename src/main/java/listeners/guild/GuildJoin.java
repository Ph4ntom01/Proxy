package listeners.guild;

import java.util.logging.Level;

import configuration.cache.Blacklist;
import configuration.files.Config;
import configuration.files.Log;
import dao.database.Dao;
import dao.database.GuildDAO;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import factory.ConfigFactory;
import factory.DaoFactory;
import factory.PojoFactory;
import factory.StatsFactory;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.ProxyUtils;

public class GuildJoin extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
        GuildPojo guild = PojoFactory.getGuild();
        guild.setId(event.getGuild().getId());
        guild.setName(event.getGuild().getName());
        guildDao.create(guild);

        if (!(boolean) Blacklist.getInstance().getUnchecked(guild.getId())) {
            event.getGuild().loadMembers().onSuccess(members -> ((GuildDAO) guildDao).create(ProxyUtils.getMembersFromDiscord(members)));
        } else {
            Dao<MemberPojo> memberDao = DaoFactory.getMemberDAO();
            event.getGuild().retrieveOwner().queue(owner -> memberDao.create(ProxyUtils.getGuildOwnerFromDiscord(owner)));
        }
        Config conf = ConfigFactory.tokens();
        StatsFactory.getDBL(conf).setStats(event.getJDA().getGuilds().size());
        Log log = new Log(GuildJoin.class.getName(), "guilds.log");
        log.log(Level.INFO, event.getGuild().getName() + "(" + event.getGuild().getId() + ")" + " joined");
    }

}