package commands;

import configuration.cache.Blacklist;
import configuration.cache.Commands;
import configuration.constants.Command;
import configuration.constants.Permissions;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import listeners.commands.AdministratorListener;
import listeners.commands.HelpListener;
import listeners.commands.ModeratorListener;
import listeners.commands.UserListener;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.ProxyUtils;

public class CommandRouter extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        if (!(boolean) Blacklist.getInstance().getUnchecked(event.getGuild().getId()) && !(boolean) Blacklist.getInstance().getUnchecked(event.getAuthor().getId())
                && !event.getAuthor().isBot()) {

            String[] args = ProxyUtils.getArgs(event);
            GuildPojo guild = ProxyUtils.getGuildFromCache(event.getGuild());
            StringBuilder firstArg = new StringBuilder(args[0]);

            if (firstArg.toString().regionMatches(0, guild.getPrefix(), 0, guild.getPrefix().length())) {
                firstArg.replace(0, guild.getPrefix().length(), "");
                String userCommand = firstArg.toString();

                if (Commands.getInstance().containsKey(userCommand)) {
                    Command command = Commands.getInstance().get(userCommand);
                    Permissions permission = command.getPermission();
                    MemberPojo author = ProxyUtils.getMemberFromCache(event.getMember());

                    if (author.getPermLevel() >= permission.getLevel()) {

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
                        ProxyUtils.sendMessage(event,
                                "You need to be **" + permission.getName().toLowerCase() + "**. "
                                + "Only the guild owner has the ability to set a permission.");
                        // @formatter:on
                    }
                }
            }
            if (event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser()) && args.length == 1) {
                ProxyUtils.selfbotEmbed(event, guild);
            }
        }
    }

}