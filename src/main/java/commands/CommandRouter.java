package commands;

import configuration.constant.Command;
import configuration.constant.Permissions;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import listeners.command.AdministratorListener;
import listeners.command.HelpListener;
import listeners.command.ModeratorListener;
import listeners.command.UserListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.utility.ProxyCache;
import proxy.utility.ProxyUtils;

public class CommandRouter extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        if (!ProxyCache.getGuildBlacklist(event.getGuild().getId()) && !ProxyCache.getMemberBlacklist(event.getAuthor().getId()) && !event.getAuthor().isBot()) {

            String[] args = ProxyUtils.getArgs(event.getMessage());
            PGuild guild = ProxyCache.getGuild(event.getGuild());
            StringBuilder firstArg = new StringBuilder(args[0]);

            if (firstArg.toString().regionMatches(0, guild.getPrefix(), 0, guild.getPrefix().length())) {
                firstArg.replace(0, guild.getPrefix().length(), "");
                String userCommand = firstArg.toString();

                if (ProxyCache.getCommands().containsKey(userCommand)) {
                    Command command = ProxyCache.getCommands().get(userCommand);
                    Permissions permission = command.getPermission();
                    PGuildMember author = ProxyCache.getGuildMember(event.getMember());

                    if (author.getPermId() >= permission.getLevel()) {

                        if (permission == Permissions.ADMINISTRATOR) {
                            AdministratorListener adminCmd = new AdministratorListener(event, guild, command);
                            adminCmd.route();
                        } else if (permission == Permissions.MODERATOR) {
                            ModeratorListener modoCmd = new ModeratorListener(event, guild, author, command);
                            modoCmd.route();
                        } else {
                            if (command == Command.HELP) {
                                HelpListener helpCmd = new HelpListener(event, guild);
                                helpCmd.route();
                            } else {
                                UserListener userCmd = new UserListener(event, guild, command);
                                userCmd.route();
                            }
                        }
                    } else {
                        // @formatter:off
                            ProxyUtils.sendMessage(event.getChannel(),
                                    "You need to be **" + permission.getName().toLowerCase() + "**. "
                                    + "Only the guild owner has the ability to set a permission.");
                        // @formatter:on
                    }
                }
            }
            if (event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser()) && args.length == 1) {
                ProxyUtils.selfbotEmbed(event.getJDA(), event.getGuild(), guild, event.getChannel());
            }
        }
    }

}