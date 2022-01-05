package handlers.actions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextAttribute;
import java.awt.geom.Ellipse2D;
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
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class MemberView {

    private static final Logger LOG = LoggerFactory.getLogger(MemberView.class);
    private static final String MEMBER_PATTERN = "[member]";

    protected void sendJoinMessage(Guild guild, PJoinChannel joinChannel, User user) {
        guild.getTextChannelById(joinChannel.getChannelId()).sendMessage(joinChannel.getMessage().replace(MEMBER_PATTERN, user.getName())).queue();
    }

    protected void sendJoinMessage(Guild guild, PJoinChannel joinChannel, User user, String eventMessage) {
        String fileName = guild.getName() + "_join.png";
        if (eventMessage.equals("join_channel_only_box")) {
            // @formatter:off
            guild.loadMembers()

            .onSuccess(members -> guild.getTextChannelById(joinChannel.getChannelId())
                       .sendFile(sendCustomImage(user, members.size()), fileName)
                       .queue())

            .onError(e -> guild.getTextChannelById(joinChannel.getChannelId())
                       .sendFile(sendCustomImage(user,guild.getMemberCount()), fileName)
                       .queue());
            // @formatter:on
        }

        else if (eventMessage.equals("join_channel_full")) {
            String quote = "> ";
            // @formatter:off
            guild.loadMembers()

            .onSuccess(members -> guild.getTextChannelById(joinChannel.getChannelId())
                       .sendMessage(quote + joinChannel.getMessage().replace(MEMBER_PATTERN,user.getName()))
                       .addFile(sendCustomImage(user, members.size()), fileName)
                       .queue())

            .onError(e -> guild.getTextChannelById(joinChannel.getChannelId())
                       .sendMessage(quote + joinChannel.getMessage().replace(MEMBER_PATTERN, user.getName()))
                       .addFile(sendCustomImage(user, guild.getMemberCount()), fileName)
                       .queue());
            // @formatter:on
        }
    }

    protected void sendLeaveMessage(Guild guild, PLeaveChannel leaveChannel, User user) {
        guild.getTextChannelById(leaveChannel.getChannelId()).sendMessage(leaveChannel.getMessage().replace(MEMBER_PATTERN, user.getName())).queue();
    }

    protected void sendLeaveMessage(Guild guild, PLeaveChannel leaveChannel, User user, String eventMessage) {
        String fileName = guild.getName() + "_leave.png";
        if (eventMessage.equals("leave_channel_only_box")) {
            // @formatter:off
            guild.loadMembers()

            .onSuccess(members -> guild.getTextChannelById(leaveChannel.getChannelId())
                    .sendFile(sendCustomImage(user, members.size()), fileName)
                    .queue())

            .onError(e -> guild.getTextChannelById(leaveChannel.getChannelId())
                    .sendFile(sendCustomImage(user, guild.getMemberCount()), fileName)
                    .queue());
            // @formatter:on
        }

        else if (eventMessage.equals("leave_channel_full")) {
            String quote = "> ";
            // @formatter:off
            guild.loadMembers()

            .onSuccess(members -> guild.getTextChannelById(leaveChannel.getChannelId())
                    .sendMessage(quote + leaveChannel.getMessage().replace(MEMBER_PATTERN, user.getName()))
                    .addFile(sendCustomImage(user, members.size()), fileName)
                    .queue())

            .onError(e -> guild.getTextChannelById(leaveChannel.getChannelId())
                    .sendMessage(quote + leaveChannel.getMessage().replace(MEMBER_PATTERN, user.getName()))
                    .addFile(sendCustomImage(user, guild.getMemberCount()), fileName)
                    .queue());
            // @formatter:on
        }
    }

    private ByteArrayInputStream sendCustomImage(User user, int memberCount) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
            ImageIO.setUseCache(false);
            BufferedImage template = ImageIO.read(new File(EFolder.RESOURCES.getName() + EFolder.TEMPLATE.getName() + "welcome_redline.png"));

            // @formatter:off
            BufferedImage avatar = (user.getAvatarUrl() == null) ? 
                    ImageIO.read(new File(EFolder.RESOURCES.getName() + EFolder.TEMPLATE.getName() + "default_discord_avatar.png")) : 
                    ImageIO.read(new URL(user.getAvatarUrl() + "?size=128"));
            // @formatter:on

            int avatarPosX = (template.getWidth() - avatar.getWidth()) / 2;
            int avatarPosY = ((template.getHeight() - avatar.getHeight()) / 2) / 2;
            Color fontColor = new Color(220, 220, 220);
            Font fontTitle = new Font("Calibri", Font.TRUETYPE_FONT, 21);
            Font fontContent = new Font("Lato", Font.BOLD, 14);

            Ellipse2D ellipse = new Ellipse2D.Float(avatarPosX, avatarPosY, avatar.getWidth(), avatar.getHeight());
            Graphics2D g = template.createGraphics();
            FontMetrics metrics = g.getFontMetrics(fontTitle);

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setColor(fontColor);
            g.setStroke(new BasicStroke(3));

            g.drawString(defineAttributedText(fontTitle, user.getAsTag(), fontColor).getIterator(), (template.getWidth() - metrics.stringWidth(user.getAsTag())) / 2, 195);
            g.drawString(defineAttributedText(fontContent, "#" + memberCount, fontColor).getIterator(), 10, 225);

            // Draw the first ellipse. (round the outside of the ellipse).
            g.draw(ellipse);
            // Define the ellipse as a clip. Useful to omit part of the image that lies outside of the clip.
            g.setClip(ellipse);
            // Draw the user's avatar.
            g.drawImage(avatar, avatarPosX, avatarPosY, null);
            // Draw the second ellipse. (round the inside of the ellipse).
            g.draw(ellipse);

            g.dispose();

            ImageIO.write(template, "png", os);
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