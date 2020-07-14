package commands.user;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.database.Dao;
import dao.pojo.ChannelJoinPojo;
import dao.pojo.ChannelLeavePojo;
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
        Dao<ChannelJoinPojo> channelJoinDao = DaoFactory.getChannelJoinDAO();
        ChannelJoinPojo channelJoin = channelJoinDao.find(guild.getChannelJoin());
        Dao<ChannelLeavePojo> channelLeaveDao = DaoFactory.getChannelLeaveDAO();
        ChannelLeavePojo channelLeave = channelLeaveDao.find(guild.getChannelLeave());
        ProxyEmbed embed = new ProxyEmbed();
        embed.controlGate(event.getGuild(), channelJoin, channelLeave);
        ProxyUtils.sendEmbed(event, embed);
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.CONTROL_GATE.getName(),
                    "Display the welcoming and leaving channel information.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.CONTROL_GATE.getName() + "`.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event, embed);
        } else {
            ProxyUtils.sendMessage(event, "Display the welcoming and leaving channel information. **Example:** `" + guild.getPrefix() + Command.CONTROL_GATE.getName() + "`.");
        }
    }

}