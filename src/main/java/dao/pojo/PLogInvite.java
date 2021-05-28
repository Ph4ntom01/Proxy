package dao.pojo;

import java.sql.Timestamp;

import org.json.JSONObject;

import configuration.constant.EInviteState;
import configuration.constant.ELogInvite;
import net.dv8tion.jda.api.entities.Guild;

public class PLogInvite {

    private String guildLog;

    public String getGuild() {
        return guildLog;
    }

    public void setGuild(String guildLog) {
        this.guildLog = guildLog;
    }

    public JSONObject getJSONGuildJoin(Guild guild) {
        JSONObject joinedLog = new JSONObject();
        joinedLog.put(ELogInvite.STATE.getName(), EInviteState.JOIN_STATE.getName());
        joinedLog.put(ELogInvite.ID.getName(), guild.getId());
        joinedLog.put(ELogInvite.NAME.getName(), guild.getName());
        joinedLog.put(ELogInvite.DATE.getName(), new Timestamp(System.currentTimeMillis()));
        return joinedLog;
    }

    public JSONObject getJSONGuildLeave(PGuild guild) {
        JSONObject leftLog = new JSONObject();
        leftLog.put(ELogInvite.STATE.getName(), EInviteState.LEAVE_STATE.getName());
        leftLog.put(ELogInvite.ID.getName(), guild.getId().toString());
        leftLog.put(ELogInvite.NAME.getName(), guild.getName());
        leftLog.put(ELogInvite.JOIN_CHANNEL.getName(), checkNObject(guild.getJoinChannel()));
        leftLog.put(ELogInvite.LEAVE_CHANNEL.getName(), checkNObject(guild.getLeaveChannel()));
        leftLog.put(ELogInvite.CONTROL_CHANNEL.getName(), checkNObject(guild.getControlChannel()));
        leftLog.put(ELogInvite.DEFAULT_ROLE.getName(), checkNObject(guild.getDefaultRole()));
        leftLog.put(ELogInvite.PREFIX.getName(), guild.getPrefix());
        leftLog.put(ELogInvite.SHIELD.getName(), guild.getShield());
        leftLog.put(ELogInvite.DATE.getName(), new Timestamp(System.currentTimeMillis()));
        return leftLog;
    }

    private String checkNObject(Object object) {
        return (object == null) ? "None" : object.toString();
    }

}