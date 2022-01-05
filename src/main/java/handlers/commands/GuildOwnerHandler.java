package handlers.commands;

import commands.ACommand;
import commands.guildowner.SetPermission;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class GuildOwnerHandler extends AHandler {

    public GuildOwnerHandler(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    @Override
    public void route() {

        if (command == ECommand.SETADMIN || command == ECommand.SETMODO || command == ECommand.SETUSER) {

            ACommand permCmd = new SetPermission(event, args, command, pguild);
            executeCommand(permCmd, 2);
        }
    }

}