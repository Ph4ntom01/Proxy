package listeners.member;

import java.time.LocalDate;

import configuration.cache.Blacklist;
import configuration.constants.Permissions;
import dao.database.Dao;
import dao.pojo.JoinChannelPojo;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import factory.DaoFactory;
import factory.PojoFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class MemberJoin extends ListenerAdapter {

    private GuildPojo guildPojo;

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!(boolean) Blacklist.getInstance().getUnchecked(event.getGuild().getId()) && !event.getUser().isBot()) {
            guildPojo = ProxyUtils.getGuildFromCache(event.getGuild());
            LocalDate shield = LocalDate.now().minusDays(guildPojo.getShield());
            LocalDate userTimeCreated = event.getUser().getTimeCreated().toLocalDate();

            if (userTimeCreated.isAfter(shield)) {
                try {
                    event.getMember().kick().queue();
                } catch (InsufficientPermissionException e) {
                } catch (HierarchyException e) {
                }
            } else {
                addMemberDatas(event);
                sendWelcomeMessage(event);
                if (guildPojo.getControlChannel() == null) {
                    addDefRole(event.getGuild(), event.getUser());
                }
            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (!(boolean) Blacklist.getInstance().getUnchecked(event.getGuild().getId())) {
            guildPojo = ProxyUtils.getGuildFromCache(event.getGuild());
            if (event.getChannel().getId().equals(guildPojo.getControlChannel()) && event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+2705")) {
                addDefRole(event.getGuild(), event.getUser());
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (!(boolean) Blacklist.getInstance().getUnchecked(event.getGuild().getId())) {
            guildPojo = ProxyUtils.getGuildFromCache(event.getGuild());
            if (event.getChannel().getId().equals(guildPojo.getControlChannel()) && event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+2705")) {
                try {
                    Role defaultRole = event.getGuild().getRoleById(guildPojo.getDefaultRole());
                    event.getGuild().removeRoleFromMember(event.getUserId(), defaultRole).queue();
                } catch (HierarchyException e) {
                } catch (ErrorResponseException e) {
                }
            }
        }
    }

    private void addMemberDatas(GuildMemberJoinEvent event) {
        Dao<MemberPojo> memberDao = DaoFactory.getMemberDAO();
        MemberPojo member = PojoFactory.getMember();
        member.setGuildId(event.getGuild().getId());
        member.setId(event.getUser().getId());
        member.setName(event.getUser().getName());
        member.setPermLevel(Permissions.USER.getLevel());
        memberDao.create(member);
    }

    private void addDefRole(Guild guildJda, User userJda) {
        if (guildPojo.getDefaultRole() != null && !guildPojo.getDefaultRole().isEmpty()) {
            try {
                Role defaultRole = guildJda.getRoleById(guildPojo.getDefaultRole());
                guildJda.addRoleToMember(userJda.getId(), defaultRole).queue();
            } catch (HierarchyException e) {
            } catch (ErrorResponseException e) {
            }
        }
    }

    private void sendWelcomeMessage(GuildMemberJoinEvent event) {
        if (guildPojo.getJoinChannel() != null) {
            Dao<JoinChannelPojo> joinChannelDao = DaoFactory.getJoinChannelDAO();
            JoinChannelPojo joinChannel = joinChannelDao.find(guildPojo.getJoinChannel());
            try {
                if (joinChannel.getMessage() != null && !joinChannel.getEmbed()) {
                    event.getGuild().getTextChannelById(guildPojo.getJoinChannel()).sendMessage(ProxyUtils.getMemberMessageEvent(joinChannel.getMessage(), event.getUser())).queue();

                } else if (joinChannel.getMessage() != null && joinChannel.getEmbed()) {
                    ProxyEmbed embed = new ProxyEmbed();
                    embed.controlGateEvent(event.getUser());
                    event.getGuild().getTextChannelById(guildPojo.getJoinChannel()).sendMessage(embed.getEmbed().build())
                            .append(ProxyUtils.getMemberMessageEvent(joinChannel.getMessage(), event.getUser())).queue();

                } else if (joinChannel.getMessage() == null && joinChannel.getEmbed()) {
                    ProxyEmbed embed = new ProxyEmbed();
                    embed.controlGateEvent(event.getUser());
                    event.getGuild().getTextChannelById(guildPojo.getJoinChannel()).sendMessage(embed.getEmbed().build()).queue();
                }
            } catch (InsufficientPermissionException e) {
            }
        }
    }

}