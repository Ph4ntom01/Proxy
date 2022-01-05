package configuration.cache;

import org.apache.tuweni.toml.TomlArray;

import configuration.file.ConfigFactory;
import configuration.file.TOMLConfig;

public enum EBlacklistCache {

    INSTANCE;

    private final TomlArray guildsCache;
    private final TomlArray membersCache;

    private EBlacklistCache() {
        TOMLConfig file = ConfigFactory.getProxy();
        guildsCache = file.getArray("blacklist.guilds.id");
        membersCache = file.getArray("blacklist.members.id");
    }

    /**
     * Check if the specified guild is blacklisted.
     * 
     * @param guildId The guild id with which the specified value is to be associated.
     * 
     * @return True if the guild is blacklisted, otherwise false.
     */
    public boolean isGuildBlacklisted(String guildId) {
        return isBlacklisted(guildsCache, guildId);
    }

    /**
     * Check if the specified member is blacklisted.
     * 
     * @param memberId The member id with which the specified value is to be associated.
     * 
     * @return True if the member is blacklisted, otherwise false.
     */
    public boolean isMemberBlacklisted(String memberId) {
        return isBlacklisted(membersCache, memberId);
    }

    private boolean isBlacklisted(TomlArray blacklist, String id) {
        if (blacklist.size() == 0) { return false; }
        for (int i = 0; i < blacklist.size(); i++) {
            if (blacklist.get(i).toString().equals(id)) { return true; }
        }
        return false;
    }

}