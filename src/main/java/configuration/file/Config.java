package configuration.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.moandjiezana.toml.Toml;

public class Config {

    private final Toml toml;

    public Config(String filePath) {
        toml = new Toml();
        toml.read(readFileAsString(filePath));
    }

    private String readFileAsString(String filePath) {
        try {
            return Files.readString(Paths.get(filePath));
        } catch (IOException e) {
            return null;
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

    public Date getDate(String key) {
        return toml.getDate(key);
    }

    public List<String> getList(String key) {
        return toml.getList(key);
    }

    public TimeUnit getTimeUnit(String unit) {
        switch (getString(unit)) {
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