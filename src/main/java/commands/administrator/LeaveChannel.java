package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.database.ChannelLeaveDAO;
import dao.database.Dao;
import dao.pojo.ChannelLeavePojo;
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
        String textChannelID = ProxyUtils.getArgs(event)[1];
        try {
            Dao<ChannelLeavePojo> channelLeaveDao = DaoFactory.getChannelLeaveDAO();
            ChannelLeavePojo channelLeave = channelLeaveDao.find(guild.getChannelLeave());
            TextChannel textChannel = event.getGuild().getTextChannelById(ProxyUtils.getMentionnedEntity(MentionType.CHANNEL, event.getMessage(), textChannelID));

            if (textChannel.getId().equals(guild.getChannelLeave()) && textChannel.getId().equals(channelLeave.getChannelId())) {
                ProxyUtils.sendMessage(event, "The default channel for leaving members has already been set to " + textChannel.getAsMention() + ".");

            } else if (guild.getChannelLeave() == null && channelLeave.getChannelId() == null) {
                Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                channelLeave = PojoFactory.getChannelLeave();
                channelLeave.setChannelId(textChannel.getId());
                guild.setChannelLeave(textChannel.getId());
                channelLeaveDao.create(channelLeave);
                guildDao.update(guild);
                ProxyUtils.sendMessage(event, "The default channel for leaving members is now " + textChannel.getAsMention() + ".");

            } else {
                guild.setChannelLeave(textChannel.getId());
                ((ChannelLeaveDAO) channelLeaveDao).update(channelLeave, textChannel.getId());
                // No need to update the guild table with "guildDao.update" because ON UPDATE
                // CASCADE is defined to the foreign key.
                ProxyUtils.sendMessage(event, "The default channel for leaving members is now " + textChannel.getAsMention() + ".");
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event, "**" + textChannelID + "** is not a text channel.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            embed.help(Command.LEAVECHAN.getName(), "Set the leaving channel.\n\nExample: `" + guild.getPrefix() + Command.LEAVECHAN.getName() + " #aTextChannel`", Color.ORANGE);
            ProxyUtils.sendEmbed(event, embed);
        } else {
            ProxyUtils.sendMessage(event, "Set the leaving channel. **Example:** `" + guild.getPrefix() + Command.LEAVECHAN.getName() + " #aTextChannel`.");
        }
    }

}