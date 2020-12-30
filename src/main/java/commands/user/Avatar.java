package commands.user;

import java.awt.Color;

import commands.CommandManager;
import configuration.constant.Command;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyString;
import proxy.utility.ProxyUtils;

public class Avatar implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public Avatar(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            event.getJDA().retrieveUserById(ProxyString.getMentionnedEntity(MentionType.USER, event.getMessage(), ProxyUtils.getArgs(event.getMessage())[1])).queue(user -> {
                ProxyEmbed embed = new ProxyEmbed();
                embed.avatar(user);
                ProxyUtils.sendEmbed(event.getChannel(), embed);
            }, ContextException.here(acceptor -> ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.")));

        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.AVATAR.getName(),
                    "Display the member's avatar.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.AVATAR.getName() + " @aMember`\n\n"
                    + "*You can also mention a member by his ID*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Display the member's avatar. **Example:** `" + guild.getPrefix() + Command.AVATAR.getName() + " @aMember`.");
        }
    }

}