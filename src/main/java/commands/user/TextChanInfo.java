package commands.user;

import java.awt.Color;

import commands.CommandManager;
import configuration.constant.Command;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyString;
import proxy.utility.ProxyUtils;

public class TextChanInfo implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public TextChanInfo(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            ProxyEmbed embed = new ProxyEmbed();
            embed.channelInfo(event.getGuild().getTextChannelById(ProxyString.getMentionnedEntity(MentionType.CHANNEL, event.getMessage(), ProxyUtils.getArgs(event.getMessage())[1])));
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.TEXTCHANINFO.getName(),
                    "Display the text channel information.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.TEXTCHANINFO.getName() + " #aTextChannel`",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Display the text channel information. **Example:** `" + guild.getPrefix() + Command.TEXTCHANINFO.getName() + " #aTextChannel`.");
        }
    }

}