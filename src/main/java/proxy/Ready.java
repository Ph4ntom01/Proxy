package proxy;

import java.util.Set;

import configuration.cache.Blacklist;
import dao.database.Dao;
import dao.database.GuildDAO;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import factory.DaoFactory;
import factory.PojoFactory;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Ready extends ListenerAdapter {

    protected Ready() {
    }

    @Override
    public void onReady(ReadyEvent event) {
        guildsLeave(event);
        guildsJoin(event);
    }

    private void guildsLeave(Event event) {
        Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
        Set<GuildPojo> discordGuilds = ProxyUtils.getGuildsFromDiscord(event.getJDA().getGuilds());
        Set<GuildPojo> databaseGuilds = ((GuildDAO) guildDao).findGuilds();
        databaseGuilds.removeAll(discordGuilds);

        if (!databaseGuilds.isEmpty()) {
            GuildPojo guild = PojoFactory.getGuild();
            for (GuildPojo guildTmp : databaseGuilds) {
                guild.setId(guildTmp.getId());
                guild.setMembers(((GuildDAO) guildDao).findMembers(guildTmp.getId()));
                ((GuildDAO) guildDao).delete(guild.getMembers());
                guildDao.delete(guild);
            }
        }
    }

    private void guildsJoin(Event event) {
        Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
        Set<GuildPojo> discordGuilds = ProxyUtils.getGuildsFromDiscord(event.getJDA().getGuilds());
        Set<GuildPojo> databaseGuilds = ((GuildDAO) guildDao).findGuilds();
        discordGuilds.removeAll(databaseGuilds);

        if (!discordGuilds.isEmpty()) {
            GuildPojo guild = PojoFactory.getGuild();
            for (GuildPojo guildTmp : discordGuilds) {
                guild.setId(guildTmp.getId());
                guild.setName(guildTmp.getName());
                guildDao.create(guild);

                if (!(boolean) Blacklist.getInstance().getUnchecked(guild.getId())) {
                    event.getJDA().getGuildById(guild.getId()).loadMembers().onSuccess(members -> ((GuildDAO) guildDao).create(ProxyUtils.getMembersFromDiscord(members)));
                } else {
                    Dao<MemberPojo> memberDao = DaoFactory.getMemberDAO();
                    event.getJDA().getGuildById(guild.getId()).retrieveOwner().queue(owner -> memberDao.create(ProxyUtils.getGuildOwnerFromDiscord(owner)));
                }
            }
        }
    }

}