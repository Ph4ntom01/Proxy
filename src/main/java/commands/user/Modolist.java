package commands.user;

import java.awt.Color;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import commands.CommandManager;
import configuration.constants.Command;
import configuration.constants.Permissions;
import dao.database.Dao;
import dao.database.GuildDAO;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import factory.DaoFactory;
import listeners.commands.UserListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class Modolist extends UserListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public Modolist(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
        Set<MemberPojo> members = ((GuildDAO) guildDao).findMembers(event.getGuild().getId());
        List<MemberPojo> modolist = members.stream().filter(member -> member.getPermLevel() == Permissions.MODERATOR.getLevel()).collect(Collectors.toList());
        if (modolist.isEmpty()) {
            // @formatter:off
            ProxyUtils.sendMessage(event,
                    "No " + Permissions.MODERATOR.getName().toLowerCase() + ". "
                    + "To define a " + Permissions.MODERATOR.getName().toLowerCase()
                    + ", the guild owner has to use the command `" + guild.getPrefix() + Command.SETMODO.getName() + " @aMember`.");
            // @formatter:on
        } else {
            ProxyEmbed embed = new ProxyEmbed();
            embed.modoList(modolist);
            ProxyUtils.sendEmbed(event, embed);
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
            ProxyUtils.sendEmbed(event, embed);
        } else {
            ProxyUtils.sendMessage(event, "Display the bot moderators. **Example:** `" + guild.getPrefix() + Command.MODOLIST.getName() + "`.");
        }
    }

}