package factory;

import configuration.constants.File;
import configuration.files.Config;

public class ConfigFactory {

    private ConfigFactory() {
    }

    public static Config blacklist() {
        return new Config(File.BLACKLIST_FILE.getName());
    }

    public static Config datasource() {
        return new Config(File.DATASOURCE_FILE.getName());
    }

    public static Config links() {
        return new Config(File.LINKS_FILE.getName());
    }

    public static Config tokens() {
        return new Config(File.TOKENS_FILE.getName());
    }

}