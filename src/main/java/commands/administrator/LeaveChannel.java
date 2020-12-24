package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.database.LeaveChannelDAO;
import dao.database.Dao;
import dao.pojo.LeaveChannelPojo;
import dao.pojo.GuildPojo;
import factory.DaoFactory;
import factory.PojoFactory;
import listeners.commands.AdministratorListener;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class LeaveChannel extends AdministratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public LeaveChannel(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        String textChannelID = ProxyUtils.getArgs(event.getMessage())[1];
        try {
            Dao<LeaveChannelPojo> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
            LeaveChannelPojo leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());
            TextChannel textChannel = event.getGuild().getTextChannelById(ProxyUtils.getMentionnedEntity(MentionType.CHANNEL, event.getMessage(), textChannelID));

            if (textChannel.getId().equals(guild.getLeaveChannel()) && textChannel.getId().equals(leaveChannel.getChannelId())) {
                ProxyUtils.sendMessage(event.getChannel(), "The default channel for leaving members has already been set to " + textChannel.getAsMention() + ".");

            } else if (guild.getLeaveChannel() == null && leaveChannel.getChannelId() == null) {
                Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                leaveChannel = PojoFactory.getLeaveChannel();
                leaveChannel.setChannelId(textChannel.getId());
                guild.setLeaveChannel(textChannel.getId());
                leaveChannelDao.create(leaveChannel);
                guildDao.update(guild);
                ProxyUtils.sendMessage(event.getChannel(), "The default channel for leaving members is now " + textChannel.getAsMention() + ".");

            } else {
                guild.setLeaveChannel(textChannel.getId());
                ((LeaveChannelDAO) leaveChannelDao).update(leaveChannel, textChannel.getId());
                // No need to update the guild table with "guildDao.update" because ON UPDATE
                // CASCADE is defined to the foreign key.
                ProxyUtils.sendMessage(event.getChannel(), "The default channel for leaving members is now " + textChannel.getAsMention() + ".");
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event.getChannel(), "**" + textChannelID + "** is not a text channel.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.LEAVECHAN.getName(),
                    "Send notification when a member leaves the server.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.LEAVECHAN.getName() + " #aTextChannel`\n\n"
                    + "*You can also mention a channel by his ID*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Send notification when a member leaves the server. **Example:** `" + guild.getPrefix() + Command.LEAVECHAN.getName() + " #aTextChannel`.");
        }
    }

}