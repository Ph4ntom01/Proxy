package dao.pojo;

import java.util.Set;

public class GuildPojo {

    private String id;
    private String name;
    private String joinChannel;
    private String leaveChannel;
    private String controlChannel;
    private String defaultRole;
    private String prefix;
    private int shield;
    private Set<MemberPojo> members;

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

    public Set<MemberPojo> getMembers() {
        return members;
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

    public void setMembers(Set<MemberPojo> members) {
        this.members = members;
    }

    public boolean isEmpty() {
        return (getId() == null && getName() == null && getJoinChannel() == null && getLeaveChannel() == null && getDefaultRole() == null && getPrefix() == null && getShield() == 0);
    }

    @Override
    public boolean equals(Object object) {

        if (!(object instanceof GuildPojo)) {
            return false;
        }

        GuildPojo guild = (GuildPojo) object;
        return guild.getId().equals(getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode() + 1;
    }

}