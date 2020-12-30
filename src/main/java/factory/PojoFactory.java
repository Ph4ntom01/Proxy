package factory;

import dao.pojo.PGuildMember;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import dao.pojo.PMember;

public class PojoFactory {

    private PojoFactory() {
    }

    public static PGuild getGuild() {
        return new PGuild();
    }

    public static PJoinChannel getJoinChannel() {
        return new PJoinChannel();
    }

    public static PLeaveChannel getLeaveChannel() {
        return new PLeaveChannel();
    }

    public static PGuildMember getGuildMember() {
        return new PGuildMember();
    }

    public static PMember getMember() {
        return new PMember();
    }

}