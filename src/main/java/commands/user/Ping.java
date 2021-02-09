package commands.user;

import java.awt.Color;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Ping extends ACommand {

    public Ping(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        getJDA().getRestPing().queue(ping -> {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.GREEN);
            embed.setTitle(":ping_pong: Pong: `" + ping + "ms`");
            embed.setImage("https://media1.tenor.com/images/75af5262d34d2d0b5dc8c404212665a8/tenor.gif?itemid=8942945");
            sendEmbed(embed);
        });
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Display the time in milliseconds that discord took to respond to a request.\n\nExample: `" + getGuildPrefix() + getCommandName() + "`.");
        } else {
            sendMessage("Display the time in milliseconds that discord took to respond to a request. **Example:** `" + getGuildPrefix() + getCommandName() + "`.");
        }
    }

}