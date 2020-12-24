package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.database.Dao;
import dao.pojo.GuildPojo;
import factory.DaoFactory;
import listeners.commands.AdministratorListener;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class ControlChannel extends AdministratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public ControlChannel(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        String textChannelID = ProxyUtils.getArgs(event.getMessage())[1];
        try {
            TextChannel textChannel = event.getGuild().getTextChannelById(ProxyUtils.getMentionnedEntity(MentionType.CHANNEL, event.getMessage(), textChannelID));

            if (textChannel.getId().equals(guild.getControlChannel())) {
                ProxyUtils.sendMessage(event.getChannel(), "The control channel for new members has already been set to " + textChannel.getAsMention() + ".");

            } else {

                if (guild.getDefaultRole() == null) {
                    ProxyUtils.sendMessage(event.getChannel(),
                            "In order to create your control channel, please select your default role first by using `" + guild.getPrefix() + Command.DEFROLE.getName() + " @aRole`.");
                } else {
                    Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                    guild.setControlChannel(textChannel.getId());
                    guildDao.update(guild);
                    ProxyUtils.sendMessage(event.getChannel(), "The control channel for new members is now " + textChannel.getAsMention() + ".");
                }
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
            embed.help(Command.CONTROLCHAN.getName(),
                    "Invite the new member to accept the server's terms of use by having him add a reaction.\n\nDefault reaction is: :white_check_mark:\n\n"
                    + "Example: `" + guild.getPrefix() + Command.CONTROLCHAN.getName() + " #aTextChannel`\n\n"
                    + "*You can also mention a channel by his ID*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Invite the new member to accept the server's terms of use by having him add a reaction. Default reaction is: :white_check_mark:. **Example:** `"
                    + guild.getPrefix() + Command.CONTROLCHAN.getName() + " #aTextChannel`.");
        }
    }

}