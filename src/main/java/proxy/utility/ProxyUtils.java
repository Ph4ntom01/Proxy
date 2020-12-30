package proxy.utility;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import configuration.constant.Category;
import configuration.constant.Command;
import configuration.constant.ID;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class ProxyUtils {

    private ProxyUtils() {
    }

    public static String[] getArgs(Message message) {
        return message.getContentRaw().split("\\s+");
    }

    public static void sendMessage(TextChannel channel, String message) {
        channel.sendTyping().queue();
        channel.sendMessage(message).queueAfter(300, TimeUnit.MILLISECONDS);
    }

    public static void sendEmbed(TextChannel channel, ProxyEmbed embed) {
        channel.sendTyping().queue();
        channel.sendMessage(embed.getEmbed().build()).queueAfter(300, TimeUnit.MILLISECONDS);
    }

    public static void selfbotEmbed(JDA jda, Guild guildJda, PGuild guildPojo, TextChannel channel) {
        jda.retrieveUserById(ID.CREATOR.getId()).queue(creator -> {
            ProxyEmbed embed = new ProxyEmbed();
            embed.selfbotInfo(guildPojo, guildJda.getSelfMember(), creator);
            sendEmbed(channel, embed);
        });
    }

    public static String getCommands(Category category) {
        List<Command> commands = ProxyCache.getCommands().values().stream().filter(command -> command.getCategory() == category).sorted().collect(Collectors.toList());
        StringBuilder commandsList = new StringBuilder();
        for (Command command : commands) {
            commandsList.append("`" + command.getName() + "` ");
        }
        return commandsList.toString();
    }

}