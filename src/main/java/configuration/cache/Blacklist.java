package configuration.cache;

import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import configuration.files.Config;
import factory.ConfigFactory;

public class Blacklist {

    private static volatile Blacklist instance;
    private static LoadingCache<String, Boolean> cache;

    private Blacklist() {
        cache = CacheBuilder.newBuilder().maximumSize(50).expireAfterWrite(10, TimeUnit.MINUTES).build(new CacheLoader<String, Boolean>() {

            @Override
            public Boolean load(String id) throws Exception {
                Config conf = ConfigFactory.blacklist();
                return (blacklist(conf, "Guilds", id) || blacklist(conf, "Members", id));
            }

            private Boolean blacklist(Config conf, String arrayKey, String id) {
                JSONArray excludedGuilds = conf.getArray(arrayKey);
                for (int i = 0; i < excludedGuilds.size(); i++) {
                    JSONObject excludedGuild = (JSONObject) excludedGuilds.get(i);
                    if (excludedGuild.get("ID").toString().equalsIgnoreCase(id)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public static LoadingCache<String, Boolean> getInstance() {
        if (instance == null) {
            synchronized (Blacklist.class) {
                if (instance == null) {
                    instance = new Blacklist();
                }
            }
        }
        return cache;
    }

}