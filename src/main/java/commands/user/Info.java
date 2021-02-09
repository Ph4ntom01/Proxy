package commands.user;

import java.awt.Color;

import org.apache.commons.lang3.StringUtils;

import commands.ACommand;
import configuration.constant.ECategory;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Info extends ACommand {

    public Info(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        embed.setTitle(":scroll: __Commands List__");
        // @formatter:off
        embed.setDescription(
                "The bot's prefix is __**" + getGuildPrefix() + "**__\n"
                + "_For more information about a specific command, use_ __**" + getGuildPrefix() + ECommand.HELP.getName() + "**__");
        embed.addField(
                ":closed_lock_with_key: " + StringUtils.capitalize(ECategory.ADMINISTRATION.getName()),
                getCommandsByCategory(ECategory.ADMINISTRATION),
                false);
        embed.addField(
                ":dagger: " + StringUtils.capitalize(ECategory.MODERATION.getName()),
                getCommandsByCategory(ECategory.MODERATION),
                false);
        embed.addField(
                ":tools: " + StringUtils.capitalize(ECategory.UTILITY.getName()),
                getCommandsByCategory(ECategory.UTILITY),
                false);
        embed.addField(
                ":grin: " + StringUtils.capitalize(ECategory.MEME.getName()),
                getCommandsByCategory(ECategory.MEME),
                false);
        // @formatter:on
        sendEmbed(embed);
    }

    @Override
    public void help(boolean embedState) {}

}