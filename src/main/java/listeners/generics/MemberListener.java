package listeners.generics;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;
import java.util.logging.Logger;

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

    private static final Logger LOG = Logger.getLogger(MemberListener.class.getName());

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getUser().isBot()) {
            EGuildCache.INSTANCE.getGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(guild -> {
                LocalDate shield = LocalDate.now().minusDays(guild.getShield());
                LocalDate userTimeCreated = event.getUser().getTimeCreated().toLocalDate();
                if (userTimeCreated.isAfter(shield)) {
                    try {
                        event.getMember().kick().queue();
                    } catch (InsufficientPermissionException | HierarchyException e) {
                        LOG.log(java.util.logging.Level.WARNING, e.getMessage());
                    }
                } else {
                    MemberModel member = new MemberModel(event, guild);
                    member.sendWelcomeMessage();
                    if (guild.getControlChannel() == null) {
                        member.addDefRole(event.getGuild(), event.getUser());
                    }
                }
            });
        }
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        if (!event.getUser().isBot()) {
            EGuildCache.INSTANCE.getGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(guild -> {
                LocalDate shield = LocalDate.now().minusDays(guild.getShield());
                LocalDate userTimeCreated = event.getUser().getTimeCreated().toLocalDate();
                if (!userTimeCreated.isAfter(shield)) {
                    ADao<PGuildMember> gMemberDao = DaoFactory.getGuildMemberDAO();
                    PGuildMember gMember = gMemberDao.find(event.getGuild().getIdLong(), event.getUser().getIdLong());
                    if (!gMember.isEmpty()) {
                        gMemberDao.delete(gMember);
                    }
                    MemberModel member = new MemberModel(event, guild);
                    member.sendLeaveMessage();
                }
            });
        }
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        ADao<PBan> pMemberDao = DaoFactory.getBanDAO();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        PBan bMember = new PBan();
        bMember.setGuildId(event.getGuild().getIdLong());
        bMember.setId(event.getUser().getIdLong());
        bMember.setBanDate(now);
        pMemberDao.create(bMember);
    }

    @Override
    public void onGuildUnban(GuildUnbanEvent event) {
        ADao<PBan> pMemberDao = DaoFactory.getBanDAO();
        PBan bMember = new PBan();
        bMember.setGuildId(event.getGuild().getIdLong());
        bMember.setId(event.getUser().getIdLong());
        pMemberDao.delete(bMember);
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        EGuildCache.INSTANCE.getGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(guild -> {
            if (Objects.equals(event.getChannel().getIdLong(), guild.getControlChannel()) && event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+2705")) {
                MemberModel member = new MemberModel();
                member.addDefRole(event.getGuild(), event.getUser());
            }
        });
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        EGuildCache.INSTANCE.getGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(guild -> {
            if (Objects.equals(event.getChannel().getIdLong(), guild.getControlChannel()) && event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+2705")) {
                try {
                    Role defaultRole = event.getGuild().getRoleById(guild.getDefaultRole());
                    event.getGuild().removeRoleFromMember(event.getUserId(), defaultRole).queue();
                } catch (NullPointerException | HierarchyException | ErrorResponseException e) {
                    LOG.log(java.util.logging.Level.WARNING, e.getMessage());
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
            ADao<PMember> memberDao = DaoFactory.getMemberDAO();
            PMember member = memberDao.find(event.getUser().getIdLong());
            if (!member.isEmpty()) {
                member.setName(event.getNewName());
                memberDao.update(member);
            }
        }
    }

}