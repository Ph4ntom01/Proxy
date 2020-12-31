package factory;

import configuration.constant.Folder;
import configuration.file.Config;

public class ConfigFactory {

    private static final String PATH = Folder.RESOURCES.getName();

    private ConfigFactory() {
    }

    public static Config getConf() {
        return new Config(PATH + "conf.toml");
    }

}