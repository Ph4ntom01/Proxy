package commands.user;

import java.awt.Color;
import java.util.Set;

import commands.CommandManager;
import configuration.constant.Command;
import configuration.constant.Permissions;
import dao.database.Dao;
import dao.database.GuildMemberDAO;
import dao.pojo.PGuildMember;
import dao.pojo.PGuild;
import factory.DaoFactory;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyUtils;

public class Adminlist implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public Adminlist(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        Dao<PGuildMember> gMemberDao = DaoFactory.getGuildMemberDAO();
        Set<PGuildMember> administrators = ((GuildMemberDAO) gMemberDao).findMembersByPerm(event.getGuild().getId(), Permissions.ADMINISTRATOR);
        ProxyEmbed embed = new ProxyEmbed();
        embed.adminList(administrators);
        ProxyUtils.sendEmbed(event.getChannel(), embed);
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.ADMINLIST.getName(),
                    "Display the bot administrators.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.ADMINLIST.getName() + "`.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Display the bot administrators. **Example:** `" + guild.getPrefix() + Command.ADMINLIST.getName() + "`.");
        }
    }

}