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

public class Adminlist extends UserListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public Adminlist(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
        Set<MemberPojo> members = ((GuildDAO) guildDao).findMembers(event.getGuild().getId());
        List<MemberPojo> adminlist = members.stream().filter(member -> member.getPermLevel() == Permissions.ADMINISTRATOR.getLevel()).collect(Collectors.toList());
        ProxyEmbed embed = new ProxyEmbed();
        embed.adminList(adminlist);
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