package dao.pojo;

import java.sql.Timestamp;

public class PBan {

    private Long guildId;
    private Long id;
    private Timestamp banDate;

    public Long getGuildId() {
        return guildId;
    }

    public Long getId() {
        return id;
    }

    public Timestamp getBanDate() {
        return banDate;
    }

    public void setGuildId(Long guildId) {
        this.guildId = guildId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setBanDate(Timestamp banDate) {
        this.banDate = banDate;
    }

}