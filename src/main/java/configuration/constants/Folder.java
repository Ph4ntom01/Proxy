package configuration.constants;

public enum Folder {

    ROOT("/"), RESOURCES("resources"), CONFIG("/config"), LOG("/logs"), IMG("/img"), ISSOU("/img/issou");

    private final String name;

    private Folder(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}