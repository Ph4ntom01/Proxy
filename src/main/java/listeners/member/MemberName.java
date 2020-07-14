package listeners.member;

import java.util.List;

import configuration.cache.Blacklist;
import dao.database.Dao;
import dao.pojo.MemberPojo;
import factory.DaoFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.ProxyUtils;

/**
 * This class doesn't trigger when MemberCachePolicy is set to 'NONE'.
 */
public class MemberName extends ListenerAdapter {

    @Override
    public void onUserUpdateName(UserUpdateNameEvent event) {
        if (!event.getUser().isBot()) {
            List<Guild> guilds = event.getJDA().getGuilds();

            for (int i = 0; i < guilds.size(); i++) {
                if (!(boolean) Blacklist.getInstance().getUnchecked(guilds.get(i).getId())) {
                    guilds.get(i).retrieveMemberById(event.getUser().getId(), false).queue(user -> {
                        MemberPojo member = ProxyUtils.getMemberFromCache(user);
                        member.setName(event.getNewName());
                        Dao<MemberPojo> memberDao = DaoFactory.getMemberDAO();
                        memberDao.update(member);
                    }, ContextException.here(acceptor -> {
                    }));
                }
            }
        }
    }

}