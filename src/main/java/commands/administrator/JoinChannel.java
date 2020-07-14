package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.database.ChannelJoinDAO;
import dao.database.Dao;
import dao.pojo.ChannelJoinPojo;
import dao.pojo.GuildPojo;
import factory.DaoFactory;
import factory.PojoFactory;
import listeners.commands.AdministratorListener;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class JoinChannel extends AdministratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public JoinChannel(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        String textChannelID = ProxyUtils.getArgs(event)[1];
        try {
            Dao<ChannelJoinPojo> channelJoinDao = DaoFactory.getChannelJoinDAO();
            ChannelJoinPojo channelJoin = channelJoinDao.find(guild.getChannelJoin());
            TextChannel textChannel = event.getGuild().getTextChannelById(ProxyUtils.getMentionnedEntity(MentionType.CHANNEL, event.getMessage(), textChannelID));

            if (textChannel.getId().equals(guild.getChannelJoin()) && textChannel.getId().equals(channelJoin.getChannelId())) {
                ProxyUtils.sendMessage(event, "The default channel for new members has already been set to " + textChannel.getAsMention() + ".");

            } else if (guild.getChannelJoin() == null && channelJoin.getChannelId() == null) {
                Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                channelJoin = PojoFactory.getChannelJoin();
                channelJoin.setChannelId(textChannel.getId());
                guild.setChannelJoin(textChannel.getId());
                channelJoinDao.create(channelJoin);
                guildDao.update(guild);
                ProxyUtils.sendMessage(event, "The default channel for new members is now " + textChannel.getAsMention() + ".");

            } else {
                guild.setChannelJoin(textChannel.getId());
                ((ChannelJoinDAO) channelJoinDao).update(channelJoin, textChannel.getId());
                // No need to update the guild table with "guildDao.update" because ON UPDATE
                // CASCADE is defined to the foreign key.
                ProxyUtils.sendMessage(event, "The default channel for new members is now " + textChannel.getAsMention() + ".");
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event, "**" + textChannelID + "** is not a text channel.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.JOINCHAN.getName(),
                    "Set the welcoming channel.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.JOINCHAN.getName() + " #aTextChannel`",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event, embed);
        } else {
            ProxyUtils.sendMessage(event, "Set the welcoming channel. **Example:** `" + guild.getPrefix() + Command.JOINCHAN.getName() + " #aTextChannel`.");
        }
    }

}