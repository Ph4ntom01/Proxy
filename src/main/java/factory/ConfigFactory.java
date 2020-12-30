package factory;

import configuration.constant.File;
import configuration.constant.Folder;
import configuration.file.Config;

public class ConfigFactory {

    private static final String PATH = Folder.RESOURCES.getName() + Folder.CONFIG.getName();

    private ConfigFactory() {
    }

    public static Config getBlacklist() {
        return new Config(PATH + File.BLACKLIST.getName());
    }

    public static Config getCache() {
        return new Config(PATH + File.CACHE.getName());
    }

    public static Config getDatasource() {
        return new Config(PATH + File.DATASOURCE.getName());
    }

    public static Config getLink() {
        return new Config(PATH + File.LINK.getName());
    }

    public static Config getToken() {
        return new Config(PATH + File.TOKEN.getName());
    }

}