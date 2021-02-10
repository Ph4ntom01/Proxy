package listeners;

import java.awt.Color;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import configuration.cache.EGuildCache;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PBan;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import dao.pojo.PMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
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

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getUser().isBot()) {
            EGuildCache.INSTANCE.getGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(guild -> {
                LocalDate shield = LocalDate.now().minusDays(guild.getShield());
                LocalDate userTimeCreated = event.getUser().getTimeCreated().toLocalDate();
                if (userTimeCreated.isAfter(shield)) {
                    try {
                        event.getMember().kick().queue();
                    } catch (InsufficientPermissionException e) {
                    } catch (HierarchyException e) {
                    }
                } else {
                    sendWelcomeMessage(event, guild);
                    if (guild.getControlChannel() == null) {
                        addDefRole(event.getGuild(), guild, event.getUser());
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
                    sendLeaveMessage(event, guild);
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
                addDefRole(event.getGuild(), guild, event.getUser());
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
                } catch (NullPointerException e) {
                } catch (HierarchyException e) {
                } catch (ErrorResponseException e) {
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

    private void addDefRole(Guild guildJda, PGuild guild, User user) {
        if (guild.getDefaultRole() != null) {
            try {
                Role defaultRole = guildJda.getRoleById(guild.getDefaultRole());
                guildJda.addRoleToMember(user.getId(), defaultRole).queue();
            } catch (HierarchyException e) {
            } catch (ErrorResponseException e) {
            }
        }
    }

    private void sendWelcomeMessage(GuildMemberJoinEvent event, PGuild guild) {
        if (guild.getJoinChannel() != null) {
            ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
            PJoinChannel joinChannel = joinChannelDao.find(guild.getJoinChannel());
            try {
                if (joinChannel.getMessage() != null && !joinChannel.getEmbed()) {
                    event.getGuild().getTextChannelById(guild.getJoinChannel()).sendMessage(joinChannel.getMessage().replace("[member]", event.getUser().getName())).queue();

                } else if (joinChannel.getMessage() != null && joinChannel.getEmbed()) {
                    EmbedBuilder embed = controlGateEvent(event.getUser());
                    // @formatter:off
                    event.getGuild().getTextChannelById(guild.getJoinChannel()).sendMessage(embed.build())
                        .append(joinChannel.getMessage().replace("[member]", event.getUser().getName())).queue();
                    // @formatter:on

                } else if (joinChannel.getMessage() == null && joinChannel.getEmbed()) {
                    EmbedBuilder embed = controlGateEvent(event.getUser());
                    event.getGuild().getTextChannelById(guild.getJoinChannel()).sendMessage(embed.build()).queue();
                }
            } catch (InsufficientPermissionException e) {
            }
        }
    }

    private void sendLeaveMessage(GuildMemberRemoveEvent event, PGuild guild) {
        if (guild.getLeaveChannel() != null) {
            ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            PLeaveChannel leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());
            try {
                if (leaveChannel.getMessage() != null && !leaveChannel.getEmbed()) {
                    event.getGuild().getTextChannelById(guild.getLeaveChannel()).sendMessage(leaveChannel.getMessage().replace("[member]", event.getUser().getName())).queue();

                } else if (leaveChannel.getMessage() != null && leaveChannel.getEmbed()) {
                    EmbedBuilder embed = controlGateEvent(event.getUser());
                    // @formatter:off
                    event.getGuild().getTextChannelById(guild.getLeaveChannel()).sendMessage(embed.build())
                        .append(leaveChannel.getMessage().replace("[member]", event.getUser().getName())).queue();
                    // @formatter:on

                } else if (leaveChannel.getMessage() == null && leaveChannel.getEmbed()) {
                    EmbedBuilder embed = controlGateEvent(event.getUser());
                    event.getGuild().getTextChannelById(guild.getLeaveChannel()).sendMessage(embed.build()).queue();
                }
            } catch (InsufficientPermissionException e) {
            }
        }
    }

    private EmbedBuilder controlGateEvent(User user) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.addField("Tag", user.getAsTag(), false);
        embed.addField("Discord Join", user.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("ID", user.getId(), true);
        embed.setImage(user.getEffectiveAvatarUrl() + "?size=256");
        return embed;
    }

}