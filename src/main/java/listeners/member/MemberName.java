package listeners.member;

import dao.database.Dao;
import dao.pojo.PMember;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberName extends ListenerAdapter {

    /**
     * Indicates that the username of a User changed. (Not Nickname).<br />
     * Note: The old member has to be cached to compare the old and new usernames.<br />
     * Scenarios: <br />
     * 
     * - If the ChunkingFilter is set to NONE and the MemberCachePolicy is set to ALL, this method will not trigger the first time a user update his name, because
     * the user is not cached, It will be after an action from him (username update, nickname update, message sent, etc).
     * 
     * - If the ChunkingFilter is set to NONE and the MemberCachePolicy is set to NONE, this method will never trigger at all.
     */
    @Override
    public void onUserUpdateName(UserUpdateNameEvent event) {
        if (!event.getUser().isBot()) {
            Dao<PMember> memberDao = DaoFactory.getMemberDAO();
            PMember member = memberDao.find(event.getUser().getId());
            if (!member.isEmpty()) {
                member.setName(event.getNewName());
                memberDao.update(member);
            }
        }
    }

}