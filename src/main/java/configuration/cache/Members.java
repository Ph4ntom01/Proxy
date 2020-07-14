package configuration.cache;

import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import dao.database.Dao;
import dao.pojo.MemberPojo;
import factory.DaoFactory;

public class Members {

    private static volatile Members instance;
    private static LoadingCache<String, MemberPojo> cache;

    private Members() {
        cache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MINUTES).build(new CacheLoader<String, MemberPojo>() {

            @Override
            public MemberPojo load(String user) throws Exception {
                Dao<MemberPojo> memberDao = DaoFactory.getMemberDAO();
                return memberDao.find(user);
            }
        });
    }

    public static LoadingCache<String, MemberPojo> getInstance() {
        if (instance == null) {
            synchronized (Members.class) {
                if (instance == null) {
                    instance = new Members();
                }
            }
        }
        return cache;
    }

}