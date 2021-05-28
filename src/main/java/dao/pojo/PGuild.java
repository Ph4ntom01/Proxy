package dao.pojo;

public class PGuild {

    private Long id;
    private String name;
    private Long joinChannel;
    private Long leaveChannel;
    private Long controlChannel;
    private Long defaultRole;
    private String prefix;
    private int shield;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getJoinChannel() {
        return joinChannel;
    }

    public Long getLeaveChannel() {
        return leaveChannel;
    }

    public Long getControlChannel() {
        return controlChannel;
    }

    public Long getDefaultRole() {
        return defaultRole;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getShield() {
        return shield;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJoinChannel(Long joinChannel) {
        this.joinChannel = joinChannel;
    }

    public void setLeaveChannel(Long leaveChannel) {
        this.leaveChannel = leaveChannel;
    }

    public void setControlChannel(Long controlChannel) {
        this.controlChannel = controlChannel;
    }

    public void setDefaultRole(Long defaultRole) {
        this.defaultRole = defaultRole;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setShield(int shield) {
        this.shield = shield;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof PGuild)) { return false; }
        PGuild guild = (PGuild) object;
        return guild.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode() + 1;
    }

}