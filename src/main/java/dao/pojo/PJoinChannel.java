package dao.pojo;

public class PJoinChannel {

    private Long channelId;
    private String message;
    private boolean embed;

    public Long getChannelId() {
        return channelId;
    }

    public String getMessage() {
        return message;
    }

    public boolean getEmbed() {
        return embed;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setEmbed(boolean embed) {
        this.embed = embed;
    }

}