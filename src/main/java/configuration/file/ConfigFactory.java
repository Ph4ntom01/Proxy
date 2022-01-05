package configuration.file;

import configuration.constant.EFolder;

public class ConfigFactory {

    private static final String PATH = EFolder.RESOURCES.getName();

    private ConfigFactory() {}

    public static TOMLConfig getProxy() {
        return new TOMLConfig(PATH + "proxy.toml");
    }

}