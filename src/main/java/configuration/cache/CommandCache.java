package configuration.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.map.UnmodifiableMap;

import configuration.constant.Command;

public enum CommandCache {

    INSTANCE;

    private final Map<String, Command> commands = new ConcurrentHashMap<>();

    private CommandCache() {
        for (Command commandTmp : Command.values()) {
            commands.put(commandTmp.getName(), commandTmp);
        }
    }

    public Map<String, Command> getCommands() {
        return UnmodifiableMap.unmodifiableMap(commands);
    }

}