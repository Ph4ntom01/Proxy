package commands.user;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.database.Dao;
import dao.pojo.JoinChannelPojo;
import dao.pojo.LeaveChannelPojo;
import dao.pojo.GuildPojo;
import factory.DaoFactory;
import listeners.commands.UserListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class ControlGate extends UserListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public ControlGate(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        Dao<JoinChannelPojo> joinChannelDao = DaoFactory.getJoinChannelDAO();
        JoinChannelPojo joinChannel = joinChannelDao.find(guild.getJoinChannel());
        Dao<LeaveChannelPojo> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
        LeaveChannelPojo leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());
        ProxyEmbed embed = new ProxyEmbed();
        embed.controlGate(event.getGuild(), guild, joinChannel, leaveChannel);
        ProxyUtils.sendEmbed(event.getChannel(), embed);
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.CONTROLGATE.getName(),
                    "Display the welcoming and leaving channel information.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.CONTROLGATE.getName() + "`.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Display the welcoming and leaving channel information. **Example:** `" + guild.getPrefix() + Command.CONTROLGATE.getName() + "`.");
        }
    }

}