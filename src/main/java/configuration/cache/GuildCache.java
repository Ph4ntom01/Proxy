package configuration.cache;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import configuration.file.Config;
import dao.database.Dao;
import dao.pojo.PGuild;
import factory.ConfigFactory;
import factory.DaoFactory;
import proxy.utility.ProxyString;

public enum GuildCache {

    INSTANCE;

    private final AsyncLoadingCache<String, PGuild> cache;

    private GuildCache() {
        Config conf = ConfigFactory.getCache();
        // @formatter:off
        cache = Caffeine.newBuilder()
                .maximumSize(Long.parseLong(conf.getValue("Guild.maxSize")))
                .expireAfterWrite(Long.parseLong(conf.getValue("Guild.expireAfter")), ProxyString.getTimeUnit(conf.getValue("Guild.timeUnit")))
                .buildAsync(guildId -> find(guildId));
        // @formatter:on
    }

    private PGuild find(String guildId) {
        Dao<PGuild> guildDao = DaoFactory.getGuildDAO();
        return guildDao.find(guildId);
    }

    public AsyncLoadingCache<String, PGuild> getGuild() {
        return cache;
    }

}