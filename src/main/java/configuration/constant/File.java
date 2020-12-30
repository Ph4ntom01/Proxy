package configuration.constant;

public enum File {

    BLACKLIST("blacklist.json"), CACHE("cache.json"), DATASOURCE("datasource.json"), LINK("link.json"), TOKEN("token.json");

    private final String name;

    private File(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}