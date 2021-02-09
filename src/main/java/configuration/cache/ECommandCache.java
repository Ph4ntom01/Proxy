package configuration.cache;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import org.apache.commons.collections4.map.UnmodifiableMap;

import configuration.constant.ECommand;

public enum ECommandCache {

    INSTANCE;

    private final Map<String, ECommand> cache = new ConcurrentHashMap<>();

    private ECommandCache() {
        for (ECommand commandTmp : ECommand.values()) {
            cache.put(commandTmp.getName(), commandTmp);
        }
    }

    /**
     * A CompletableFuture that is asynchronously completed by an unmodifiable map containing the
     * {@link configuration.constant.ECommand ECommands}.
     * 
     * @return A CompletableFuture that is asynchronously completed by the
     *         {@link configuration.constant.ECommand ECommands}.
     * 
     * @see java.util.concurrent.CompletableFuture#supplyAsync(java.util.function.Supplier)
     *      CompletableFuture.supplyAsync
     */
    @Nonnull
    public CompletableFuture<Map<String, ECommand>> getCommandsAsync() {
        return CompletableFuture.supplyAsync(() -> UnmodifiableMap.unmodifiableMap(cache));
    }

    /**
     * Retrieve the {@link configuration.constant.ECommand ECommands} as an unmodifiable map.
     * 
     * @return An unmodifiable map containing the {@link configuration.constant.ECommand ECommands}.
     */
    @Nonnull
    public Map<String, ECommand> getCommands() {
        return UnmodifiableMap.unmodifiableMap(cache);
    }

    /**
     * Retrieve the {@link configuration.constant.ECommand ECommand}.
     * 
     * @param command The command with which the specified value is to be associated.
     * 
     * @return The result {@link configuration.constant.ECommand ECommand}.
     * 
     * @see java.util.Map#get(Object) Map.get
     */
    @Nonnull
    public ECommand getCommand(String command) {
        return cache.get(command);
    }

}