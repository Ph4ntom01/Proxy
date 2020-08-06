package commands.user;

import commands.CommandManager;
import dao.pojo.GuildPojo;
import listeners.commands.UserListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class Info extends UserListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public Info(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        ProxyEmbed embed = new ProxyEmbed();
        embed.info(guild.getPrefix());
        ProxyUtils.sendEmbed(event.getChannel(), embed);
    }

    @Override
    public void help(boolean embedState) {
    }

}