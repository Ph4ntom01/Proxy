package dao.pojo;

public class MemberPojo {

    private String guildId;
    private String id;
    private String name;
    private int permLevel;

    public String getGuildId() {
        return guildId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPermLevel() {
        return permLevel;
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

    public void setPermLevel(int permLevel) {
        this.permLevel = permLevel;
    }

    public boolean isEmpty() {
        return (getGuildId() == null && getId() == null && getName() == null && getPermLevel() == 0);
    }

}