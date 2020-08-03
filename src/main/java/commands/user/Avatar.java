package commands.user;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.pojo.GuildPojo;
import listeners.commands.UserListener;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class Avatar extends UserListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public Avatar(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            event.getGuild().retrieveMemberById(ProxyUtils.getMentionnedEntity(MentionType.USER, event.getMessage(), ProxyUtils.getArgs(event)[1]), false).queue(member -> {
                ProxyEmbed embed = new ProxyEmbed();
                embed.avatar(member);
                ProxyUtils.sendEmbed(event, embed);
            }, ContextException.here(acceptor -> ProxyUtils.sendMessage(event, "Invalid ID or mention.")));

        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event, "Invalid ID or mention.");
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
            ProxyUtils.sendEmbed(event, embed);
        } else {
            ProxyUtils.sendMessage(event, "Display the member's avatar. **Example:** `" + guild.getPrefix() + Command.AVATAR.getName() + " @aMember`.");
        }
    }

}