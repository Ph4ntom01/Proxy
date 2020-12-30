package commands.user;

import java.awt.Color;

import commands.CommandManager;
import configuration.constant.Command;
import dao.database.Dao;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyUtils;

public class ControlGate implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public ControlGate(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        Dao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
        PJoinChannel joinChannel = joinChannelDao.find(guild.getJoinChannel());
        Dao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
        PLeaveChannel leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());
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