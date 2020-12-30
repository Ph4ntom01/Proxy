package dao.pojo;

public class PGuild {

    private String id;
    private String name;
    private String joinChannel;
    private String leaveChannel;
    private String controlChannel;
    private String defaultRole;
    private String prefix;
    private int shield;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getJoinChannel() {
        return joinChannel;
    }

    public String getLeaveChannel() {
        return leaveChannel;
    }

    public String getControlChannel() {
        return controlChannel;
    }

    public String getDefaultRole() {
        return defaultRole;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getShield() {
        return shield;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJoinChannel(String joinChannel) {
        this.joinChannel = joinChannel;
    }

    public void setLeaveChannel(String leaveChannel) {
        this.leaveChannel = leaveChannel;
    }

    public void setControlChannel(String controlChannel) {
        this.controlChannel = controlChannel;
    }

    public void setDefaultRole(String defaultRole) {
        this.defaultRole = defaultRole;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setShield(int shield) {
        this.shield = shield;
    }

    public boolean isEmpty() {
        return (getId() == null && getName() == null && getJoinChannel() == null && getLeaveChannel() == null && getDefaultRole() == null && getPrefix() == null && getShield() == 0);
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof PGuild)) {
            return false;
        }

        PGuild guild = (PGuild) object;
        return guild.getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode() + 1;
    }

}