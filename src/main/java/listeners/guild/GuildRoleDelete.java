package listeners.guild;

import dao.database.Dao;
import dao.pojo.PGuild;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.utility.ProxyCache;

public class GuildRoleDelete extends ListenerAdapter {

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        PGuild guild = ProxyCache.getGuild(event.getGuild());
        if (event.getRole().getId().equals(guild.getDefaultRole())) {
            Dao<PGuild> guildDao = DaoFactory.getGuildDAO();
            guild.setDefaultRole(null);
            guildDao.update(guild);
        }
    }

}