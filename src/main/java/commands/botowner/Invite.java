package commands.botowner;

import java.awt.Color;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import commands.ACommand;
import configuration.constant.ECommand;
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
        Set<PLogInvite> logs = ((LogInviteDAO) logDao).findLogs(true);
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.setTitle(":bar_chart: Logs");
        embed.addField("", ":shinto_shrine: __**Invite**__", false);
        for (PLogInvite logTmp : logs) {
            DataObject json = DataObject.fromJson(logTmp.getGuildLog());
            // @formatter:off
            embed.addField("> _" + json.getString("name") + "_", "```ini"
                    + "\n[ID]:    " + json.getLong("id")
                    + "\n[Date]:  " + Timestamp.valueOf(json.getString("date")).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "```", false);
            // @formatter:on
        }
        logs = ((LogInviteDAO) logDao).findLogs(false);
        embed.addField("", ":kaaba: __**Leave**__", false);
        for (PLogInvite logTmp : logs) {
            DataObject json = DataObject.fromJson(logTmp.getGuildLog());
            // @formatter:off
            embed.addField("> _" + json.getString("name") + "_", "```ini"
                    + "\n[ID]:               " + json.getLong("id")
                    + "\n[Join Channel]:     " + json.getString("join_channel_id")
                    + "\n[Leave Channel]:    " + json.getString("leave_channel_id")
                    + "\n[Control Channel]:  " + json.getString("control_channel_id")
                    + "\n[Default Role]:     " + json.getString("default_role_id")
                    + "\n[Prefix]:           " + json.getString("prefix")
                    + "\n[Shield]:           " + json.getInt("shield") 
                    + "\n[Date]:             " + Timestamp.valueOf(json.getString("date")).toLocalDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "```", false);
            // @formatter:on
        }
        sendPrivateEmbedToBotOwner(embed);
    }

    @Override
    public void help(boolean embedState) {}

}