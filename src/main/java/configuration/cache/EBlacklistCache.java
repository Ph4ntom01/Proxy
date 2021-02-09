package configuration.cache;

import java.util.List;

import org.apache.commons.collections4.list.UnmodifiableList;

import configuration.file.Config;
import configuration.file.ConfigFactory;

public enum EBlacklistCache {

    INSTANCE;

    private final List<String> guildsCache;
    private final List<String> membersCache;

    private EBlacklistCache() {
        Config conf = ConfigFactory.getConf();
        guildsCache = UnmodifiableList.unmodifiableList(conf.getList("blacklist.guilds.id"));
        membersCache = UnmodifiableList.unmodifiableList(conf.getList("blacklist.members.id"));
    }

    /**
     * Check if the specified guild is blacklisted.
     * 
     * @param guildId The guild id with which the specified value is to be associated.
     * 
     * @return True if the guild is blacklisted, otherwise false.
     */
    public boolean isGuildBlacklisted(String guildId) {
        return guildsCache.contains(guildId);
    }

    /**
     * Check if the specified member is blacklisted.
     * 
     * @param memberId The member id with which the specified value is to be associated.
     * 
     * @return True if the member is blacklisted, otherwise false.
     */
    public boolean isMemberBlacklisted(String memberId) {
        return membersCache.contains(memberId);
    }

}