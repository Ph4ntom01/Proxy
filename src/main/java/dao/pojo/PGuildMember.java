package dao.pojo;

public class PGuildMember {

    private String guildId;
    private String id;
    private String name;
    private int permId;

    public String getGuildId() {
        return guildId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPermId() {
        return permId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPermId(int permId) {
        this.permId = permId;
    }

    public boolean isEmpty() {
        return (getGuildId() == null && getId() == null && getPermId() == 0);
    }

}