package configuration.cache;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import configuration.constant.Permissions;
import configuration.file.Config;
import dao.database.Dao;
import dao.pojo.PGuildMember;
import factory.ConfigFactory;
import factory.DaoFactory;
import net.dv8tion.jda.api.entities.Member;
import proxy.utility.ProxyString;

public enum GuildMemberCache {

    INSTANCE;

    private final AsyncLoadingCache<Member, PGuildMember> cache;

    private GuildMemberCache() {
        Config conf = ConfigFactory.getConf();
        // @formatter:off
        cache = Caffeine.newBuilder()
                .maximumSize(conf.getLong("cache.guildMember.maxSize"))
                .expireAfterWrite(conf.getLong("cache.guildMember.expireAfter"), ProxyString.getTimeUnit(conf.getString("cache.guildMember.timeUnit")))
                .buildAsync(member -> find(member));
        // @formatter:on
    }

    private PGuildMember find(Member member) {
        Dao<PGuildMember> gMemberDao = DaoFactory.getGuildMemberDAO();
        PGuildMember gMember = gMemberDao.find(member.getGuild().getId() + "#" + member.getId());
        if (gMember.isEmpty()) {
            gMember.setGuildId(member.getGuild().getId());
            gMember.setId(member.getId());
            gMember.setName(member.getUser().getName());
            gMember.setPermId(member.isOwner() ? Permissions.ADMINISTRATOR.getLevel() : Permissions.USER.getLevel());
            gMemberDao.create(gMember);
        }
        return gMember;
    }

    public AsyncLoadingCache<Member, PGuildMember> getGuildMember() {
        return cache;
    }

}