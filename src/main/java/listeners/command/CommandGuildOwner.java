package listeners.command;

import commands.ACommand;
import commands.guildowner.SetPermission;
import configuration.constant.ECommand;
import configuration.constant.EPermission;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandGuildOwner extends ACommandListener {

    protected CommandGuildOwner(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        super(event, args, command, guild);
    }

    @Override
    public void route() {

        if (command == ECommand.SETADMIN) {

            ACommand permCmd = new SetPermission(event, args, command, guild, EPermission.ADMINISTRATOR);
            if (args.length == 2)
                permCmd.execute();
            else
                permCmd.help(false);
        }

        else if (command == ECommand.SETMODO) {

            ACommand permCmd = new SetPermission(event, args, command, guild, EPermission.MODERATOR);
            if (args.length == 2)
                permCmd.execute();
            else
                permCmd.help(false);
        }

        else if (command == ECommand.SETUSER) {

            ACommand permCmd = new SetPermission(event, args, command, guild, EPermission.USER);
            if (args.length == 2)
                permCmd.execute();
            else
                permCmd.help(false);
        }
    }

}