package configuration.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;

import configuration.constants.Command;

public class Commands {

    private static volatile Commands instance;
    private static Cache<String, Command> cache;

    private Commands() {
        cache = CacheBuilder.newBuilder().maximumSize(100).build();
        for (Command command : new ArrayList<>(Arrays.asList(Command.values()))) {
            cache.put(command.getName(), command);
        }
    }

    public static Map<String, Command> getInstance() {
        if (instance == null) {
            synchronized (Commands.class) {
                if (instance == null) {
                    instance = new Commands();
                }
            }
        }
        return ImmutableMap.copyOf(cache.asMap());
    }

}