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

public class LeaveEmbed extends AdministratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public LeaveEmbed(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        if (guild.getChannelLeave() != null) {

            Dao<ChannelLeavePojo> channelLeaveDao = DaoFactory.getChannelLeaveDAO();
            ChannelLeavePojo channelLeave = channelLeaveDao.find(guild.getChannelLeave());

            if (ProxyUtils.getArgs(event.getMessage())[1].equalsIgnoreCase("on")) {

                if (channelLeave.getEmbed()) {
                    ProxyUtils.sendMessage(event.getChannel(), "Leaving box has already been **enabled**.");

                } else if (!channelLeave.getEmbed()) {
                    channelLeave.setEmbed(true);
                    channelLeaveDao.update(channelLeave);
                    ProxyUtils.sendMessage(event.getChannel(), "Leaving box is now **enabled**.");
                }
            } else if (ProxyUtils.getArgs(event.getMessage())[1].equalsIgnoreCase("off")) {

                if (!channelLeave.getEmbed()) {
                    ProxyUtils.sendMessage(event.getChannel(), "Leaving box has already been **disabled**.");

                } else if (channelLeave.getEmbed()) {
                    channelLeave.setEmbed(false);
                    channelLeaveDao.update(channelLeave);
                    ProxyUtils.sendMessage(event.getChannel(), "Leaving box is now **disabled**, you will no longer receive a box when a member leaves the server.");
                }
            } else {
                ProxyUtils.sendMessage(event.getChannel(), "Please specify **on** or **off**.");
            }
        } else {
            ProxyUtils.sendMessage(event.getChannel(),
                    "In order to create a leaving box, please select your leaving channel first using `" + guild.getPrefix() + Command.LEAVECHAN.getName() + " #aTextChannel`.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.LEAVEEMBED.getName(),
                    "Set the leaving box.\n\n"
                    + "Example:\n\n`" + guild.getPrefix() + Command.LEAVEEMBED.getName() + " on` *enables the leaving box*.\n"
                    + "`" + guild.getPrefix() + Command.LEAVEEMBED.getName() + " off` *disables the leaving box*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Set the leaving box. **Example:** `" + guild.getPrefix() + Command.LEAVEEMBED.getName() + " on` *enables the leaving box*.");
        }
    }

}