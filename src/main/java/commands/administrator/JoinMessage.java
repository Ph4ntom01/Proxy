package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constant.Command;
import dao.database.Dao;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyUtils;

public class JoinMessage implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public JoinMessage(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        if (guild.getJoinChannel() != null) {
            Dao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
            PJoinChannel joinChannel = joinChannelDao.find(guild.getJoinChannel());
            joinChannel.setMessage(getJoinMessage(ProxyUtils.getArgs(event.getMessage())));
            joinChannelDao.update(joinChannel);
            ProxyUtils.sendMessage(event.getChannel(), "Welcoming message has been successfully defined.");
        } else {
            ProxyUtils.sendMessage(event.getChannel(),
                    "In order to create a welcoming message, please select your welcoming channel first using `" + guild.getPrefix() + Command.JOINCHAN.getName() + " #aTextChannel`.");
        }
    }

    private String getJoinMessage(String[] args) {
        StringBuilder welcomeMessage = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            welcomeMessage.append(args[i]);
            welcomeMessage.append(" ");
        }
        return welcomeMessage.deleteCharAt(welcomeMessage.length() - 1).toString();
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.JOINMESSAGE.getName(),
                    "Set the welcoming message.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.JOINMESSAGE.getName() + " Welcome [member] !`\n\n"
                    + "*Add `[member]` if you want the bot to mention the joining member*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Set the welcoming message. **Example:** `" + guild.getPrefix() + Command.JOINMESSAGE.getName()
                    + " Welcome [member] !`\n*Add `[member]` if you want the bot to mention the joining member*.");
        }
    }

}