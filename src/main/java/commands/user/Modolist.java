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

public class Modolist implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public Modolist(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        Dao<PGuildMember> gMemberDao = DaoFactory.getGuildMemberDAO();
        Set<PGuildMember> moderators = ((GuildMemberDAO) gMemberDao).findMembersByPerm(event.getGuild().getId(), Permissions.MODERATOR);
        if (moderators.isEmpty()) {
            // @formatter:off
            ProxyUtils.sendMessage(event.getChannel(),
                    "No " + Permissions.MODERATOR.getName().toLowerCase() + ". "
                    + "To define a " + Permissions.MODERATOR.getName().toLowerCase()
                    + ", the guild owner has to use the command `" + guild.getPrefix() + Command.SETMODO.getName() + " @aMember`.");
            // @formatter:on
        } else {
            ProxyEmbed embed = new ProxyEmbed();
            embed.modoList(moderators);
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.MODOLIST.getName(),
                    "Display the bot moderators.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.MODOLIST.getName() + "`.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Display the bot moderators. **Example:** `" + guild.getPrefix() + Command.MODOLIST.getName() + "`.");
        }
    }

}