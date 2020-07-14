package configuration.constants;

public enum File {

    BLACKLIST_FILE("blacklist.json"), DATASOURCE_FILE("datasource.json"), LINKS_FILE("links.json"), TOKENS_FILE("tokens.json");

    private final String name;

    private File(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}