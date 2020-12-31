package listeners.member;

import java.time.LocalDate;

import dao.database.Dao;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import factory.DaoFactory;
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
import proxy.utility.ProxyCache;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyString;

public class MemberJoin extends ListenerAdapter {

    private PGuild guildPojo;

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getUser().isBot()) {
            guildPojo = ProxyCache.getGuild(event.getGuild());
            LocalDate shield = LocalDate.now().minusDays(guildPojo.getShield());
            LocalDate userTimeCreated = event.getUser().getTimeCreated().toLocalDate();

            if (userTimeCreated.isAfter(shield)) {
                try {
                    event.getMember().kick().queue();
                } catch (InsufficientPermissionException e) {
                } catch (HierarchyException e) {
                }
            } else {
                sendWelcomeMessage(event);
                if (guildPojo.getControlChannel() == null) {
                    addDefRole(event.getGuild(), event.getUser());
                }
            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        guildPojo = ProxyCache.getGuild(event.getGuild());
        if (event.getChannel().getId().equals(guildPojo.getControlChannel()) && event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+2705")) {
            addDefRole(event.getGuild(), event.getUser());
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        guildPojo = ProxyCache.getGuild(event.getGuild());
        if (event.getChannel().getId().equals(guildPojo.getControlChannel()) && event.getReactionEmote().getAsCodepoints().equalsIgnoreCase("U+2705")) {
            try {
                Role defaultRole = event.getGuild().getRoleById(guildPojo.getDefaultRole());
                event.getGuild().removeRoleFromMember(event.getUserId(), defaultRole).queue();
            } catch (NullPointerException e) {
            } catch (HierarchyException e) {
            } catch (ErrorResponseException e) {
            }
        }
    }

    private void addDefRole(Guild guildJda, User user) {
        if (guildPojo.getDefaultRole() != null && !guildPojo.getDefaultRole().isEmpty()) {
            try {
                Role defaultRole = guildJda.getRoleById(guildPojo.getDefaultRole());
                guildJda.addRoleToMember(user.getId(), defaultRole).queue();
            } catch (HierarchyException e) {
            } catch (ErrorResponseException e) {
            }
        }
    }

    private void sendWelcomeMessage(GuildMemberJoinEvent event) {
        if (guildPojo.getJoinChannel() != null) {
            Dao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
            PJoinChannel joinChannel = joinChannelDao.find(guildPojo.getJoinChannel());
            try {
                if (joinChannel.getMessage() != null && !joinChannel.getEmbed()) {
                    event.getGuild().getTextChannelById(guildPojo.getJoinChannel()).sendMessage(ProxyString.getMemberMessageEvent(joinChannel.getMessage(), event.getUser())).queue();

                } else if (joinChannel.getMessage() != null && joinChannel.getEmbed()) {
                    ProxyEmbed embed = new ProxyEmbed();
                    embed.controlGateEvent(event.getUser());
                    event.getGuild().getTextChannelById(guildPojo.getJoinChannel()).sendMessage(embed.getEmbed().build())
                            .append(ProxyString.getMemberMessageEvent(joinChannel.getMessage(), event.getUser())).queue();

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