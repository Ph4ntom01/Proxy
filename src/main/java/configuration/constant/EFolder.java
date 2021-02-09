package configuration.constant;

public enum EFolder {

    RESOURCES("/home/orlando/discordbots/proxy/resources/"), IMG("img/"), ISSOU("img/issou/");

    private final String name;

    private EFolder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}