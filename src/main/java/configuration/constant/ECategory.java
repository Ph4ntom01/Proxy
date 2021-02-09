package configuration.constant;

public enum ECategory {

    ADMINISTRATION("administration"), MODERATION("moderation"), UTILITY("utility"), MEME("meme");

    private final String name;

    private ECategory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}