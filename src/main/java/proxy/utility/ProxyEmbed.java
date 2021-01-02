package proxy.utility;

import java.awt.Color;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.WordUtils;

import configuration.constant.Category;
import configuration.constant.Command;
import configuration.constant.ID;
import configuration.constant.Permissions;
import configuration.file.Config;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import factory.ConfigFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class ProxyEmbed {

    private EmbedBuilder embed;

    public void info(String prefix) {
        embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        embed.setTitle(":scroll: __Commands List__");
        // @formatter:off
        embed.setDescription(
                "The bot's prefix is __**" + prefix + "**__\n"
                + "_For more information about a specific command, use_ __**" + prefix + Command.HELP.getName() + "**__");
        embed.addField(
                ":closed_lock_with_key: " + StringUtils.capitalize(Category.ADMINISTRATION.getName()),
                ProxyUtils.getCommands(Category.ADMINISTRATION),
                false);
        embed.addField(
                ":dagger: " + StringUtils.capitalize(Category.MODERATION.getName()),
                ProxyUtils.getCommands(Category.MODERATION),
                false);
        embed.addField(
                ":tools: " + StringUtils.capitalize(Category.UTILITY.getName()),
                ProxyUtils.getCommands(Category.UTILITY),
                false);
        embed.addField(
                ":grin: " + StringUtils.capitalize(Category.MEME.getName()),
                ProxyUtils.getCommands(Category.MEME),
                false);
        // @formatter:on
    }

    public void help(String title, String message, Color color) {
        embed = new EmbedBuilder();
        embed.setColor(color);
        embed.setTitle(":gear: Command: " + title);
        embed.addField("", message, true);
    }

    public void ping(long ping) {
        embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setTitle(":ping_pong: Pong: `" + ping + "ms`");
        embed.setImage("https://media1.tenor.com/images/75af5262d34d2d0b5dc8c404212665a8/tenor.gif?itemid=8942945");
    }

    public void uptime() {
        final long duration = ManagementFactory.getRuntimeMXBean().getUptime();
        final long years = duration / 31104000000L;
        final long months = duration / 2592000000L % 12;
        final long days = duration / 86400000L % 30;
        final long hours = duration / 3600000L % 24;
        final long minutes = duration / 60000L % 60;
        final long seconds = duration / 1000L % 60;
        String uptime = (years == 0 ? "" : "**" + years + "** Years, ") + (months == 0 ? "" : "**" + months + "** Months, ") + (days == 0 ? "" : "**" + days + "** Days, ")
                + (hours == 0 ? "" : "**" + hours + "** Hours, ") + (minutes == 0 ? "" : "**" + minutes + "** Minutes, ") + (seconds == 0 ? "" : "**" + seconds + "** Seconds, ");
        uptime = uptime.replaceFirst("(?s)(.*)" + ", ", "$1" + "");
        uptime = uptime.replaceFirst("(?s)(.*)" + ",", "$1" + " and");
        embed = new EmbedBuilder();
        embed.setTitle(":stopwatch: Uptime");
        embed.setColor(Color.GREEN);
        embed.addField("", uptime + ".", true);
    }

    public void serverInfo(Guild gld, PGuild guild, Member owner) {
        long categories = gld.getCategories().size();
        long textChannels = gld.getTextChannels().size();
        long voiceChannels = gld.getVoiceChannels().size();
        long channels = categories + textChannels + voiceChannels;
        embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setThumbnail(gld.getIconUrl());
        embed.setAuthor(gld.getName(), null, gld.getIconUrl());
        embed.addField("Server Creation",
                StringUtils.capitalize(gld.getTimeCreated().getDayOfWeek().toString().toLowerCase()) + " " + gld.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH))
                        + " at " + gld.getTimeCreated().format(DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ENGLISH)),
                false);
        embed.addField("Guild Owner", owner.getAsMention(), true);
        embed.addField("Owner ID", "`" + owner.getId() + "`", true);
        embed.addField("Guild ID", "`" + gld.getId() + "`", true);

        embed.addField("Boost Count", ":small_orange_diamond: **__" + gld.getBoostCount() + "__** boosts", true);
        embed.addField("Boost Tier", StringUtils.capitalize(gld.getBoostTier().name().toLowerCase().replace("_", " ")), true);
        embed.addField("Guild Region", WordUtils.capitalize(gld.getRegionRaw().replace("-", " ")), true);

        // @formatter:off
        embed.addField(
                "Channel Count: __" + channels 
                + "__", ":small_orange_diamond: **__" + categories + "__** categories\n"
                + ":small_orange_diamond: **__" + textChannels + "__** text channels\n"
                + ":small_orange_diamond: **__" + voiceChannels + "__** voice channels",
                true);
        embed.addField("Role Count", ":small_orange_diamond: **__" + gld.getRoles().size() + "__** roles", true);
        embed.addField("Emote Count", ":small_orange_diamond: **__" + gld.getEmotes().size() + "__** emotes", true);

        embed.addField("Default Notification Level", StringUtils.capitalize(gld.getDefaultNotificationLevel().toString().toLowerCase().replace("_", " ")), false);

        if (gld.getExplicitContentLevel().getKey() == 0) {
            embed.addField(
                    "Explicit Content Level",
                    gld.getExplicitContentLevel().getDescription() + "\n"
                    + "*Ain't no party like my grandma's tea party.*",
                    false);
        
        } else if (gld.getExplicitContentLevel().getKey() == 1) {
            embed.addField(
                    "Explicit Content Level",
                    gld.getExplicitContentLevel().getDescription() + "\n"
                    + "*Recommended option for servers that use roles trusted membership.*",
                    false);
            
        } else if (gld.getExplicitContentLevel().getKey() == 2) {
            embed.addField(
                    "Explicit Content Level",
                    gld.getExplicitContentLevel().getDescription() + "\n"
                    + "*Recommended option for when you want that squeaky clean shine.*",
                    false);
        }
        
        embed.addField(
                "Verification Level",
                WordUtils.capitalize(gld.getVerificationLevel().name().toLowerCase().replace("_", " "))
                + " (Level " + gld.getVerificationLevel().getKey() + ")",
                true);
        
        embed.addField(
                "MFA Level",
                WordUtils.capitalize(gld.getRequiredMFALevel().name().toLowerCase().replace("_", " "))
                + " (Level " + gld.getRequiredMFALevel().getKey() + ")",
                true);
        // @formatter:on

        if (gld.getSystemChannel() == null) {
            embed.addField("System Channel", "No System Channel", true);
        }

        else {
            embed.addField("System Channel", gld.getSystemChannel().getAsMention(), true);
        }

        if (gld.getAfkChannel() != null) {
            embed.addField("AFK Channel",
                    "```ini\n[Name]:      " + gld.getAfkChannel().getName() + "\n[ID]:        " + gld.getAfkChannel().getId() + "\n[Timeout]:   " + gld.getAfkTimeout().getSeconds() + "s```", false);
        }

        if (guild.getDefaultRole() == null) {
            embed.addField("Default Role", "No Default Role", true);
        }

        else {
            embed.addField("Default Role", gld.getRoleById(guild.getDefaultRole()).getAsMention(), true);
        }

        embed.addField("Prefix", guild.getPrefix(), true);
        embed.addField("Shield", ProxyString.activeOrInactive(guild.getShield(), ProxyString.day(guild.getShield())), true);

        // @formatter:off
        embed.setFooter(
                StringUtils.capitalize(DateTimeFormatter.ofPattern("EEE, dd/MM/yyyy", Locale.ENGLISH).format(LocalDateTime.now())) + " at "
                + DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH).format(LocalDateTime.now()),
                gld.getMemberById(ID.PROXY.getId()).getUser().getAvatarUrl());
        // @formatter:on
    }

    public void memberInfo(Member mbr, Permissions permission) {
        embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setThumbnail(mbr.getUser().getEffectiveAvatarUrl());
        embed.setAuthor(mbr.getUser().getName(), null, mbr.getUser().getAvatarUrl());
        embed.addField("Nickname", ProxyString.getNickname(mbr), true);
        embed.addField("Discriminator", mbr.getUser().getDiscriminator(), true);
        embed.addField("Mention", mbr.getAsMention(), true);
        embed.addField("Permission", permission.getName(), true);
        embed.addField("Voice Channel", ProxyString.getVoiceChannel(mbr), true);
        embed.addField("ID", mbr.getId(), true);
        embed.addField("Server Join", mbr.getTimeJoined().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("Discord Join", mbr.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("Boosted Time", ProxyString.getTimeBoosted(mbr), true);

        if (!mbr.getRoles().isEmpty()) {
            embed.addField("Role(s)", getMemberRoles(mbr), false);
        }
    }

    public void botInfo(Member bot) {
        embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setThumbnail(bot.getUser().getEffectiveAvatarUrl());
        embed.setAuthor(bot.getUser().getName(), null, bot.getUser().getAvatarUrl());
        embed.addField("Nickname", ProxyString.getNickname(bot), true);
        embed.addField("Discriminator", bot.getUser().getDiscriminator(), true);
        embed.addField("Mention", bot.getAsMention(), true);
        embed.addField("Server Join", bot.getTimeJoined().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("Discord Join", bot.getUser().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("ID", bot.getId(), true);

        if (bot.hasPermission(Permission.ADMINISTRATOR)) {
            int position = bot.getGuild().getRoles().size() - (bot.getGuild().getRoleById(bot.getRoles().get(0).getId()).getPosition() + 1);
            // @formatter:off
            embed.addField(
                    "Permissions",
                    "```ini\n[Administrator]:       " + ProxyString.yesOrNo(bot.hasPermission(Permission.ADMINISTRATOR))
                    + "\n[Role Position]:        " + position + "```",
                    false);
            // @formatter:on
        }

        else {
            // @formatter:off
            embed.addField(
                    "Permissions", "```ini\n"
                    + "[Manage Server]:       " + ProxyString.yesOrNo(bot.hasPermission(Permission.MANAGE_SERVER)) + "\n"
                    + "[Manage Roles]:        " + ProxyString.yesOrNo(bot.hasPermission(Permission.MANAGE_ROLES)) + "\n"
                    + "[Manage Channels]:     " + ProxyString.yesOrNo(bot.hasPermission(Permission.MANAGE_CHANNEL)) + "\n"
                    + "[Kick Members]:        " + ProxyString.yesOrNo(bot.hasPermission(Permission.KICK_MEMBERS)) + "\n"
                    + "[Ban Members]:         " + ProxyString.yesOrNo(bot.hasPermission(Permission.BAN_MEMBERS)) + "\n"
                    + "[Mute Members]:        " + ProxyString.yesOrNo(bot.hasPermission(Permission.VOICE_MUTE_OTHERS)) + "\n"
                    + "[Deafen Members]:      " + ProxyString.yesOrNo(bot.hasPermission(Permission.VOICE_DEAF_OTHERS)) + "```",
                    false);
            // @formatter:on
        }

        if (!bot.getRoles().isEmpty()) {
            embed.addField("Role(s)", getMemberRoles(bot), false);
        }
    }

    public void selfbotInfo(PGuild guild, Member bot, User creator) {
        embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setThumbnail(bot.getUser().getEffectiveAvatarUrl());
        embed.setAuthor(bot.getUser().getName(), null, bot.getUser().getAvatarUrl());
        embed.addField("Nickname", ProxyString.getNickname(bot), true);
        embed.addField("Discriminator", bot.getUser().getDiscriminator(), true);
        embed.addField("Mention", bot.getAsMention(), true);
        embed.addField("Server Join", bot.getTimeJoined().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("Discord Join", bot.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("ID", bot.getId(), true);

        if (bot.hasPermission(Permission.ADMINISTRATOR)) {
            int position = bot.getGuild().getRoles().size() - (bot.getGuild().getRoleById(bot.getRoles().get(0).getId()).getPosition() + 1);
            // @formatter:off
            embed.addField(
                    "Permissions", "```ini\n"
                    + "[Administrator]:       " + ProxyString.yesOrNo(bot.hasPermission(Permission.ADMINISTRATOR)) + "\n"
                    + "[Role Position]:        " + position + "```",
                    false);
            // @formatter:on
        }

        else {
            // @formatter:off
            embed.addField("Permissions",
                    "```ini\n"
                    + "[Manage Server]:       " + ProxyString.yesOrNo(bot.hasPermission(Permission.MANAGE_SERVER)) + "\n"
                    + "[Manage Roles]:        " + ProxyString.yesOrNo(bot.hasPermission(Permission.MANAGE_ROLES)) + "\n"
                    + "[Manage Channels]:     " + ProxyString.yesOrNo(bot.hasPermission(Permission.MANAGE_CHANNEL)) + "\n"
                    + "[Kick Members]:        " + ProxyString.yesOrNo(bot.hasPermission(Permission.KICK_MEMBERS)) + "\n"
                    + "[Ban Members]:         " + ProxyString.yesOrNo(bot.hasPermission(Permission.BAN_MEMBERS)) + "\n"
                    + "[Mute Members]:        " + ProxyString.yesOrNo(bot.hasPermission(Permission.VOICE_MUTE_OTHERS)) + "\n"
                    + "[Deafen Members]:      " + ProxyString.yesOrNo(bot.hasPermission(Permission.VOICE_DEAF_OTHERS)) + "```",
                    false);
            // @formatter:on
        }

        embed.addField("Guild Count", bot.getJDA().getGuilds().size() + "", true);
        embed.addField("Prefix", guild.getPrefix(), true);
        embed.addField("Commands List", guild.getPrefix() + "info", true);
        embed.setFooter("Creator: " + creator.getName() + "#" + creator.getDiscriminator(), creator.getEffectiveAvatarUrl());

        if (!bot.getRoles().isEmpty()) {
            embed.addField("Role(s)", getMemberRoles(bot), false);
        }

        Config conf = ConfigFactory.getConf();
        // @formatter:off
        embed.addField(
                "Links", 
                "[Invite](" + conf.getString("link.invite") + ") | "
                + "[GitHub](" + conf.getString("link.github") + ") | "
                + "[Support Server](" + conf.getString("link.support") + ")",
                false);
        // @formatter:on
    }

    public void avatar(User user) {
        embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setImage(user.getEffectiveAvatarUrl() + "?size=512");
    }

    public void controlGateEvent(User user) {
        embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.addField("Tag", user.getAsTag(), false);
        embed.addField("Discord Join", user.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("ID", user.getId(), true);
        embed.setImage(user.getEffectiveAvatarUrl() + "?size=256");
    }

    public void channelInfo(TextChannel textChannel) {
        embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setThumbnail(textChannel.getGuild().getIconUrl());
        embed.addField("Name", textChannel.getName(), true);
        embed.addField("Position", (textChannel.getPosition() + 1) + "", true);
        embed.addField("Creation", textChannel.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);

        for (PermissionOverride role : textChannel.getRolePermissionOverrides()) {

            if (role.getRole().isPublicRole()
                    && (role.getManager().getInheritedPermissions().contains(Permission.MESSAGE_WRITE) || role.getManager().getAllowedPermissions().contains(Permission.MESSAGE_WRITE))) {
                embed.addField("Lock", "Inactive", true);
            }

            else if (role.getRole().isPublicRole() && (role.getManager().getDeniedPermissions().contains(Permission.MESSAGE_WRITE))) {
                embed.addField("Lock", "Active", true);
            }
        }
        embed.addField("Slowmode", ProxyString.activeOrInactive(textChannel.getSlowmode(), "s"), true);
        embed.addField("ID", textChannel.getId(), true);
        // @formatter:off
        embed.addField(
                "Category", "```ini\n"
                + "[Name]:               " + textChannel.getParent().getName() + "\n"
                + "[ID]:                 " + textChannel.getParent().getId() + "\n"
                + "[Position]:           " + (textChannel.getParent().getPosition() + 1) + "\n"
                + "[Creation]:           " + textChannel.getParent().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)) + "```",
                true);
        // @formatter:on
    }

    public void modoList(Set<PGuildMember> moderators) {
        embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle(":dagger: __Moderator(s)__");

        for (PGuildMember modo : moderators) {
            embed.addField("", "<@" + modo.getId() + ">", false);
        }
    }

    public void adminList(Set<PGuildMember> administrators) {
        embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle(":closed_lock_with_key: __Administrator(s)__");

        for (PGuildMember admin : administrators) {
            embed.addField("", "<@" + admin.getId() + ">", false);
        }
    }

    public void banList(List<Ban> bans, String prefix) {
        embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setTitle(":skull_crossbones: __Ban List__");

        for (int i = 0; i < bans.size(); i++) {
            embed.addField("", "**" + (i + 1) + ". " + bans.get(i).getUser().getName() + "**", false);
        }
        embed.addField("", "**" + prefix + "banlist [a number]**", true);
    }

    public void bannedMemberInfo(List<Ban> memberList, int position) {
        embed = new EmbedBuilder();
        embed.setColor(Color.RED);
        embed.setThumbnail(memberList.get(position).getUser().getEffectiveAvatarUrl());
        embed.addField("Tag", memberList.get(position).getUser().getAsTag(), true);
        embed.addField("Discord Join", memberList.get(position).getUser().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)), true);
        embed.addField("ID", memberList.get(position).getUser().getId(), true);
        embed.addField("Reason", "_" + String.valueOf(memberList.get(position).getReason()).replace("null", "No reason provided.") + "_", false);
    }

    public void meme(String title, String url, String image) {
        embed = new EmbedBuilder();
        embed.setColor(Color.BLUE);
        embed.setTitle(title, url);
        embed.setImage(image);
    }

    public void disable(String prefix) {
        embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        embed.setTitle("__**" + StringUtils.capitalize(Command.DISABLE.getName()) + "**__");
        // @formatter:off
        embed.addField("",
                "`" + Command.JOINCHAN.getName() + "` " + "`" + Command.JOINMESSAGE.getName() + "` " + "`" + Command.JOINEMBED.getName() + "` " + "`" + Command.LEAVECHAN.getName()
                + "` " + "`" + Command.LEAVEMESSAGE.getName() + "` " + "`" + Command.LEAVEEMBED.getName() + "` " + "`" + Command.CONTROLCHAN.getName() + "` " + "`" + Command.DEFROLE.getName() + "` " + "`"
                + Command.SHIELD.getName() + "`",
                false);
        // @formatter:on
        embed.addField("", "Example: `" + prefix + Command.DISABLE.getName() + " " + Command.JOINCHAN.getName() + "`", false);
    }

    public void controlGate(Guild gld, PGuild guild, PJoinChannel joinChannel, PLeaveChannel leaveChannel) {
        embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setTitle(":shield: Controlgate");
        embed.setThumbnail("https://media1.tenor.com/images/f02a54eef52d2867abd892ca0841a439/tenor.gif?itemid=9580001");

        embed.addField("", ":airplane_arriving: __**Arrivals**__\n_Notification when a member joins the server._", false);

        if (joinChannel.getChannelId() != null) {
            embed.addField(":pushpin: Channel", gld.getTextChannelById(joinChannel.getChannelId()).getAsMention(), true);
        }

        else {
            embed.addField(":pushpin: Channel", "No welcoming channel.", true);
        }

        embed.addField(":mag_right: Box", ProxyString.activeOrInactive(joinChannel.getEmbed()), true);

        if (joinChannel.getMessage() != null) {
            embed.addField(":page_with_curl: Message", "```\n" + joinChannel.getMessage() + "```", false);
        }

        else {
            embed.addField(":page_with_curl: Message", "```\nNo message set.```", false);
        }

        embed.addField("", ":airplane_departure: __**Departures**__\n_Notification when a member leaves the server._", false);

        if (leaveChannel.getChannelId() != null) {
            embed.addField(":pushpin: Channel", gld.getTextChannelById(leaveChannel.getChannelId()).getAsMention(), true);
        }

        else {
            embed.addField(":pushpin: Channel", "No leaving channel.", true);
        }

        embed.addField(":mag_right: Box", ProxyString.activeOrInactive(leaveChannel.getEmbed()), true);

        if (leaveChannel.getMessage() != null) {
            embed.addField(":page_with_curl: Message", "```\n" + leaveChannel.getMessage() + "```", false);
        }

        else {
            embed.addField(":page_with_curl: Message", "```\n" + "No message set." + "```", false);
        }

        if (guild.getControlChannel() != null) {
            embed.addField("", ":white_check_mark: __**Control Channel**__: " + gld.getTextChannelById(guild.getControlChannel()).getAsMention(), false);
        }

        else {
            embed.addField("", ":white_check_mark: __**Control Channel**__: Disabled", false);
        }
    }

    public void helpList() {
        embed = new EmbedBuilder();
        embed.setTitle(":scroll: __Help List__");
        embed.setColor(Color.GREEN);
        // @formatter:off
        embed.addField(
                ":closed_lock_with_key: " + StringUtils.capitalize(Category.ADMINISTRATION.getName()),
                "`" + Command.HELP.getName() + " " + Category.ADMINISTRATION.getName() + "`",
                true);
        embed.addField(
                ":dagger: " + StringUtils.capitalize(Category.MODERATION.getName()),
                "`" + Command.HELP.getName() + " " + Category.MODERATION.getName() + "`",
                true);
        embed.addField(
                ":tools: " + StringUtils.capitalize(Category.UTILITY.getName()),
                "`" + Command.HELP.getName() + " " + Category.UTILITY.getName() + "`",
                true);
        embed.addField(
                ":grin: " + StringUtils.capitalize(Category.MEME.getName()),
                "`" + Command.HELP.getName() + " " + Category.MEME.getName() + "`",
                true);
        // @formatter:on
    }

    private String getMemberRoles(Member member) {
        StringBuilder roles = new StringBuilder();
        for (Role memberRoles : member.getRoles()) {
            roles.append(memberRoles.getAsMention() + " ");
        }
        return roles.deleteCharAt(roles.length() - 1).toString();
    }

    public EmbedBuilder getEmbed() {
        return embed;
    }

}