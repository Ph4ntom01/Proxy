package configuration.cache;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import dao.database.Dao;
import dao.pojo.GuildPojo;
import factory.DaoFactory;

public class Guilds {

    private static volatile Guilds instance;
    private static LoadingCache<String, GuildPojo> cache;

    private Guilds() {
        cache = CacheBuilder.newBuilder().expireAfterWrite(3, TimeUnit.MINUTES).build(new CacheLoader<String, GuildPojo>() {

            @Override
            public GuildPojo load(String guildId) throws Exception {
                Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                return guildDao.find(guildId);
            }
        });
    }

    public static LoadingCache<String, GuildPojo> getInstance() {
        if (instance == null) {
            synchronized (Guilds.class) {
                if (instance == null) {
                    instance = new Guilds();
                }
            }
        }
        return cache;
    }

}