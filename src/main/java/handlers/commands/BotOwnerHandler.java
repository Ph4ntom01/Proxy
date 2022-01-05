package handlers.commands;

import commands.ACommand;
import commands.botowner.Invite;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class BotOwnerHandler extends AHandler {

    public BotOwnerHandler(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    @Override
    public void route() {

        if (command == ECommand.INVITE && args.length == 1) {

            ACommand inviteCmd = new Invite(event, command, pguild);
            inviteCmd.execute();
        }
    }

}