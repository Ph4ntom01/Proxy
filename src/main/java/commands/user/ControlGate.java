package commands.user;

import java.awt.Color;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ControlGate extends ACommand {

    public ControlGate(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
        PJoinChannel joinChannel = joinChannelDao.find(getPGuild().getJoinChannel());
        ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
        PLeaveChannel leaveChannel = leaveChannelDao.find(getPGuild().getLeaveChannel());
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setTitle(":shield: Controlgate");
        embed.setThumbnail("https://media1.tenor.com/images/f02a54eef52d2867abd892ca0841a439/tenor.gif?itemid=9580001");
        embed.addField("", ":airplane_arriving: __**Arrivals**__\n_Notification when a member joins the server._", false);

        if (joinChannel.getChannelId() != null) {
            embed.addField(":pushpin: Channel", getGuild().getTextChannelById(joinChannel.getChannelId()).getAsMention(), true);
        }

        else {
            embed.addField(":pushpin: Channel", "No welcoming channel.", true);
        }

        embed.addField(":mag_right: Box", joinChannel.getEmbed() ? "Active" : "Inactive", true);

        if (joinChannel.getMessage() != null) {
            embed.addField(":page_with_curl: Message", "```\n" + joinChannel.getMessage() + "```", false);
        }

        else {
            embed.addField(":page_with_curl: Message", "```\nNo message set.```", false);
        }

        embed.addField("", ":airplane_departure: __**Departures**__\n_Notification when a member leaves the server._", false);

        if (leaveChannel.getChannelId() != null) {
            embed.addField(":pushpin: Channel", getGuild().getTextChannelById(leaveChannel.getChannelId()).getAsMention(), true);
        }

        else {
            embed.addField(":pushpin: Channel", "No leaving channel.", true);
        }

        embed.addField(":mag_right: Box", leaveChannel.getEmbed() ? "Active" : "Inactive", true);

        if (leaveChannel.getMessage() != null) {
            embed.addField(":page_with_curl: Message", "```\n" + leaveChannel.getMessage() + "```", false);
        }

        else {
            embed.addField(":page_with_curl: Message", "```\n" + "No message set." + "```", false);
        }

        if (getPGuild().getControlChannel() != null) {
            embed.addField("", ":white_check_mark: __**Control Channel**__: " + getGuild().getTextChannelById(getPGuild().getControlChannel()).getAsMention(), false);
        }

        else {
            embed.addField("", ":white_check_mark: __**Control Channel**__: Disabled", false);
        }
        sendEmbed(embed);
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Display the welcoming and leaving channel information.\n\nExample: `" + getGuildPrefix() + getCommandName() + "`.");
        } else {
            sendMessage("Display the welcoming and leaving channel information. **Example:** `" + getGuildPrefix() + getCommandName() + "`.");
        }
    }

}