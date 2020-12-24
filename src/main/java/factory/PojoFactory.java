package factory;

import dao.pojo.JoinChannelPojo;
import dao.pojo.LeaveChannelPojo;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import dao.pojo.PermissionPojo;

public class PojoFactory {

    private PojoFactory() {
    }

    public static GuildPojo getGuild() {
        return new GuildPojo();
    }

    public static JoinChannelPojo getJoinChannel() {
        return new JoinChannelPojo();
    }

    public static LeaveChannelPojo getLeaveChannel() {
        return new LeaveChannelPojo();
    }

    public static MemberPojo getMember() {
        return new MemberPojo();
    }

    public static PermissionPojo getPermission() {
        return new PermissionPojo();
    }

}