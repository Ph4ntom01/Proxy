package configuration.constant;

public enum Folder {

    RESOURCES("resources/"), CONFIG("config/"), LOG("logs/"), IMG("img/"), ISSOU("img/issou/");

    private final String name;

    private Folder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}