package configuration.file;

import configuration.constant.EFolder;

public class ConfigFactory {

    private static final String PATH = EFolder.RESOURCES.getName();

    private ConfigFactory() {}

    public static Config getConf() {
        return new Config(PATH + "conf.toml");
    }

}