package configuration.constants;

public enum Permissions {

    USER("User", 1), MODERATOR("Moderator", 2), ADMINISTRATOR("Administrator", 3);

    private final String name;
    private final int level;

    private Permissions(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return this.name;
    }

    public int getLevel() {
        return this.level;
    }

}