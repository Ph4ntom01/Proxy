package listeners.guild;

import configuration.cache.Blacklist;
import configuration.constants.Permissions;
import dao.database.Dao;
import dao.pojo.MemberPojo;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.ProxyUtils;

public class GuildUpdateOwner extends ListenerAdapter {

    @Override
    public void onGuildUpdateOwner(GuildUpdateOwnerEvent event) {
        if (!(boolean) Blacklist.getInstance().getUnchecked(event.getGuild().getId())) {

            event.getGuild().retrieveMemberById(event.getOldOwnerId()).queue(oldOwn -> {
                Dao<MemberPojo> memberDao = DaoFactory.getMemberDAO();
                MemberPojo oldOwner = ProxyUtils.getMemberFromCache(oldOwn);
                oldOwner.setPermLevel(Permissions.USER.getLevel());
                memberDao.update(oldOwner);
            });
            event.getGuild().retrieveMemberById(event.getNewOwnerId()).queue(newOwn -> {
                Dao<MemberPojo> memberDao = DaoFactory.getMemberDAO();
                MemberPojo newOwner = ProxyUtils.getMemberFromCache(newOwn);
                newOwner.setPermLevel(Permissions.ADMINISTRATOR.getLevel());
                memberDao.update(newOwner);
            });
        }
    }

}