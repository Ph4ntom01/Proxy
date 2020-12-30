package configuration.cache;

import java.util.List;

import org.apache.commons.collections4.list.UnmodifiableList;

import configuration.file.Config;
import factory.ConfigFactory;

public enum BlacklistCache {

    INSTANCE;

    private final List<String> guilds;
    private final List<String> members;

    private BlacklistCache() {
        Config conf = ConfigFactory.getBlacklist();
        guilds = UnmodifiableList.unmodifiableList(conf.getValues("Guilds[*].ID"));
        members = UnmodifiableList.unmodifiableList(conf.getValues("Members[*].ID"));
    }

    public boolean getGuild(String guildId) {
        return guilds.contains(guildId);
    }

    public boolean getMember(String memberId) {
        return members.contains(memberId);
    }

}