package configuration.file;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.tuweni.toml.Toml;
import org.apache.tuweni.toml.TomlArray;
import org.apache.tuweni.toml.TomlParseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TOMLConfig {

    private static final Logger LOG = LoggerFactory.getLogger(TOMLConfig.class);

    private TomlParseResult toml;

    public TOMLConfig(String path) {
        try {
            toml = Toml.parse(Paths.get(path));
        } catch (IOException e) {
            toml.errors().forEach(error -> LOG.error(error.toString()));
        }
    }

    public String getString(String key) {
        return toml.getString(key);
    }

    public long getLong(String key) {
        return toml.getLong(key);
    }

    public boolean getBoolean(String key) {
        return toml.getBoolean(key);
    }

    public TomlArray getArray(String key) {
        return toml.getArrayOrEmpty(key);
    }

    /**
     * Gets a TimeUnit object related to the configuration file.
     * 
     * @param key The key.
     * 
     * @return A TimeUnit object related to the configuration file.
     */
    public TimeUnit getTimeUnit(String key) {
        switch (getString(key)) {
        case "hour":
            return TimeUnit.HOURS;
        case "min":
            return TimeUnit.MINUTES;
        case "sec":
            return TimeUnit.SECONDS;
        default:
            return TimeUnit.MILLISECONDS;
        }
    }

}