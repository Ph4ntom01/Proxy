package configuration.cache;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import configuration.file.ConfigFactory;
import configuration.file.TOMLConfig;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;

public enum EGuildCache {

    INSTANCE;

    private final AsyncLoadingCache<Long, PGuild> cache;

    private EGuildCache() {
        TOMLConfig file = ConfigFactory.getProxy();
        // @formatter:off
        cache = Caffeine.newBuilder()
                .maximumSize(file.getLong("cache.guild.maxSize"))
                .expireAfterWrite(file.getLong("cache.guild.expireAfter"), file.getTimeUnit("cache.guild.timeUnit"))
                .buildAsync(this::find);
        // @formatter:on
    }

    /**
     * Retrieve the {@link dao.pojo.PGuild PGuild} from the database.
     * 
     * @param guildId The guild id.
     * 
     * @return The {@link dao.pojo.PGuild PGuild}. Possibly-null if a database access error occurs.
     */
    @Nullable
    private PGuild find(Long guildId) {
        ADao<PGuild> guildDao = DaoFactory.getPGuildDAO();
        return guildDao.find(guildId);
    }

    /**
     * Returns the future associated with key in this cache as an asynchronous computation.
     * 
     * @param guildId The guild id with which the specified value is to be associated.
     * 
     * @return The current (existing or computed) future {@link dao.pojo.PGuild PGuild} associated with
     *         the specified guild id.
     * 
     * @see com.github.benmanes.caffeine.cache.AsyncLoadingCache#get(Object) AsyncLoadingCache.get(Long
     *      key)
     */
    @Nullable
    public CompletableFuture<PGuild> getPGuildAsync(Long guildId) {
        return cache.get(guildId);
    }

    /**
     * Retrieve the {@link dao.pojo.PGuildMember PGuildMember}. <br>
     * This is NOT an asynchronous operation, returns the result value when complete.
     * 
     * @param guildId The guild id with which the specified value is to be associated.
     * 
     * @return The result {@link dao.pojo.PGuild PGuild}.
     * 
     * @see java.util.concurrent.CompletableFuture#join() CompletableFuture.join
     */
    public PGuild getPGuild(Long guildId) {
        return cache.get(guildId).join();
    }

    /**
     * Discards any cached value for the key.
     * 
     * @param guildId The guild id with which the specified value is to be associated.
     * 
     * @see com.github.benmanes.caffeine.cache.Cache#invalidate(Object) Cache.invalidate
     */
    public void invalidate(Long guildId) {
        cache.synchronous().invalidate(guildId);
    }

}