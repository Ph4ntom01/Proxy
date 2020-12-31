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
        Config conf = ConfigFactory.getConf();
        guilds = UnmodifiableList.unmodifiableList(conf.getList("blacklist.guilds.id"));
        members = UnmodifiableList.unmodifiableList(conf.getList("blacklist.members.id"));
    }

    public boolean getGuild(String guildId) {
        return guilds.contains(guildId);
    }

    public boolean getMember(String memberId) {
        return members.contains(memberId);
    }

}