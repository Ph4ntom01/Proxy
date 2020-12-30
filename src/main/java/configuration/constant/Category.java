package configuration.constant;

public enum Category {

    ADMINISTRATION("administration"), MODERATION("moderation"), UTILITY("utility"), MEME("meme");

    private final String name;

    private Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}