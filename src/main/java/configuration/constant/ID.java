package configuration.constant;

public enum ID {

    PROXY("592680274962415635"), CREATOR("500688503617749023");

    private final String identification;

    private ID(String identification) {
        this.identification = identification;
    }

    public String getId() {
        return identification;
    }

}