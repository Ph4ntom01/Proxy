package commands.user;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import commands.ACommand;
import configuration.constant.ECommand;
import configuration.constant.EID;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GuildInfo extends ACommand {

    public GuildInfo(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        getGuild().retrieveOwner().queue(owner -> {
            long categories = getGuild().getCategories().size();
            long textChannels = getGuild().getTextChannels().size();
            long voiceChannels = getGuild().getVoiceChannels().size();
            long channels = categories + textChannels + voiceChannels;
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.GREEN);
            embed.setThumbnail(getGuild().getIconUrl());
            embed.setAuthor(getGuild().getName(), null, getGuild().getIconUrl());
            // @formatter:off
            embed.addField(
                    "Server Creation", StringUtils.capitalize(getGuild().getTimeCreated().getDayOfWeek().toString().toLowerCase())
                    + " " + getGuild().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    + " at " + getGuild().getTimeCreated().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                    false);
            // @formatter:on
            embed.addField("Guild Owner", owner.getAsMention(), true);
            embed.addField("Owner ID", "`" + owner.getId() + "`", true);
            embed.addField("Guild ID", "`" + getGuild().getId() + "`", true);
            embed.addField("Boost Count", ":small_orange_diamond: **__" + getGuild().getBoostCount() + "__** boosts", true);
            embed.addField("Boost Tier", StringUtils.capitalize(getGuild().getBoostTier().name().toLowerCase().replace("_", " ")), true);
            embed.addField("Guild Region", WordUtils.capitalize(getGuild().getRegionRaw().replace("-", " ")), true);

            // @formatter:off
            embed.addField(
                    "Channel Count: __" + channels 
                    + "__", ":small_orange_diamond: **__" + categories + "__** categories\n"
                    + ":small_orange_diamond: **__" + textChannels + "__** text channels\n"
                    + ":small_orange_diamond: **__" + voiceChannels + "__** voice channels",
                    true);
            embed.addField("Role Count", ":small_orange_diamond: **__" + getGuild().getRoles().size() + "__** roles", true);
            embed.addField("Emote Count", ":small_orange_diamond: **__" + getGuild().getEmotes().size() + "__** emotes", true);
            embed.addField("Default Notification Level", StringUtils.capitalize(getGuild().getDefaultNotificationLevel().toString().toLowerCase().replace("_", " ")), false);
    
            if (getGuild().getExplicitContentLevel().getKey() == 0) {
                embed.addField(
                        "Explicit Content Level",
                        getGuild().getExplicitContentLevel().getDescription() + "\n"
                        + "*Ain't no party like my grandma's tea party.*",
                        false);
                
            } else if (getGuild().getExplicitContentLevel().getKey() == 1) {
                embed.addField(
                        "Explicit Content Level",
                        getGuild().getExplicitContentLevel().getDescription() + "\n"
                        + "*Recommended option for servers that use roles trusted membership.*",
                        false);
                    
            } else if (getGuild().getExplicitContentLevel().getKey() == 2) {
                embed.addField(
                        "Explicit Content Level",
                        getGuild().getExplicitContentLevel().getDescription() + "\n"
                        + "*Recommended option for when you want that squeaky clean shine.*",
                        false);
            }
                
            embed.addField(
                    "Verification Level",
                    WordUtils.capitalize(getGuild().getVerificationLevel().name().toLowerCase().replace("_", " "))
                    + " (Level " + getGuild().getVerificationLevel().getKey() + ")",
                    true);
                
            embed.addField(
                    "MFA Level",
                    WordUtils.capitalize(getGuild().getRequiredMFALevel().name().toLowerCase().replace("_", " "))
                    + " (Level " + getGuild().getRequiredMFALevel().getKey() + ")",
                    true);
            // @formatter:on
            embed.addField("System Channel", (getGuild().getSystemChannel() == null) ? "No System Channel" : getGuild().getSystemChannel().getAsMention(), true);

            if (getGuild().getAfkChannel() != null) {
            // @formatter:off
            embed.addField("AFK Channel","```ini"
                    + "\n[Name]:      " + getGuild().getAfkChannel().getName()
                    + "\n[ID]:        " + getGuild().getAfkChannel().getId()
                    + "\n[Timeout]:   " + getGuild().getAfkTimeout().getSeconds() + "s```", false);
            // @formatter:on
            }

            embed.addField("Default Role", (getPGuild().getDefaultRole() == null) ? "No Default Role" : getGuild().getRoleById(getPGuild().getDefaultRole()).getAsMention(), true);
            embed.addField("Prefix", getGuildPrefix(), true);
            embed.addField("Shield", getPGuild().getShield() == 0 ? "Inactive" : "Active: " + getPGuild().getShield() + " day(s)", true);

            // @formatter:off
            embed.setFooter(
                    StringUtils.capitalize(DateTimeFormatter.ofPattern("EEE, dd/MM/yyyy", Locale.ENGLISH).format(LocalDateTime.now()))
                    + " at " + DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH).format(LocalDateTime.now()),
                    getGuild().getMemberById(EID.BOT_ID.getId()).getUser().getAvatarUrl());
            // @formatter:on
            sendEmbed(embed);
        });
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Display the server information.\n\nExample: `" + getGuildPrefix() + getCommandName() + "`.");
        } else {
            sendMessage("Display the server information. **Example:** `" + getGuildPrefix() + getCommandName() + "`.");
        }
    }

}