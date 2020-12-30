package listeners.guild;

import configuration.constant.Permissions;
import dao.database.Dao;
import dao.pojo.PGuildMember;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.utility.ProxyCache;

public class GuildUpdateOwner extends ListenerAdapter {

    @Override
    public void onGuildUpdateOwner(GuildUpdateOwnerEvent event) {
        event.getGuild().retrieveMemberById(event.getOldOwnerId()).queue(oldOwn -> {
            Dao<PGuildMember> gMemberDao = DaoFactory.getGuildMemberDAO();
            PGuildMember oldOwner = ProxyCache.getGuildMember(oldOwn);
            oldOwner.setPermId(Permissions.USER.getLevel());
            gMemberDao.update(oldOwner);
        });
        event.getGuild().retrieveMemberById(event.getNewOwnerId()).queue(newOwn -> {
            Dao<PGuildMember> gMemberDao = DaoFactory.getGuildMemberDAO();
            PGuildMember newOwner = ProxyCache.getGuildMember(newOwn);
            newOwner.setPermId(Permissions.ADMINISTRATOR.getLevel());
            gMemberDao.update(newOwner);
        });
    }

}