package configuration.cache;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import configuration.constant.EID;
import configuration.constant.EPermission;
import configuration.file.ConfigFactory;
import configuration.file.TOMLConfig;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuildMember;
import net.dv8tion.jda.api.entities.Member;

public enum EGuildMemberCache {

    INSTANCE;

    private final AsyncLoadingCache<Member, PGuildMember> cache;

    private EGuildMemberCache() {
        TOMLConfig file = ConfigFactory.getProxy();
        // @formatter:off
        cache = Caffeine.newBuilder()
                .maximumSize(file.getLong("cache.guildMember.maxSize"))
                .expireAfterWrite(file.getLong("cache.guildMember.expireAfter"), file.getTimeUnit("cache.guildMember.timeUnit"))
                .buildAsync(this::find);
        // @formatter:on
    }

    /**
     * Retrieve the {@link dao.pojo.PGuildMember PGuildMember} from the database.
     * 
     * @param member The {@link net.dv8tion.jda.api.entities.Member Member}.
     * 
     * @return The {@link dao.pojo.PGuildMember PGuildMember}. Possibly-null if a database access error
     *         occurs.
     */
    @Nullable
    private PGuildMember find(Member member) {
        ADao<PGuildMember> pguildMemberDao = DaoFactory.getPGuildMemberDAO();
        // Find the member with his guild id AND id.
        PGuildMember pguildMember = pguildMemberDao.find(member.getGuild().getIdLong(), member.getUser().getIdLong());
        // If the member is not yet registered in the database.
        if (pguildMember.isEmpty()) {
            pguildMember.setGuildId(member.getGuild().getIdLong());
            pguildMember.setId(member.getIdLong());
            pguildMember.setName(member.getUser().getName());
            pguildMember.setPermission(checkPermission(member));
            pguildMemberDao.create(pguildMember);
        }
        return pguildMember;
    }

    private EPermission checkPermission(Member member) {
        if (member.getId().equals(EID.BOT_OWNER.getId())) {
            return EPermission.BOT_OWNER;
        } else {
            return (member.isOwner()) ? EPermission.GUILD_OWNER : EPermission.USER;
        }
    }

    /**
     * Returns the future associated with key in this cache as an asynchronous computation.
     * 
     * @param member The {@link net.dv8tion.jda.api.entities.Member Member} with which the specified
     *               value is to be associated.
     * 
     * @return The current (existing or computed) future {@link dao.pojo.PGuildMember PGuildMember}
     *         associated with the specified {@link net.dv8tion.jda.api.entities.Member Member}.
     * 
     * @see com.github.benmanes.caffeine.cache.AsyncLoadingCache#get(Object)
     *      AsyncLoadingCache.get(Member key)
     */
    @Nullable
    public CompletableFuture<PGuildMember> getPGuildMemberAsync(Member member) {
        return cache.get(member);
    }

    /**
     * Retrieve the {@link dao.pojo.PGuildMember PGuildMember}. <br>
     * This is NOT an asynchronous operation, returns the result value when complete.
     * 
     * @param member The {@link net.dv8tion.jda.api.entities.Member Member} with which the specified
     *               value is to be associated.
     * 
     * @return The result {@link dao.pojo.PGuildMember PGuildMember}.
     * 
     * @see java.util.concurrent.CompletableFuture#join() CompletableFuture.join
     */
    public PGuildMember getPGuildMember(Member member) {
        return cache.get(member).join();
    }

}