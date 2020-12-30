package configuration.constant;

public enum Command {

    // @formatter:off
    INFO("info", Permissions.USER, null),
    HELP("help", Permissions.USER, null),

    // Administrator commands
    PREFIX("prefix", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),
    SETADMIN("setadmin", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),
    SETMODO("setmodo", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),
    SETUSER("setuser", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),
    JOINCHAN("joinchan", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),
    JOINMESSAGE("joinmsg", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),
    JOINEMBED("joinbox", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),
    LEAVECHAN("leavechan", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),
    LEAVEMESSAGE("leavemsg", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),
    LEAVEEMBED("leavebox", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),
    CONTROLCHAN("controlchan", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),
    DEFROLE("defrole", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),
    SHIELD("shield", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),
    DISABLE("disable", Permissions.ADMINISTRATOR, Category.ADMINISTRATION),

    // Moderator commands
    CLEAN("clean", Permissions.MODERATOR, Category.MODERATION),
    SLOWMODE("slowmode", Permissions.MODERATOR, Category.MODERATION),
    VOICEKICK("voicekick", Permissions.MODERATOR, Category.MODERATION),
    VOICEMUTE("voicemute", Permissions.MODERATOR, Category.MODERATION),
    VOICEUNMUTE("voiceunmute", Permissions.MODERATOR, Category.MODERATION),
    KICK("kick", Permissions.MODERATOR, Category.MODERATION),
    BAN("ban", Permissions.MODERATOR, Category.MODERATION),
    SOFTBAN("softban", Permissions.MODERATOR, Category.MODERATION),
    UNBAN("unban", Permissions.MODERATOR, Category.MODERATION),
    PURGE("purge", Permissions.MODERATOR, Category.MODERATION),
    RESETCHAN("resetchan", Permissions.MODERATOR, Category.MODERATION),

    // User commands
    PING("ping", Permissions.USER, Category.UTILITY),
    UPTIME("uptime", Permissions.USER, Category.UTILITY),
    GUILDINFO("server", Permissions.USER, Category.UTILITY),
    MEMBERINFO("member", Permissions.USER, Category.UTILITY),
    AVATAR("avatar", Permissions.USER, Category.UTILITY),
    TEXTCHANINFO("textchan", Permissions.USER, Category.UTILITY),
    CONTROLGATE("controlgate", Permissions.USER, Category.UTILITY),
    BANLIST("banlist", Permissions.USER, Category.UTILITY),
    MODOLIST("modolist", Permissions.USER, Category.UTILITY),
    ADMINLIST("adminlist", Permissions.USER, Category.UTILITY),
    ISSOU("issou", Permissions.USER, Category.MEME);
    // @formatter:on

    private final String name;
    private final Permissions permission;
    private final Category category;

    private Command(String name, Permissions permission, Category category) {
        this.name = name;
        this.permission = permission;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public Permissions getPermission() {
        return permission;
    }

    public Category getCategory() {
        return category;
    }

}