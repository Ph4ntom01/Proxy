package configuration.constant;

public enum EID {

    BOT_ID("592680274962415635"), BOT_OWNER("500688503617749023");

    private final String id;

    private EID(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}