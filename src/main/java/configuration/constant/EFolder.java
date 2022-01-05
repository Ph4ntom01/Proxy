package configuration.constant;

public enum EFolder {

    RESOURCES("resources/"), IMG("img/"), TEMPLATE("img/templates/"), ISSOU("img/issou/");

    private final String name;

    private EFolder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}