package factory;

import dao.pojo.ChannelJoinPojo;
import dao.pojo.ChannelLeavePojo;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import dao.pojo.PermissionPojo;

public class PojoFactory {

    private PojoFactory() {
    }

    public static GuildPojo getGuild() {
        return new GuildPojo();
    }

    public static ChannelJoinPojo getChannelJoin() {
        return new ChannelJoinPojo();
    }

    public static ChannelLeavePojo getChannelLeave() {
        return new ChannelLeavePojo();
    }

    public static MemberPojo getMember() {
        return new MemberPojo();
    }

    public static PermissionPojo getPermission() {
        return new PermissionPojo();
    }

}