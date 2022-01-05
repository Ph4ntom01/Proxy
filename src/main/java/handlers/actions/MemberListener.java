package handlers.actions;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.cache.EGuildCache;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PBan;
import dao.pojo.PGuildMember;
import dao.pojo.PMember;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateNameEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MemberListener extends ListenerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(MemberListener.class);

    private MemberModel memberModel;
    private MemberView memberView;

    public MemberListener(MemberModel memberModel, MemberView memberView) {
        this.memberModel = memberModel;
        this.memberView = memberView;
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getUser().isBot()) {
            EGuildCache.INSTANCE.getPGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(pguild -> {
                LocalDate shield = LocalDate.now().minusDays(pguild.getShield());
                LocalDate userTimeCreated = event.getUser().getTimeCreated().toLocalDate();
                if (userTimeCreated.isAfter(shield)) {
                    try {
                        event.getMember().kick().queue();
                    } catch (InsufficientPermissionException | HierarchyException e) {
                        LOG.error(e.getMessage());
                    }
                } else {
                    memberModel.join(event.getGuild(), event.getUser(), pguild, memberView);
                    if (pguild.getControlChannel() == null) {
                        memberModel.addDefRole(event.getGuild(), pguild, event.getUser());
                    }
                }
            });
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        if (!event.getUser().isBot()) {
            EGuildCache.INSTANCE.getPGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(pguild -> {
                LocalDate shield = LocalDate.now().minusDays(pguild.getShield());
                LocalDate userTimeCreated = event.getUser().getTimeCreated().toLocalDate();
                if (!userTimeCreated.isAfter(shield)) {
                    ADao<PGuildMember> pguildMemberDao = DaoFactory.getPGuildMemberDAO();
                    PGuildMember pguildMember = pguildMemberDao.find(event.getGuild().getIdLong(), event.getUser().getIdLong());
                    if (!pguildMember.isEmpty()) {
                        pguildMemberDao.delete(pguildMember);
                    }
                    memberModel.remove(event.getGuild(), event.getUser(), pguild, memberView);
                }
            });
        }
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        ADao<PBan> pmemberDao = DaoFactory.getBanDAO();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        PBan bannedMember = new PBan();
        bannedMember.setGuildId(event.getGuild().getIdLong());
        bannedMember.setId(event.getUser().getIdLong());
        bannedMember.setBanDate(now);
        pmemberDao.create(bannedMember);
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        ADao<PBan> pmemberDao = DaoFactory.getBanDAO();
        PBan bannedMember = new PBan();
        bannedMember.setGuildId(event.getGuild().getIdLong());
        bannedMember.setId(event.getUser().getIdLong());
        pmemberDao.delete(bannedMember);
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        EGuildCache.INSTANCE.getPGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(pguild -> {
            if (Objects.equals(event.getChannel().getIdLong(), pguild.getControlChannel()) && event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+2705")) {
                memberModel.addDefRole(event.getGuild(), pguild, event.getUser());
            }
        });
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        EGuildCache.INSTANCE.getPGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(pguild -> {
            if (Objects.equals(event.getChannel().getIdLong(), pguild.getControlChannel()) && event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+2705")) {
                try {
                    Role defaultRole = event.getGuild().getRoleById(pguild.getDefaultRole());
                    event.getGuild().removeRoleFromMember(event.getUserId(), defaultRole).queue();
                } catch (NullPointerException | HierarchyException | ErrorResponseException e) {
                    LOG.error(e.getMessage());
                }
            }
        });
    }

    /**
     * Indicates that the username of a User changed. (Not Nickname). <br>
     * Note: The old member has to be cached to compare the old and new usernames. <br>
     * Scenarios:
     * <ul>
     * <li>If the ChunkingFilter is set to NONE and the MemberCachePolicy is set to ALL, this method
     * will not trigger the first time a user update his name, because the user is not cached, It will
     * be after an action from him (username update, nickname update, message sent, etc).</li><br>
     * 
     * <li>If the ChunkingFilter is set to NONE and the MemberCachePolicy is set to NONE, this method
     * will never trigger at all.</li>
     * </ul>
     * 
     * @param event The UserUpdateNameEvent object.
     */
    @Override
    public void onUserUpdateName(UserUpdateNameEvent event) {
        if (!event.getUser().isBot()) {
            ADao<PMember> pmemberDao = DaoFactory.getPMemberDAO();
            PMember pmember = pmemberDao.find(event.getUser().getIdLong());
            if (!pmember.isEmpty()) {
                pmember.setName(event.getNewName());
                pmemberDao.update(pmember);
            }
        }
    }

}