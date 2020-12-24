package dao.pojo;

public class LeaveChannelPojo {

    private String channelId;
    private String message;
    private boolean embed;

    public String getChannelId() {
        return channelId;
    }

    public String getMessage() {
        return message;
    }

    public boolean getEmbed() {
        return embed;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setEmbed(boolean embed) {
        this.embed = embed;
    }

}