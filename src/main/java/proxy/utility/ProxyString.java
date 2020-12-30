package proxy.utility;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.User;

public class ProxyString {

    private ProxyString() {
    }

    public static String getMemberMessageEvent(String message, User user) {
        return message.replace("[member]", user.getName());
    }

    public static String getMentionnedEntity(MentionType mention, Message message, String id) {
        return (!message.getMentions(mention).isEmpty()) ? message.getMentions(mention).get(0).getId() : id;
    }

    public static String getNickname(Member member) {
        return (member.getNickname() == null) ? member.getUser().getName() : member.getNickname();
    }

    public static String getVoiceChannel(Member member) {
        return (member.getVoiceState().getChannel() == null) ? "Not Connected" : member.getVoiceState().getChannel().getName();
    }

    public static String getTimeBoosted(Member member) {
        return (member.getTimeBoosted() == null) ? "No Boost" : member.getTimeBoosted().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public static String activeOrInactive(int number, String unit) {
        return (number == 0) ? "Inactive" : "Active: " + number + " " + unit;
    }

    public static String activeOrInactive(boolean value) {
        return (value) ? "Active" : "Inactive";
    }

    public static String yesOrNo(boolean permission) {
        return (permission) ? "Yes" : "No";
    }

    public static String day(int days) {
        return (days == 1) ? "day" : "days";
    }

    public static TimeUnit getTimeUnit(String unit) {
        switch (unit) {
        case "sec":
            return TimeUnit.SECONDS;
        case "min":
            return TimeUnit.MINUTES;
        case "hour":
            return TimeUnit.HOURS;
        default:
            return TimeUnit.MILLISECONDS;
        }
    }

}