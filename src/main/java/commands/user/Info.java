package commands.user;

import commands.CommandManager;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyUtils;

public class Info implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public Info(GuildMessageReceivedEvent event, PGuild guild) {
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