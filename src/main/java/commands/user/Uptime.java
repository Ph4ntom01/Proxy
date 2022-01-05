package commands.user;

import java.awt.Color;
import java.lang.management.ManagementFactory;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Uptime extends ACommand {

    public Uptime(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        final long duration = ManagementFactory.getRuntimeMXBean().getUptime();
        final long years = duration / 31104000000L;
        final long months = duration / 2592000000L % 12;
        final long days = duration / 86400000L % 30;
        final long hours = duration / 3600000L % 24;
        final long minutes = duration / 60000L % 60;
        final long seconds = duration / 1000L % 60;
        String uptime = (years == 0 ? "" : "**" + years + "** Years, ") + (months == 0 ? "" : "**" + months + "** Months, ") + (days == 0 ? "" : "**" + days + "** Days, ")
                + (hours == 0 ? "" : "**" + hours + "** Hours, ") + (minutes == 0 ? "" : "**" + minutes + "** Minutes, ") + (seconds == 0 ? "" : "**" + seconds + "** Seconds, ");
        uptime = uptime.replaceFirst("(?s)(.*)" + ", ", "$1" + "");
        uptime = uptime.replaceFirst("(?s)(.*)" + ",", "$1" + " and");
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(":stopwatch: Uptime");
        embed.setColor(Color.GREEN);
        embed.addField("", uptime + ".", true);
        sendEmbed(embed);
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Display the percentage of time the bot has been available.\n\n" + "Example: `" + getPGuildPrefix() + getCommandName() + "`.");
        } else {
            sendMessage("Display the percentage of time the bot has been available. **Example:** `" + getPGuildPrefix() + getCommandName() + "`.");
        }
    }

}