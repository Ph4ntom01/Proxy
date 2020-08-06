package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.database.Dao;
import dao.pojo.ChannelJoinPojo;
import dao.pojo.GuildPojo;
import factory.DaoFactory;
import listeners.commands.AdministratorListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class JoinEmbed extends AdministratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public JoinEmbed(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        if (guild.getChannelJoin() != null) {

            Dao<ChannelJoinPojo> channelJoinDao = DaoFactory.getChannelJoinDAO();
            ChannelJoinPojo channelJoin = channelJoinDao.find(guild.getChannelJoin());

            if (ProxyUtils.getArgs(event.getMessage())[1].equalsIgnoreCase("on")) {

                if (channelJoin.getEmbed()) {
                    ProxyUtils.sendMessage(event.getChannel(), "Welcoming box has already been **enabled**.");

                } else if (!channelJoin.getEmbed()) {
                    channelJoin.setEmbed(true);
                    channelJoinDao.update(channelJoin);
                    ProxyUtils.sendMessage(event.getChannel(), "Welcoming box is now **enabled**.");
                }
            } else if (ProxyUtils.getArgs(event.getMessage())[1].equalsIgnoreCase("off")) {

                if (!channelJoin.getEmbed()) {
                    ProxyUtils.sendMessage(event.getChannel(), "Welcoming box has already been **disabled**.");

                } else if (channelJoin.getEmbed()) {
                    channelJoin.setEmbed(false);
                    channelJoinDao.update(channelJoin);
                    ProxyUtils.sendMessage(event.getChannel(), "Welcoming box is now **disabled**, you will no longer receive a box when a member joins the server.");
                }
            } else {
                ProxyUtils.sendMessage(event.getChannel(), "Please specify **on** or **off**.");
            }
        } else {
            ProxyUtils.sendMessage(event.getChannel(),
                    "In order to create a welcoming box, please select your welcoming channel first using `" + guild.getPrefix() + Command.JOINCHAN.getName() + " #aTextChannel`.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.JOINEMBED.getName(),
                    "Set the welcoming box.\n\n"
                    + "Example:\n\n`"
                    + guild.getPrefix() + Command.JOINEMBED.getName() + " on` *enables the welcoming box*.\n`"
                    + guild.getPrefix() + Command.JOINEMBED.getName() + " off` *disables the welcoming box*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Set the welcoming box. **Example:** `" + guild.getPrefix() + Command.JOINEMBED.getName() + " on` *enables the welcoming box*.");
        }
    }

}