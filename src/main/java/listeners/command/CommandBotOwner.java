package listeners.command;

import commands.ACommand;
import commands.botowner.Invite;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandBotOwner extends ACommandListener {

    protected CommandBotOwner(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        super(event, args, command, guild);
    }

    @Override
    public void route() {

        if (command == ECommand.INVITE && args.length == 1) {

            ACommand inviteCmd = new Invite(event, command, guild);
            inviteCmd.execute();
        }
    }

}