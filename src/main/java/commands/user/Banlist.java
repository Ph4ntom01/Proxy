package commands.user;

import java.awt.Color;
import java.time.format.DateTimeFormatter;
import java.util.List;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PBan;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;

public class Banlist extends ACommand {

    public Banlist(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    public Banlist(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        RestAction<List<Ban>> command = retrieveBanlist();
        if (command == null) { return; }
        command.queue(banlist -> {
            if (banlist.isEmpty()) {
                sendMessage("No banned member.");
            } else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setColor(Color.RED);
                embed.setTitle(":skull_crossbones: __Ban List__");
                for (int i = 0; i < banlist.size(); i++) {
                    embed.addField("", "**" + (i + 1) + ". " + banlist.get(i).getUser().getName() + "**", false);
                }
                embed.addField("", "**" + getPGuildPrefix() + "banlist [a number]**", true);
                sendEmbed(embed);
            }
        });
    }

    public void consultBannedMember() {
        RestAction<List<Ban>> command = retrieveBanlist();
        if (command == null) { return; }
        command.queue(banlist -> {
            int value = getIntArg(1);
            if (value > 0 && value <= banlist.size()) {
                if (banlist.isEmpty()) {
                    sendMessage("No banned member.");
                } else {
                    int choice = value - 1;
                    User bannedUser = banlist.get(choice).getUser();
                    ADao<PBan> bMemberDao = DaoFactory.getBanDAO();
                    PBan bMember = bMemberDao.find(getPGuild().getId(), bannedUser.getIdLong());
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setColor(Color.RED);
                    embed.setThumbnail(bannedUser.getEffectiveAvatarUrl());
                    embed.addField("Name", bannedUser.getName(), true);
                    embed.addField("Discriminator", "#" + bannedUser.getDiscriminator(), true);
                    embed.addField("Bot Account", String.valueOf(bannedUser.isBot()).replace("true", "Yes").replace("false", "No"), true);
                    embed.addField("Discord Join", bannedUser.getTimeCreated().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
                    embed.addField("Ban Date", bMember.getBanDate() == null ? "Not Available" : bMember.getBanDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), true);
                    embed.addField("ID", bannedUser.getId(), true);
                    embed.addField("Reason", "_" + String.valueOf(banlist.get(choice).getReason()).replace("null", "No reason provided.") + "_", false);
                    sendEmbed(embed);
                }
            } else {
                sendMessage("Please enter a number from the banlist.");
            }
        });
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Display the banned members.\n\nExample: `" + getPGuildPrefix() + getCommandName() + "`.");
        } else {
            sendMessage("Display the banned members. **Example:** `" + getPGuildPrefix() + getCommandName() + "`.");
        }
    }

}