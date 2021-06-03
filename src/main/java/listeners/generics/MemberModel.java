package listeners.generics;

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
import java.text.AttributedString;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.constant.EFolder;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class MemberModel {

    private static final Logger LOG = LoggerFactory.getLogger(MemberModel.class);

    private Event event;
    private PGuild guild;

    protected MemberModel() {}

    protected MemberModel(Event event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    protected void addDefRole(Guild guildJda, User user) {
        if (guild.getDefaultRole() != null) {
            try {
                Role defaultRole = guildJda.getRoleById(guild.getDefaultRole());
                guildJda.addRoleToMember(user.getId(), defaultRole).queue();
            } catch (HierarchyException | ErrorResponseException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    protected void sendWelcomeMessage() {
        GuildMemberJoinEvent joinEvent = (GuildMemberJoinEvent) event;
        if (guild.getJoinChannel() != null) {
            ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
            PJoinChannel joinChannel = joinChannelDao.find(guild.getJoinChannel());
            try {
                if (joinChannel.getMessage() != null && !joinChannel.getEmbed()) {
                    joinEvent.getGuild().getTextChannelById(joinChannel.getChannelId()).sendMessage(joinChannel.getMessage().replace("[member]", joinEvent.getUser().getName())).queue();

                } else if (joinChannel.getMessage() != null && joinChannel.getEmbed()) {
                    sendJoinMessage(joinEvent, "join_channel_full", joinChannel);

                } else if (joinChannel.getMessage() == null && joinChannel.getEmbed()) {
                    sendJoinMessage(joinEvent, "join_channel_only_box", joinChannel);
                }
            } catch (InsufficientPermissionException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    protected void sendLeaveMessage() {
        GuildMemberRemoveEvent removeEvent = (GuildMemberRemoveEvent) event;
        if (guild.getLeaveChannel() != null) {
            ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            PLeaveChannel leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());
            try {
                if (leaveChannel.getMessage() != null && !leaveChannel.getEmbed()) {
                    removeEvent.getGuild().getTextChannelById(leaveChannel.getChannelId()).sendMessage(leaveChannel.getMessage().replace("[member]", removeEvent.getUser().getName())).queue();
                }

                else if (leaveChannel.getMessage() != null && leaveChannel.getEmbed()) {
                    sendLeaveMessage(removeEvent, "leave_channel_full", leaveChannel);
                }

                else if (leaveChannel.getMessage() == null && leaveChannel.getEmbed()) {
                    sendLeaveMessage(removeEvent, "leave_channel_only_box", leaveChannel);
                }
            } catch (InsufficientPermissionException e) {
                LOG.error(e.getMessage());
            }
        }
    }

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
            LOG.error(e.getMessage());
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