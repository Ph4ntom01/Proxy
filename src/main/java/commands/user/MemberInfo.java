package commands.user;

import java.awt.Color;
import java.time.format.DateTimeFormatter;

import commands.ACommand;
import configuration.cache.EGuildMemberCache;
import configuration.constant.ECommand;
import configuration.constant.EID;
import configuration.constant.EPermission;
import configuration.file.Config;
import configuration.file.ConfigFactory;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.requests.RestAction;

public class MemberInfo extends ACommand {

    public MemberInfo(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        super(event, args, command, guild);
    }

    public MemberInfo(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        RestAction<Member> command = retrieveMentionnedMember(1, false);
        if (command == null) { return; }
        command.queue(mentionnedMember -> {
            if (mentionnedMember.getUser().isBot()) {
                if (mentionnedMember.getId().equals(EID.BOT_ID.getId())) {
                    getJDA().retrieveUserById(EID.BOT_OWNER.getId()).queue(creator -> sendEmbed(selfbotInfo(getPGuild(), mentionnedMember, creator)));
                } else {
                    sendEmbed(botInfo(mentionnedMember));
                }
            } else {
                EGuildMemberCache.INSTANCE.getGuildMemberAsync(mentionnedMember)
                        .thenAcceptAsync(member -> sendEmbed(memberInfo(mentionnedMember, EPermission.getPermission(member.getPermission().getLevel()))));
            }
        }, ContextException.here(acceptor -> sendMessage("**" + getArgs()[1] + "** is not a member.")));
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed("Display the member information.\n\n"
                    + "Example: `" + getGuildPrefix() + getCommandName() + " @aMember`\n\n"
                    + "*You can also mention a member by his ID*.");
            // @formatter:on
        } else {
            sendMessage("Display the member information. **Example:** `" + getGuildPrefix() + getCommandName() + " @aMember`.");
        }
    }

    public EmbedBuilder selfbotInfo(PGuild guild, Member bot, User creator) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setThumbnail(bot.getUser().getEffectiveAvatarUrl());
        embed.setAuthor(bot.getUser().getName(), null, bot.getUser().getAvatarUrl());
        embed.addField("Nickname", bot.getNickname() == null ? bot.getUser().getName() : bot.getNickname(), true);
        embed.addField("Discriminator", "#" + bot.getUser().getDiscriminator(), true);
        embed.addField("Mention", bot.getAsMention(), true);
        embed.addField("Server Join", bot.getTimeJoined().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("Discord Join", bot.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("ID", bot.getId(), true);

        if (bot.hasPermission(Permission.ADMINISTRATOR)) {
            int position = bot.getGuild().getRoles().size() - (bot.getGuild().getRoleById(bot.getRoles().get(0).getId()).getPosition() + 1);
            // @formatter:off
            embed.addField(
                    "Permissions", "```ini\n"
                    + "[Administrator]:       " + yesOrNo(bot.hasPermission(Permission.ADMINISTRATOR)) + "\n"
                    + "[Role Position]:        " + position + "```",
                    false);
            // @formatter:on
        }

        else {
            // @formatter:off
            embed.addField("Permissions",
                    "```ini\n"
                    + "[Manage Server]:       " + yesOrNo(bot.hasPermission(Permission.MANAGE_SERVER)) + "\n"
                    + "[Manage Roles]:        " + yesOrNo(bot.hasPermission(Permission.MANAGE_ROLES)) + "\n"
                    + "[Manage Channels]:     " + yesOrNo(bot.hasPermission(Permission.MANAGE_CHANNEL)) + "\n"
                    + "[Kick Members]:        " + yesOrNo(bot.hasPermission(Permission.KICK_MEMBERS)) + "\n"
                    + "[Ban Members]:         " + yesOrNo(bot.hasPermission(Permission.BAN_MEMBERS)) + "\n"
                    + "[Mute Members]:        " + yesOrNo(bot.hasPermission(Permission.VOICE_MUTE_OTHERS)) + "\n"
                    + "[Deafen Members]:      " + yesOrNo(bot.hasPermission(Permission.VOICE_DEAF_OTHERS)) + "```",
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
        return embed;
    }

    private EmbedBuilder botInfo(Member bot) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setThumbnail(bot.getUser().getEffectiveAvatarUrl());
        embed.setAuthor(bot.getUser().getName(), null, bot.getUser().getAvatarUrl());
        embed.addField("Nickname", bot.getNickname() == null ? bot.getUser().getName() : bot.getNickname(), true);
        embed.addField("Discriminator", "#" + bot.getUser().getDiscriminator(), true);
        embed.addField("Mention", bot.getAsMention(), true);
        embed.addField("Server Join", bot.getTimeJoined().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("Discord Join", bot.getUser().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("ID", bot.getId(), true);
        if (bot.hasPermission(Permission.ADMINISTRATOR)) {
            int position = bot.getGuild().getRoles().size() - (bot.getGuild().getRoleById(bot.getRoles().get(0).getId()).getPosition() + 1);
            // @formatter:off
            embed.addField(
                    "Permissions",
                    "```ini\n[Administrator]:       " + yesOrNo(bot.hasPermission(Permission.ADMINISTRATOR))
                    + "\n[Role Position]:        " + position + "```",
                    false);
            // @formatter:on
        } else {
            // @formatter:off
            embed.addField(
                    "Permissions", "```ini\n"
                    + "[Manage Server]:       " + yesOrNo(bot.hasPermission(Permission.MANAGE_SERVER)) + "\n"
                    + "[Manage Roles]:        " + yesOrNo(bot.hasPermission(Permission.MANAGE_ROLES)) + "\n"
                    + "[Manage Channels]:     " + yesOrNo(bot.hasPermission(Permission.MANAGE_CHANNEL)) + "\n"
                    + "[Kick Members]:        " + yesOrNo(bot.hasPermission(Permission.KICK_MEMBERS)) + "\n"
                    + "[Ban Members]:         " + yesOrNo(bot.hasPermission(Permission.BAN_MEMBERS)) + "\n"
                    + "[Mute Members]:        " + yesOrNo(bot.hasPermission(Permission.VOICE_MUTE_OTHERS)) + "\n"
                    + "[Deafen Members]:      " + yesOrNo(bot.hasPermission(Permission.VOICE_DEAF_OTHERS)) + "```",
                    false);
            // @formatter:on
        }
        if (!bot.getRoles().isEmpty()) {
            embed.addField("Role(s)", getMemberRoles(bot), false);
        }
        return embed;
    }

    private EmbedBuilder memberInfo(Member mbr, EPermission permission) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setThumbnail(mbr.getUser().getEffectiveAvatarUrl());
        embed.setAuthor(mbr.getUser().getName(), null, mbr.getUser().getAvatarUrl());
        embed.addField("Nickname", mbr.getNickname() == null ? mbr.getUser().getName() : mbr.getNickname(), true);
        embed.addField("Discriminator", "#" + mbr.getUser().getDiscriminator(), true);
        embed.addField("Mention", mbr.getAsMention(), true);
        embed.addField("Permission", permission.getName(), true);
        embed.addField("Voice Channel", mbr.getVoiceState().getChannel() == null ? "Not Connected" : mbr.getVoiceState().getChannel().getName(), true);
        embed.addField("ID", mbr.getId(), true);
        embed.addField("Server Join", mbr.getTimeJoined().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("Discord Join", mbr.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        embed.addField("Boosted Time", mbr.getTimeBoosted() == null ? "No Boost" : mbr.getTimeBoosted().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        if (!mbr.getRoles().isEmpty()) {
            embed.addField("Role(s)", getMemberRoles(mbr), false);
        }
        return embed;
    }

    private String getMemberRoles(Member member) {
        StringBuilder roles = new StringBuilder();
        for (Role memberRoles : member.getRoles()) {
            roles.append(memberRoles.getAsMention() + " ");
        }
        return roles.deleteCharAt(roles.length() - 1).toString();
    }

    private String yesOrNo(boolean permission) {
        return (permission) ? "Yes" : "No";
    }

}