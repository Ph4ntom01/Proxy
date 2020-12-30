package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constant.Command;
import dao.database.Dao;
import dao.database.JoinChannelDAO;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import factory.DaoFactory;
import factory.PojoFactory;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyString;
import proxy.utility.ProxyUtils;

public class JoinChannel implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public JoinChannel(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        String textChannelID = ProxyUtils.getArgs(event.getMessage())[1];
        try {
            Dao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
            PJoinChannel joinChannel = joinChannelDao.find(guild.getJoinChannel());
            TextChannel textChannel = event.getGuild().getTextChannelById(ProxyString.getMentionnedEntity(MentionType.CHANNEL, event.getMessage(), textChannelID));

            if (textChannel.getId().equals(guild.getJoinChannel()) && textChannel.getId().equals(joinChannel.getChannelId())) {
                ProxyUtils.sendMessage(event.getChannel(), "The default channel for new members has already been set to " + textChannel.getAsMention() + ".");

            } else if (guild.getJoinChannel() == null && joinChannel.getChannelId() == null) {
                Dao<PGuild> guildDao = DaoFactory.getGuildDAO();
                joinChannel = PojoFactory.getJoinChannel();
                joinChannel.setChannelId(textChannel.getId());
                guild.setJoinChannel(textChannel.getId());
                joinChannelDao.create(joinChannel);
                guildDao.update(guild);
                ProxyUtils.sendMessage(event.getChannel(), "The default channel for new members is now " + textChannel.getAsMention() + ".");

            } else {
                // No need to update the guild table because ON UPDATE CASCADE is defined to the foreign key.
                ((JoinChannelDAO) joinChannelDao).update(joinChannel, textChannel.getId());
                ProxyUtils.sendMessage(event.getChannel(), "The default channel for new members is now " + textChannel.getAsMention() + ".");
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
            embed.help(Command.JOINCHAN.getName(),
                    "Send notification when a member joins the server.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.JOINCHAN.getName() + " #aTextChannel`\n\n"
                    + "*You can also mention a channel by his ID*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Send notification when a member joins the server. **Example:** `" + guild.getPrefix() + Command.JOINCHAN.getName() + " #aTextChannel`.");
        }
    }

}