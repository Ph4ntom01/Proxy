package commands.user;

import java.awt.Color;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.requests.RestAction;

public class Avatar extends ACommand {

    public Avatar(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        super(event, args, command, guild);
    }

    public Avatar(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        RestAction<Member> command = retrieveMentionnedMember(1, false);
        if (command == null) { return; }
        command.queue(mentionnedMember -> {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.GREEN);
            embed.setImage(mentionnedMember.getUser().getEffectiveAvatarUrl() + "?size=512");
            sendEmbed(embed);
        }, ContextException.here(acceptor -> sendMessage("**" + getArgs()[1] + "** is not a member.")));
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Display the member's avatar.\n\n"
                    + "Example: `" + getGuildPrefix() + getCommandName() + " @aMember`\n\n"
                    + "*You can also mention a member by his ID*.");
            // @formatter:on
        } else {
            sendMessage("Display the member's avatar. **Example:** `" + getGuildPrefix() + getCommandName() + " @aMember`.");
        }
    }

}