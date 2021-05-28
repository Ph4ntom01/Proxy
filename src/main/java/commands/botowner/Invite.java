package commands.botowner;

import java.awt.Color;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import commands.ACommand;
import configuration.constant.ECommand;
import configuration.constant.EInviteState;
import configuration.constant.ELogInvite;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.database.LogInviteDAO;
import dao.pojo.PGuild;
import dao.pojo.PLogInvite;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.data.DataObject;

public class Invite extends ACommand {

    public Invite(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        getMessage().delete().queue();
        ADao<PLogInvite> logDao = DaoFactory.getLogInviteDAO();
        Set<DataObject> logs = ((LogInviteDAO) logDao).findLogs(EInviteState.JOIN_STATE);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.setTitle(":bar_chart: Logs");
        embed.addField("", ":shinto_shrine: __**Invite**__", false);
        for (DataObject logTmp : logs) {
            // @formatter:off
            embed.addField("> _" + logTmp.getString(ELogInvite.NAME.getName()) + "_", "```ini"
                    + "\n[ID]:    " + logTmp.getLong(ELogInvite.ID.getName())
                    + "\n[Date]:  " + Timestamp.valueOf(logTmp.getString(ELogInvite.DATE.getName())).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "```", false);
            // @formatter:on
        }
        logs = ((LogInviteDAO) logDao).findLogs(EInviteState.LEAVE_STATE);
        embed.addField("", ":kaaba: __**Leave**__", false);
        for (DataObject logTmp : logs) {
            // @formatter:off
            embed.addField("> _" + logTmp.getString(ELogInvite.NAME.getName()) + "_", "```ini"
                    + "\n[ID]:               " + logTmp.getLong(ELogInvite.ID.getName())
                    + "\n[Join Channel]:     " + logTmp.getString(ELogInvite.JOIN_CHANNEL.getName())
                    + "\n[Leave Channel]:    " + logTmp.getString(ELogInvite.LEAVE_CHANNEL.getName())
                    + "\n[Control Channel]:  " + logTmp.getString(ELogInvite.CONTROL_CHANNEL.getName())
                    + "\n[Default Role]:     " + logTmp.getString(ELogInvite.DEFAULT_ROLE.getName())
                    + "\n[Prefix]:           " + logTmp.getString(ELogInvite.PREFIX.getName())
                    + "\n[Shield]:           " + logTmp.getInt(ELogInvite.SHIELD.getName())
                    + "\n[Date]:             " + Timestamp.valueOf(logTmp.getString(ELogInvite.DATE.getName())).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "```", false);
            // @formatter:on
        }
        sendPrivateEmbedToBotOwner(embed);
    }

    @Override
    public void help(boolean embedState) {}

}