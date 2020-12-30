package proxy.utility;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import configuration.cache.BlacklistCache;
import configuration.cache.CommandCache;
import configuration.cache.GuildCache;
import configuration.cache.GuildMemberCache;
import configuration.constant.Command;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class ProxyCache {

    private ProxyCache() {
    }

    public static Map<String, Command> getCommands() {
        return CommandCache.INSTANCE.getCommands();
    }

    public static boolean getGuildBlacklist(String guildId) {
        return BlacklistCache.INSTANCE.getGuild(guildId);
    }

    public static boolean getMemberBlacklist(String memberId) {
        return BlacklistCache.INSTANCE.getMember(memberId);
    }

    public static PGuild getGuild(Guild guild) {
        try {
            return GuildCache.INSTANCE.getGuild().get(guild.getId()).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public static PGuildMember getGuildMember(Member member) {
        try {
            return GuildMemberCache.INSTANCE.getGuildMember().get(member).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

}