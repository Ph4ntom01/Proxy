package listeners.guild;

import dao.database.Dao;
import dao.pojo.GuildPojo;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.ProxyUtils;

public class GuildUpdateName extends ListenerAdapter {

    @Override
    public void onGuildUpdateName(GuildUpdateNameEvent event) {
        Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
        GuildPojo guild = ProxyUtils.getGuildFromCache(event.getGuild());
        guild.setName(event.getNewName());
        guildDao.update(guild);
    }

}