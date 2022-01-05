package handlers.commands;

import commands.ACommand;
import commands.administrator.ControlChannel;
import commands.administrator.DefaultRole;
import commands.administrator.Disable;
import commands.administrator.JoinChannel;
import commands.administrator.JoinEmbed;
import commands.administrator.JoinMessage;
import commands.administrator.LeaveChannel;
import commands.administrator.LeaveEmbed;
import commands.administrator.LeaveMessage;
import commands.administrator.Prefix;
import commands.administrator.Shield;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class AdministratorHandler extends AHandler {

    public AdministratorHandler(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    @Override
    public void route() {

        if (command == ECommand.PREFIX) {

            ACommand prefixCmd = new Prefix(event, args, command, pguild);
            executeCommand(prefixCmd, 2);

        } else if (command == ECommand.JOINCHAN) {

            ACommand joinChanCmd = new JoinChannel(event, args, command, pguild);
            executeCommand(joinChanCmd, 2);

        } else if (command == ECommand.JOINMESSAGE) {

            ACommand joinMsgCmd = new JoinMessage(event, args, command, pguild);
            executeCommand(joinMsgCmd, 2);

        } else if (command == ECommand.JOINEMBED) {

            ACommand joinEmbebCmd = new JoinEmbed(event, args, command, pguild);
            executeCommand(joinEmbebCmd, 2);

        } else if (command == ECommand.LEAVECHAN) {

            ACommand leaveChanCmd = new LeaveChannel(event, args, command, pguild);
            executeCommand(leaveChanCmd, 2);

        } else if (command == ECommand.LEAVEMESSAGE) {

            ACommand leaveMsgCmd = new LeaveMessage(event, args, command, pguild);
            executeCommand(leaveMsgCmd, 2);

        } else if (command == ECommand.LEAVEEMBED) {

            ACommand leaveEmbebCmd = new LeaveEmbed(event, args, command, pguild);
            executeCommand(leaveEmbebCmd, 2);

        } else if (command == ECommand.CONTROLCHAN) {

            ACommand controlChanCmd = new ControlChannel(event, args, command, pguild);
            executeCommand(controlChanCmd, 2);

        } else if (command == ECommand.DEFROLE) {

            ACommand defroleCmd = new DefaultRole(event, args, command, pguild);
            executeCommand(defroleCmd, 2);

        } else if (command == ECommand.SHIELD) {

            ACommand shieldCmd = new Shield(event, args, command, pguild);
            executeCommand(shieldCmd, 2);

        } else if (command == ECommand.DISABLE) {

            ACommand disableCmd = new Disable(event, args, command, pguild);
            executeCommand(disableCmd, 2);
        }
    }

}