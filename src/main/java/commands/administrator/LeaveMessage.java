package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.database.Dao;
import dao.pojo.ChannelLeavePojo;
import dao.pojo.GuildPojo;
import factory.DaoFactory;
import listeners.commands.AdministratorListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class LeaveMessage extends AdministratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public LeaveMessage(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        if (guild.getChannelLeave() != null) {
            Dao<ChannelLeavePojo> channelLeaveDao = DaoFactory.getChannelLeaveDAO();
            ChannelLeavePojo channelLeave = channelLeaveDao.find(guild.getChannelLeave());
            channelLeave.setMessage(getLeaveMessage(ProxyUtils.getArgs(event)));
            channelLeaveDao.update(channelLeave);
            ProxyUtils.sendMessage(event, "Leaving message has been successfully defined.");
        } else {
            ProxyUtils.sendMessage(event, "In order to create a leaving message, please select your leaving channel first using **" + guild.getPrefix()
                    + Command.LEAVECHAN.getName() + " #aTextChannel**.");
        }
    }

    private String getLeaveMessage(String[] args) {
        StringBuilder leaveMessage = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            leaveMessage.append(args[i]);
            leaveMessage.append(" ");
        }
        return leaveMessage.deleteCharAt(leaveMessage.length() - 1).toString();
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            embed.help(Command.LEAVEMESSAGE.getName(), "Set the leaving message.\n\nExample: `" + guild.getPrefix() + Command.LEAVEMESSAGE.getName()
                    + " Bye bye [member] !`\n\n*Add `[member]` if you want the bot to mention the leaving member*.", Color.ORANGE);
            ProxyUtils.sendEmbed(event, embed);
        } else {
            ProxyUtils.sendMessage(event, "Set the leaving message. **Example:** `" + guild.getPrefix() + Command.LEAVEMESSAGE.getName()
                    + " Bye bye [member] !`\n*Add `[member]` if you want the bot to mention the leaving member*.");
        }
    }

}