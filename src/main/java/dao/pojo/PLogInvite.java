package dao.pojo;

public class PLogInvite {

    private Long id;
    private boolean inviteFlag;
    private String guildLog;

    public Long getId() {
        return id;
    }

    public boolean getInviteFlag() {
        return inviteFlag;
    }

    public String getGuildLog() {
        return guildLog;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInviteFlag(boolean inviteFlag) {
        this.inviteFlag = inviteFlag;
    }

    public void setGuildLog(String guildLog) {
        this.guildLog = guildLog;
    }

}