package commands.user;

import java.awt.Color;

import org.apache.commons.lang3.StringUtils;

import commands.ACommand;
import configuration.constant.ECategory;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Help extends ACommand {

    public Help(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {}

    @Override
    public void help(boolean embedState) {}

    public void categories() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(":scroll: __Help List__");
        embed.setColor(Color.GREEN);
        // @formatter:off
        embed.addField(
                ":closed_lock_with_key: " + StringUtils.capitalize(ECategory.ADMINISTRATION.getName()),
                "`" + getCommandName() + " " + ECategory.ADMINISTRATION.getName() + "`",
                true);
        embed.addField(
                ":dagger: " + StringUtils.capitalize(ECategory.MODERATION.getName()),
                "`" + getCommandName() + " " + ECategory.MODERATION.getName() + "`",
                true);
        embed.addField(
                ":tools: " + StringUtils.capitalize(ECategory.UTILITY.getName()),
                "`" + getCommandName() + " " + ECategory.UTILITY.getName() + "`",
                true);
        embed.addField(
                ":grin: " + StringUtils.capitalize(ECategory.MEME.getName()),
                "`" + getCommandName() + " " + ECategory.MEME.getName() + "`",
                true);
        // @formatter:on
        sendEmbed(embed);
    }

    public void administration() {
        // @formatter:off
        sendHelpEmbed(
                getCommandsByCategory(ECategory.ADMINISTRATION)
                + "\n\nExample: `" + getGuildPrefix() + getCommand().getName() + " " + ECommand.SHIELD.getName() + "`");
        // @formatter:on
    }

    public void moderation() {
        // @formatter:off
        sendHelpEmbed(
                getCommandsByCategory(ECategory.MODERATION)
                + "\n\nExample: `" + getGuildPrefix() + getCommandName() + " " + ECommand.PURGE.getName() + "`");
        // @formatter:on
    }

    public void utility() {
        // @formatter:off
        sendHelpEmbed(
                getCommandsByCategory(ECategory.UTILITY)
                + "\n\nExample: `" + getGuildPrefix() + getCommandName() + " " + ECommand.GUILDINFO.getName() + "`");
        // @formatter:on
    }

    public void meme() {
        // @formatter:off
        sendHelpEmbed(
                getCommandsByCategory(ECategory.MEME)
                + "\n\nExample: `" + getGuildPrefix() + getCommandName() + " " + ECommand.ISSOU.getName() + "`");
        // @formatter:on
    }

}