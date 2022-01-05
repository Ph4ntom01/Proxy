package commands.user;

import java.awt.Color;
import java.util.Set;

import commands.ACommand;
import configuration.constant.ECommand;
import configuration.constant.EPermission;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.database.GuildMemberDAO;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Modolist extends ACommand {

    public Modolist(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        ADao<PGuildMember> gMemberDao = DaoFactory.getPGuildMemberDAO();
        Set<PGuildMember> moderators = ((GuildMemberDAO) gMemberDao).findPGuildMembersByPerm(getGuild().getIdLong(), EPermission.MODERATOR);
        if (moderators.isEmpty()) {
            // @formatter:off
            sendMessage(
                    "No " + EPermission.MODERATOR.getName().toLowerCase() + "(s). "
                    + "To define a " + EPermission.MODERATOR.getName().toLowerCase()
                    + ", the guild owner has to use the command `" + getPGuildPrefix() + ECommand.SETMODO.getName() + " @aMember`.");
            // @formatter:on
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle(":dagger: __Moderator(s)__");
            for (PGuildMember modo : moderators) {
                embed.addField("", "<@" + modo.getId() + ">", false);
            }
            sendEmbed(embed);
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Display the bot moderators.\n\nExample: `" + getPGuildPrefix() + getCommandName() + "`.");
        } else {
            sendMessage("Display the bot moderators. **Example:** `" + getPGuildPrefix() + getCommandName() + "`.");
        }
    }

}