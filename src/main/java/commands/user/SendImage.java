package commands.user;

import java.awt.Color;
import java.io.File;
import java.util.Random;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class SendImage extends ACommand {

    private String path;

    public SendImage(GuildMessageReceivedEvent event, ECommand command, PGuild guild, String path) {
        super(event, command, guild);
        this.path = path;
    }

    public SendImage(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        File directory = new File(path);
        File[] files = directory.listFiles();
        File image = files[new Random().nextInt((files.length))];

        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.YELLOW);
        /*
         * "attachment://" contains only the file name, not the path, that's why a split is done on "\".
         * Next, we need to split the entire path and search for a String who contains ".png", ".jpg",
         * ".jpeg" or ".gif". Finally, we have only the file name to concat with "attachment".
         * 
         * FOR WINDOWS
         * 
         * The double backward slash is treated as a single backward slash
         * "\" in regular expression. So four backward slash "\\\\" should be added to match a single
         * backward slash in a String.
         * 
         * FOR UNIX
         * 
         * Just use "/".
         */
        for (String pathTmp : image.getPath().split("/")) {
            if (pathTmp.contains(".png") || pathTmp.contains(".jpg") || pathTmp.contains(".jpeg") || pathTmp.contains(".gif")) {
                embed.setImage("attachment://" + pathTmp);
                embed.setFooter(getAuthor().getAsTag(), getAuthor().getAvatarUrl());
                getMessage().delete().queue();
                getChannel().sendFile(image).embed(embed.build()).queue();
            }
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Generate a random issou image.\n\nExample: `" + getGuildPrefix() + getCommandName() + "`.");
        } else {
            sendMessage("Generate a random issou image. **Example:** `" + getGuildPrefix() + getCommandName() + "`.");
        }
    }

}