package commands.user;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.pojo.GuildPojo;
import listeners.commands.UserListener;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class TextChanInfo extends UserListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public TextChanInfo(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            ProxyEmbed embed = new ProxyEmbed();
            embed.channelInfo(event.getGuild().getTextChannelById(ProxyUtils.getMentionnedEntity(MentionType.CHANNEL, event.getMessage(), ProxyUtils.getArgs(event)[1])));
            ProxyUtils.sendEmbed(event, embed);
        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event, "Invalid ID or mention.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.TEXTCHAN_INFO.getName(),
                    "Display the text channel information.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.TEXTCHAN_INFO.getName() + " #aTextChannel`",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event, embed);
        } else {
            ProxyUtils.sendMessage(event, "Display the text channel information. **Example:** `" + guild.getPrefix() + Command.TEXTCHAN_INFO.getName() + " #aTextChannel`.");
        }
    }

}