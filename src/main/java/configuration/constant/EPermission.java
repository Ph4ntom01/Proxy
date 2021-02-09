package configuration.constant;

public enum EPermission {

    USER(1, "User"), MODERATOR(2, "Moderator"), ADMINISTRATOR(3, "Administrator"), GUILD_OWNER(4, "Guild Owner"), BOT_OWNER(5, "Bot Owner");

    private final int level;
    private final String name;

    private EPermission(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public int getLevel() {
        return this.level;
    }

    public String getName() {
        return this.name;
    }

    public static EPermission getPermission(int level) {
        if (level == EPermission.BOT_OWNER.getLevel()) { return EPermission.BOT_OWNER; }
        if (level == EPermission.GUILD_OWNER.getLevel()) { return EPermission.GUILD_OWNER; }
        if (level == EPermission.ADMINISTRATOR.getLevel()) { return EPermission.ADMINISTRATOR; }
        if (level == EPermission.MODERATOR.getLevel()) { return EPermission.MODERATOR; }
        return EPermission.USER;
    }

}