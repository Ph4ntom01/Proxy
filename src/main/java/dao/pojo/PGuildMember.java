package dao.pojo;

import configuration.constant.EPermission;

public class PGuildMember {

    private Long guildId;
    private Long id;
    private String name;
    private EPermission permission;

    public Long getGuildId() {
        return guildId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public EPermission getPermission() {
        return permission;
    }

    public void setGuildId(Long guildId) {
        this.guildId = guildId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermission(EPermission permission) {
        this.permission = permission;
    }

    public boolean isEmpty() {
        return (getGuildId() == null && getId() == null && getPermission() == null);
    }

}