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
            Role role = event.getGuild().getRoleById(ProxyUtils.getMentionnedEntity(MentionType.ROLE, event.getMessage(), ProxyUtils.getArgs(event)[1]));
            if (role.getId().equals(guild.getDefaultRole())) {
                ProxyUtils.sendMessage(event, "Default role **" + role.getName() + "** has already been defined.");
            } else {
                Dao<GuildPojo> guildDao = DaoFactory.getGuildDAO();
                guild.setDefaultRole(role.getId());
                guildDao.update(guild);
                ProxyUtils.sendMessage(event, "Default role is now **" + role.getName() + "**.");
            }
        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event, "**" + ProxyUtils.getArgs(event)[1] + "** is not a role.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.DEFROLE.getName(), 
                    "Set the default role when a member join the server.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.DEFROLE.getName() + " @aRole`",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event, embed);
        } else {
            ProxyUtils.sendMessage(event, "Set the default role when a member join the server. **Example:** `" + guild.getPrefix() + Command.DEFROLE.getName() + " @aRole`.");
        }
    }

}