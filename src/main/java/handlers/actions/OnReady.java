package handlers.actions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dao.database.ADao;
import dao.database.DaoFactory;
import dao.database.GuildDAO;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnReady extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        guildsLeave(event);
        guildsJoin(event);
    }

    private void guildsLeave(Event event) {

        ADao<PGuild> pguildDao = DaoFactory.getPGuildDAO();

        // Gets a list of all guilds that the logged account is connected to.
        Set<PGuild> discordGuilds = getGuildsFromDiscord(event.getJDA().getGuilds());

        // Gets a list of all guilds contained in the database.
        Set<PGuild> databaseGuilds = ((GuildDAO) pguildDao).findPGuilds();

        // Removes all guilds that are contained in the database list.
        databaseGuilds.removeAll(discordGuilds);

        // If the database list is not empty, one or more guilds have kicked the bot.
        if (!databaseGuilds.isEmpty()) {
            for (PGuild guildTmp : databaseGuilds) {
                PGuild pguild = new PGuild();
                pguild.setId(guildTmp.getId());
                pguildDao.delete(pguild);
            }
        }
    }

    private void guildsJoin(Event event) {

        ADao<PGuild> pguildDao = DaoFactory.getPGuildDAO();

        // Gets a list of all guilds that the logged account is connected to.
        Set<PGuild> discordGuilds = getGuildsFromDiscord(event.getJDA().getGuilds());

        // Gets a list of all guilds contained in the database.
        Set<PGuild> databaseGuilds = ((GuildDAO) pguildDao).findPGuilds();

        // Removes all guilds that are contained in the discord list.
        discordGuilds.removeAll(databaseGuilds);

        // If the discord list is not empty, one or more guilds have invited the bot.
        if (!discordGuilds.isEmpty()) {
            for (PGuild guildTmp : discordGuilds) {
                PGuild pguild = new PGuild();
                pguild.setId(guildTmp.getId());
                pguild.setName(guildTmp.getName());
                pguildDao.create(pguild);
            }
        }
    }

    private Set<PGuild> getGuildsFromDiscord(List<Guild> guilds) {
        Set<PGuild> discordGuilds = new HashSet<>();
        for (Guild guildTmp : guilds) {
            PGuild pguild = new PGuild();
            pguild.setId(guildTmp.getIdLong());
            pguild.setName(guildTmp.getName());
            discordGuilds.add(pguild);
        }
        return discordGuilds;
    }

}