package listeners.guild;

import dao.database.Dao;
import dao.pojo.PGuild;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.utility.ProxyCache;

public class GuildUpdateName extends ListenerAdapter {

    @Override
    public void onGuildUpdateName(GuildUpdateNameEvent event) {
        Dao<PGuild> guildDao = DaoFactory.getGuildDAO();
        PGuild guild = ProxyCache.getGuild(event.getGuild());
        guild.setName(event.getNewName());
        guildDao.update(guild);
    }

}