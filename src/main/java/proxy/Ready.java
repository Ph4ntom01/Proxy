package proxy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dao.database.Dao;
import dao.database.GuildDAO;
import dao.pojo.PGuild;
import factory.DaoFactory;
import factory.PojoFactory;
import net.dv8tion.jda.api.entities.Guild;
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
        Dao<PGuild> guildDao = DaoFactory.getGuildDAO();
        Set<PGuild> discordGuilds = getGuildsFromDiscord(event.getJDA().getGuilds());
        Set<PGuild> databaseGuilds = ((GuildDAO) guildDao).findGuilds();
        databaseGuilds.removeAll(discordGuilds);

        if (!databaseGuilds.isEmpty()) {
            for (PGuild guildTmp : databaseGuilds) {
                PGuild guild = PojoFactory.getGuild();
                guild.setId(guildTmp.getId());
                guildDao.delete(guild);
            }
        }
    }

    private void guildsJoin(Event event) {
        Dao<PGuild> guildDao = DaoFactory.getGuildDAO();
        Set<PGuild> discordGuilds = getGuildsFromDiscord(event.getJDA().getGuilds());
        Set<PGuild> databaseGuilds = ((GuildDAO) guildDao).findGuilds();
        discordGuilds.removeAll(databaseGuilds);

        if (!discordGuilds.isEmpty()) {
            for (PGuild guildTmp : discordGuilds) {
                PGuild guild = PojoFactory.getGuild();
                guild.setId(guildTmp.getId());
                guild.setName(guildTmp.getName());
                guildDao.create(guild);
            }
        }
    }

    private Set<PGuild> getGuildsFromDiscord(List<Guild> guilds) {
        Set<PGuild> discordGuilds = new HashSet<>();
        for (Guild guildTmp : guilds) {
            PGuild guild = PojoFactory.getGuild();
            guild.setId(guildTmp.getId());
            guild.setName(guildTmp.getName());
            discordGuilds.add(guild);
        }
        return discordGuilds;
    }

}