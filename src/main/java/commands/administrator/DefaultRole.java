package commands.administrator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.database.Dao;
import dao.pojo.GuildPojo;
import factory.DaoFactory;
import listeners.commands.AdministratorListener;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class DefaultRole extends AdministratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public DefaultRole(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            Role role = event.getGuild().getRoleById(ProxyUtils.getMentionnedEntity(MentionType.ROLE, event.getMessage(), ProxyUtils.getArgs(event.getMessage())[1]));
            if (role.getId().equals(guild.getDefaultRole())) {
                ProxyUtils.sendMessage(event.getChannel(), "Default role **" + role.getName() + "** has already been defined.");
            } else {
                Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                guild.setDefaultRole(role.getId());
                guildDao.update(guild);
                ProxyUtils.sendMessage(event.getChannel(), "Default role is now **" + role.getName() + "**.");
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event.getChannel(), "**" + ProxyUtils.getArgs(event.getMessage())[1] + "** is not a role.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.DEFROLE.getName(), 
                    "Automatically assign a role when a member joins the server.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.DEFROLE.getName() + " @aRole`\n\n"
                    + "*You can also mention a role by his ID*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            ProxyUtils.sendMessage(event.getChannel(), "Automatically assign a role when a member joins the server. **Example:** `" + guild.getPrefix() + Command.DEFROLE.getName() + " @aRole`.");
        }
    }

}