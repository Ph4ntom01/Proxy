package proxy;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import configuration.cache.Commands;
import configuration.cache.Guilds;
import configuration.cache.Members;
import configuration.constants.Category;
import configuration.constants.Command;
import configuration.constants.ID;
import configuration.constants.Permissions;
import dao.database.Dao;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import factory.DaoFactory;
import factory.PojoFactory;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class ProxyUtils {

    private ProxyUtils() {
    }

    public static String[] getArgs(Message message) {
        return message.getContentRaw().split("\\s+");
    }

    public static void sendMessage(TextChannel channel, String message) {
        channel.sendTyping().queue();
        channel.sendMessage(message).queueAfter(300, TimeUnit.MILLISECONDS);
    }

    public static void sendEmbed(TextChannel channel, ProxyEmbed embed) {
        channel.sendTyping().queue();
        channel.sendMessage(embed.getEmbed().build()).queueAfter(300, TimeUnit.MILLISECONDS);
    }

    public static String getMemberMessageEvent(String message, User user) {
        return message.replace("[member]", user.getName());
    }

    public static String getMentionnedEntity(MentionType mention, Message message, String id) {
        return (!message.getMentions(mention).isEmpty()) ? message.getMentions(mention).get(0).getId() : id;
    }

    public static String getNickname(Member member) {
        return (member.getNickname() == null) ? member.getUser().getName() : member.getNickname();
    }

    public static String getVoiceChannel(Member member) {
        return (member.getVoiceState().getChannel() == null) ? "Not Connected" : member.getVoiceState().getChannel().getName();
    }

    public static String getTimeBoosted(Member member) {
        return (member.getTimeBoosted() == null) ? "No Boost" : member.getTimeBoosted().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String activeOrInactive(int number, String unit) {
        return (number == 0) ? "Inactive" : "Active: " + number + " " + unit;
    }

    public static String activeOrInactive(boolean value) {
        return (value) ? "Active" : "Inactive";
    }

    public static String yesOrNo(boolean permission) {
        return (permission) ? "Yes" : "No";
    }

    public static String day(int days) {
        return (days == 1) ? "day" : "days";
    }

    public static Permissions getPermission(String command) {
        if (command.equalsIgnoreCase(Command.SETADMIN.getName())) {
            return Permissions.ADMINISTRATOR;
        }
        return (command.equalsIgnoreCase(Command.SETMODO.getName())) ? Permissions.MODERATOR : Permissions.USER;
    }

    public static String getMemberRoles(Member member) {
        StringBuilder roles = new StringBuilder();
        for (int i = 0; i < member.getRoles().size(); i++) {
            roles.append(member.getRoles().get(i).getAsMention() + " ");
        }
        return roles.deleteCharAt(roles.length() - 1).toString();
    }

    public static String getCommands(Category category) {
        List<Command> commands = Commands.getInstance().values().stream().filter(command -> command.getCategory() == category).sorted().collect(Collectors.toList());
        StringBuilder commandsList = new StringBuilder();
        for (Command command : commands) {
            commandsList.append("`" + command.getName() + "` ");
        }
        return commandsList.deleteCharAt(commandsList.length() - 1).toString();
    }

    public static Set<GuildPojo> getGuildsFromDiscord(List<Guild> guilds) {
        Set<GuildPojo> discordGuilds = new HashSet<>();
        for (Guild guildTmp : guilds) {
            GuildPojo guild = PojoFactory.getGuild();
            guild.setId(guildTmp.getId());
            guild.setName(guildTmp.getName());
            discordGuilds.add(guild);
        }
        return discordGuilds;
    }

    public static Set<MemberPojo> getMembersFromDiscord(List<Member> members) {
        Set<MemberPojo> discordMembers = new HashSet<>();
        for (Member memberTmp : members) {
            if (!memberTmp.getUser().isBot()) {
                MemberPojo member = PojoFactory.getMember();
                member.setGuildId(memberTmp.getGuild().getId());
                member.setId(memberTmp.getId());
                member.setName(memberTmp.getUser().getName());
                member.setPermLevel(memberTmp.isOwner() ? Permissions.ADMINISTRATOR.getLevel() : Permissions.USER.getLevel());
                discordMembers.add(member);
            }
        }
        return discordMembers;
    }

    public static MemberPojo getGuildOwnerFromDiscord(Member owner) {
        MemberPojo member = PojoFactory.getMember();
        member.setGuildId(owner.getGuild().getId());
        member.setId(owner.getId());
        member.setName(owner.getUser().getName());
        member.setPermLevel(Permissions.ADMINISTRATOR.getLevel());
        return member;
    }

    public static GuildPojo getGuildFromCache(Guild guild) {
        return Guilds.getInstance().getUnchecked(guild.getId());
    }

    public static MemberPojo getMemberFromCache(Member mbr) {
        MemberPojo member = Members.getInstance().getUnchecked(mbr.getGuild().getId() + "#" + mbr.getId());
        if (member.isEmpty()) {
            member.setGuildId(mbr.getGuild().getId());
            member.setId(mbr.getId());
            member.setName(mbr.getUser().getName());
            member.setPermLevel(Permissions.USER.getLevel());
            Dao<MemberPojo> memberDao = DaoFactory.getMemberDAO();
            memberDao.create(member);
        }
        return member;
    }

    public static void selfbotEmbed(JDA jda, Guild guildJda, GuildPojo guildPojo, TextChannel channel) {
        jda.retrieveUserById(ID.CREATOR.getId()).queue(creator -> {
            ProxyEmbed embed = new ProxyEmbed();
            embed.selfbotInfo(guildPojo, guildJda.getSelfMember(), creator);
            sendEmbed(channel, embed);
        });
    }

}