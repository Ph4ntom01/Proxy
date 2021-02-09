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

public class Adminlist extends ACommand {

    public Adminlist(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        ADao<PGuildMember> gMemberDao = DaoFactory.getGuildMemberDAO();
        Set<PGuildMember> administrators = ((GuildMemberDAO) gMemberDao).findMembersByPerm(getGuild().getIdLong(), EPermission.ADMINISTRATOR);
        if (administrators.isEmpty()) {
            // @formatter:off
            sendMessage(
                    "No " + EPermission.ADMINISTRATOR.getName().toLowerCase() + "(s). "
                    + "To define a " + EPermission.ADMINISTRATOR.getName().toLowerCase()
                    + ", the guild owner has to use the command `" + getGuildPrefix() + ECommand.SETADMIN.getName() + " @aMember`.");
            // @formatter:on
        } else {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle(":closed_lock_with_key: __Administrator(s)__");
            for (PGuildMember admin : administrators) {
                embed.addField("", "<@" + admin.getId() + ">", false);
            }
            sendEmbed(embed);
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Display the bot administrators.\n\nExample: `" + getGuildPrefix() + getCommandName() + "`.");
        } else {
            sendMessage("Display the bot administrators. **Example:** `" + getGuildPrefix() + getCommandName() + "`.");
        }
    }

}