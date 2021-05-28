package configuration.constant;

public enum ELogInvite {

    ID("id"), DATE("date"), NAME("name"), STATE("state"), PREFIX("prefix"), SHIELD("shield"), DEFAULT_ROLE("default_role_id"), JOIN_CHANNEL("join_channel_id"), LEAVE_CHANNEL("leave_channel_id"),
    CONTROL_CHANNEL("control_channel_id");

    private String logInvite;

    private ELogInvite(String logInvite) {
        this.logInvite = logInvite;
    }

    public String getName() {
        return logInvite;
    }

}
