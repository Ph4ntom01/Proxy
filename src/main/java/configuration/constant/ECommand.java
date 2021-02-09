package configuration.constant;

public enum ECommand {

    // @formatter:off
    INFO("info", EPermission.USER, null),
    HELP("help", EPermission.USER, null),
    
    // Bot owner commands.
    INVITE("invite", EPermission.BOT_OWNER, null),
    
    // Guild owner commands.
    SETADMIN("setadmin", EPermission.GUILD_OWNER, ECategory.ADMINISTRATION),
    SETMODO("setmodo", EPermission.GUILD_OWNER, ECategory.ADMINISTRATION),
    SETUSER("setuser", EPermission.GUILD_OWNER, ECategory.ADMINISTRATION),

    // Administrator commands.
    PREFIX("prefix", EPermission.ADMINISTRATOR, ECategory.ADMINISTRATION),
    JOINCHAN("joinchan", EPermission.ADMINISTRATOR, ECategory.ADMINISTRATION),
    JOINMESSAGE("joinmsg", EPermission.ADMINISTRATOR, ECategory.ADMINISTRATION),
    JOINEMBED("joinbox", EPermission.ADMINISTRATOR, ECategory.ADMINISTRATION),
    LEAVECHAN("leavechan", EPermission.ADMINISTRATOR, ECategory.ADMINISTRATION),
    LEAVEMESSAGE("leavemsg", EPermission.ADMINISTRATOR, ECategory.ADMINISTRATION),
    LEAVEEMBED("leavebox", EPermission.ADMINISTRATOR, ECategory.ADMINISTRATION),
    CONTROLCHAN("controlchan", EPermission.ADMINISTRATOR, ECategory.ADMINISTRATION),
    DEFROLE("defrole", EPermission.ADMINISTRATOR, ECategory.ADMINISTRATION),
    SHIELD("shield", EPermission.ADMINISTRATOR, ECategory.ADMINISTRATION),
    DISABLE("disable", EPermission.ADMINISTRATOR, ECategory.ADMINISTRATION),

    // Moderator commands.
    CLEAN("clean", EPermission.MODERATOR, ECategory.MODERATION),
    SLOWMODE("slowmode", EPermission.MODERATOR, ECategory.MODERATION),
    VOICEKICK("voicekick", EPermission.MODERATOR, ECategory.MODERATION),
    VOICEMUTE("voicemute", EPermission.MODERATOR, ECategory.MODERATION),
    VOICEUNMUTE("voiceunmute", EPermission.MODERATOR, ECategory.MODERATION),
    KICK("kick", EPermission.MODERATOR, ECategory.MODERATION),
    BAN("ban", EPermission.MODERATOR, ECategory.MODERATION),
    SOFTBAN("softban", EPermission.MODERATOR, ECategory.MODERATION),
    UNBAN("unban", EPermission.MODERATOR, ECategory.MODERATION),
    PURGE("purge", EPermission.MODERATOR, ECategory.MODERATION),
    RESETCHAN("resetchan", EPermission.MODERATOR, ECategory.MODERATION),

    // User commands.
    PING("ping", EPermission.USER, ECategory.UTILITY),
    UPTIME("uptime", EPermission.USER, ECategory.UTILITY),
    GUILDINFO("server", EPermission.USER, ECategory.UTILITY),
    MEMBERINFO("member", EPermission.USER, ECategory.UTILITY),
    AVATAR("avatar", EPermission.USER, ECategory.UTILITY),
    TEXTCHANINFO("textchan", EPermission.USER, ECategory.UTILITY),
    CONTROLGATE("controlgate", EPermission.USER, ECategory.UTILITY),
    BANLIST("banlist", EPermission.USER, ECategory.UTILITY),
    MODOLIST("modolist", EPermission.USER, ECategory.UTILITY),
    ADMINLIST("adminlist", EPermission.USER, ECategory.UTILITY),
    ISSOU("issou", EPermission.USER, ECategory.MEME);
    // @formatter:on

    private final String name;
    private final EPermission permission;
    private final ECategory category;

    private ECommand(String name, EPermission permission, ECategory category) {
        this.name = name;
        this.permission = permission;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public EPermission getPermission() {
        return permission;
    }

    public ECategory getCategory() {
        return category;
    }

}