package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.database.Dao;
import dao.pojo.JoinChannelPojo;
import dao.pojo.GuildPojo;
import factory.DaoFactory;
import listeners.commands.AdministratorListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class JoinMessage extends AdministratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public JoinMessage(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        if (guild.getJoinChannel() != null) {
            Dao<JoinChannelPojo> joinChannelDao = DaoFactory.getJoinChannelDAO();
            JoinChannelPojo joinChannel = joinChannelDao.find(guild.getJoinChannel());
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