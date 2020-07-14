package listeners.guild;

import configuration.cache.Blacklist;
import dao.database.Dao;
import dao.pojo.GuildPojo;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.ProxyUtils;

public class GuildRoleDelete extends ListenerAdapter {

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        if (!(boolean) Blacklist.getInstance().getUnchecked(event.getGuild().getId())) {

            GuildPojo guild = ProxyUtils.getGuildFromCache(event.getGuild());

            if (event.getRole().getId().equals(guild.getDefaultRole())) {
                Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                guild.setDefaultRole(null);
                guildDao.update(guild);
            }
        }
    }

}