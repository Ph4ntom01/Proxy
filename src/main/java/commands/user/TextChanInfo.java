package commands.user;

import java.awt.Color;
import java.time.format.DateTimeFormatter;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.PermissionOverride;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TextChanInfo extends ACommand {

    public TextChanInfo(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    public TextChanInfo(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        TextChannel textChannel = retrieveMentionnedTextChannel(1);
        if (textChannel == null) { return; }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.GREEN);
        embed.setThumbnail(textChannel.getGuild().getIconUrl());
        embed.addField("Name", textChannel.getName(), true);
        embed.addField("Position", (textChannel.getPosition() + 1) + "", true);
        embed.addField("Creation", textChannel.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
        for (PermissionOverride role : textChannel.getRolePermissionOverrides()) {
            if (role.getRole().isPublicRole()
                    && (role.getManager().getInheritedPermissions().contains(Permission.MESSAGE_WRITE) || role.getManager().getAllowedPermissions().contains(Permission.MESSAGE_WRITE))) {
                embed.addField("Lock", "Inactive", true);
            } else if (role.getRole().isPublicRole() && (role.getManager().getDeniedPermissions().contains(Permission.MESSAGE_WRITE))) {
                embed.addField("Lock", "Active", true);
            }
        }
        embed.addField("Slowmode", textChannel.getSlowmode() == 0 ? "Inactive" : "Active: " + getPGuild().getShield() + "s", true);
        embed.addField("ID", textChannel.getId(), true);
        // @formatter:off
        embed.addField(
                "Category", "```ini\n"
                + "[Name]:               " + textChannel.getParent().getName() + "\n"
                + "[ID]:                 " + textChannel.getParent().getId() + "\n"
                + "[Position]:           " + (textChannel.getParent().getPosition() + 1) + "\n"
                + "[Creation]:           " + textChannel.getParent().getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "```",
                true);
        // @formatter:on
        sendEmbed(embed);
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Display the text channel information.\n\nExample: `" + getPGuildPrefix() + getCommandName() + " #aTextChannel`");
        } else {
            sendMessage("Display the text channel information. **Example:** `" + getPGuildPrefix() + getCommandName() + " #aTextChannel`.");
        }
    }

}