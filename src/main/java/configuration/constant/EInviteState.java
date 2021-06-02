package configuration.constant;

public enum EInviteState {

    JOIN_STATE("joined"), LEAVE_STATE("left");

    private String state;

    private EInviteState(String state) {
        this.state = state;
    }

    public String getName() {
        return state;
    }

}