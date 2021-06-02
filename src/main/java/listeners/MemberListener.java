package listeners;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.AttributedString;
import java.time.LocalDate;
import java.util.Objects;

import javax.imageio.ImageIO;

import configuration.cache.EGuildCache;
import configuration.constant.EFolder;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PBan;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import dao.pojo.PMember;
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
                    event.getGuild().getTextChannelById(joinChannel.getChannelId()).sendMessage(joinChannel.getMessage().replace("[member]", event.getUser().getName())).queue();

                } else if (joinChannel.getMessage() != null && joinChannel.getEmbed()) {
                    GuildMemberJoinRemoveModel join = new GuildMemberJoinRemoveModel();
                    join.sendJoinMessage(event, "join_channel_full", joinChannel);

                } else if (joinChannel.getMessage() == null && joinChannel.getEmbed()) {
                    GuildMemberJoinRemoveModel join = new GuildMemberJoinRemoveModel();
                    join.sendJoinMessage(event, "join_channel_only_box", joinChannel);
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
                    event.getGuild().getTextChannelById(leaveChannel.getChannelId()).sendMessage(leaveChannel.getMessage().replace("[member]", event.getUser().getName())).queue();
                }

                else if (leaveChannel.getMessage() != null && leaveChannel.getEmbed()) {
                    GuildMemberJoinRemoveModel leave = new GuildMemberJoinRemoveModel();
                    leave.sendLeaveMessage(event, "leave_channel_full", leaveChannel);
                }

                else if (leaveChannel.getMessage() == null && leaveChannel.getEmbed()) {
                    GuildMemberJoinRemoveModel leave = new GuildMemberJoinRemoveModel();
                    leave.sendLeaveMessage(event, "leave_channel_only_box", leaveChannel);
                }
            } catch (InsufficientPermissionException e) {
            }
        }
    }

    private class GuildMemberJoinRemoveModel {

        private void sendJoinMessage(GuildMemberJoinEvent eventJoin, String channel, PJoinChannel joinChannel) {
            String fileName = "join.png";
            if (channel.equals("join_channel_only_box")) {
                // @formatter:off
                eventJoin.getGuild().loadMembers()

                   .onSuccess(members -> eventJoin.getGuild().getTextChannelById(joinChannel.getChannelId())
                           .sendFile(sendCustomImage(eventJoin.getUser(), members.size()), fileName)
                           .queue())

                   .onError(e -> eventJoin.getGuild().getTextChannelById(joinChannel.getChannelId())
                           .sendFile(sendCustomImage(eventJoin.getUser(), eventJoin.getGuild().getMemberCount()), fileName)
                           .queue());
                   // @formatter:on
            }

            else if (channel.equals("join_channel_full")) {
                String quote = "> ";
                // @formatter:off
                   eventJoin.getGuild().loadMembers()

                   .onSuccess(members -> eventJoin.getGuild().getTextChannelById(joinChannel.getChannelId())
                           .sendMessage(quote + joinChannel.getMessage().replace("[member]", eventJoin.getUser().getName()))
                           .addFile(sendCustomImage(eventJoin.getUser(), members.size()), fileName)
                           .queue())

                   .onError(e -> eventJoin.getGuild().getTextChannelById(joinChannel.getChannelId())
                           .sendMessage(quote + joinChannel.getMessage().replace("[member]", eventJoin.getUser().getName()))
                           .addFile(sendCustomImage(eventJoin.getUser(), eventJoin.getGuild().getMemberCount()), fileName)
                           .queue());
                   // @formatter:on
            }
        }

        private void sendLeaveMessage(GuildMemberRemoveEvent eventRemove, String channel, PLeaveChannel leaveChannel) {
            String fileName = "leave.png";
            if (channel.equals("leave_channel_only_box")) {
             // @formatter:off
                eventRemove.getGuild().loadMembers()

                .onSuccess(members -> eventRemove.getGuild().getTextChannelById(leaveChannel.getChannelId())
                        .sendFile(sendCustomImage(eventRemove.getUser(), members.size()), fileName)
                        .queue())

                .onError(e -> eventRemove.getGuild().getTextChannelById(leaveChannel.getChannelId())
                        .sendFile(sendCustomImage(eventRemove.getUser(), eventRemove.getGuild().getMemberCount()), fileName)
                        .queue());
                // @formatter:on
            }

            else if (channel.equals("leave_channel_full")) {
                String quote = "> ";
                // @formatter:off
                eventRemove.getGuild().loadMembers()

                .onSuccess(members -> eventRemove.getGuild().getTextChannelById(leaveChannel.getChannelId())
                        .sendMessage(quote + leaveChannel.getMessage().replace("[member]", eventRemove.getUser().getName()))
                        .addFile(sendCustomImage(eventRemove.getUser(), members.size()), fileName)
                        .queue())

                .onError(e -> eventRemove.getGuild().getTextChannelById(leaveChannel.getChannelId())
                        .sendMessage(quote + leaveChannel.getMessage().replace("[member]", eventRemove.getUser().getName()))
                        .addFile(sendCustomImage(eventRemove.getUser(), eventRemove.getGuild().getMemberCount()), fileName)
                        .queue());
                // @formatter:on
            }
        }

        private ByteArrayInputStream sendCustomImage(User user, int memberCount) {
            try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
                ImageIO.setUseCache(false);
                BufferedImage image = ImageIO.read(new File(EFolder.RESOURCES.getName() + EFolder.TEMPLATE.getName() + "welcome.png"));

                // @formatter:off
                BufferedImage avatar = (user.getAvatarUrl() == null) ? 
                        ImageIO.read(new File(EFolder.RESOURCES.getName() + EFolder.TEMPLATE.getName() + "default_discord_avatar.png")) : 
                        ImageIO.read(new URL(user.getAvatarUrl() + "?size=128"));
                // @formatter:on

                Font font = new Font("Calibri", Font.TRUETYPE_FONT, 24);
                Graphics2D g = image.createGraphics();
                FontMetrics metrics = g.getFontMetrics(font);

                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.drawImage(avatar, (image.getWidth() - avatar.getWidth()) / 2, 27, null);
                g.drawString(defineAttributedText(font, user.getAsTag(), Color.WHITE).getIterator(), (image.getWidth() - metrics.stringWidth(user.getAsTag())) / 2, 195);
                g.drawString(defineAttributedText(new Font("Lato", Font.BOLD, 14), "#" + memberCount, Color.WHITE).getIterator(), 10, 225);
                g.dispose();

                ImageIO.write(image, "png", os);
                return new ByteArrayInputStream(os.toByteArray());
            } catch (IOException e) {
            }
            return null;
        }

        private AttributedString defineAttributedText(Font font, String text, Color textColor) {
            AttributedString attributedText = new AttributedString(text);
            attributedText.addAttribute(TextAttribute.FONT, font);
            attributedText.addAttribute(TextAttribute.FOREGROUND, textColor);
            return attributedText;
        }

    }

}