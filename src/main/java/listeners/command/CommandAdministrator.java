package listeners.command;

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
import configuration.cache.ECommandCache;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandAdministrator extends ACommandListener {

    public CommandAdministrator(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild, PGuildMember member) {
        super(event, args, command, guild, member);
    }

    @Override
    public void route() {

        if (command == ECommand.PREFIX) {

            ACommand prefixCmd = new Prefix(event, args, command, guild);
            if (args.length == 2)
                prefixCmd.execute();
            else
                prefixCmd.help(false);

        } else if (command == ECommand.JOINCHAN) {

            ACommand joinChanCmd = new JoinChannel(event, args, command, guild);
            if (args.length == 2)
                joinChanCmd.execute();
            else
                joinChanCmd.help(false);

        } else if (command == ECommand.JOINMESSAGE) {

            ACommand joinMsgCmd = new JoinMessage(event, args, command, guild);
            if (args.length >= 2)
                joinMsgCmd.execute();
            else
                joinMsgCmd.help(false);

        } else if (command == ECommand.JOINEMBED) {

            ACommand joinEmbebCmd = new JoinEmbed(event, args, command, guild);
            if (args.length == 2)
                joinEmbebCmd.execute();
            else
                joinEmbebCmd.help(false);

        } else if (command == ECommand.LEAVECHAN) {

            ACommand leaveChanCmd = new LeaveChannel(event, args, command, guild);
            if (args.length == 2)
                leaveChanCmd.execute();
            else
                leaveChanCmd.help(false);

        } else if (command == ECommand.LEAVEMESSAGE) {

            ACommand leaveMsgCmd = new LeaveMessage(event, args, command, guild);
            if (args.length >= 2)
                leaveMsgCmd.execute();
            else
                leaveMsgCmd.help(false);

        } else if (command == ECommand.LEAVEEMBED) {

            ACommand leaveEmbebCmd = new LeaveEmbed(event, args, command, guild);
            if (args.length == 2)
                leaveEmbebCmd.execute();
            else
                leaveEmbebCmd.help(false);

        } else if (command == ECommand.CONTROLCHAN) {

            ACommand controlChanCmd = new ControlChannel(event, args, command, guild);
            if (args.length == 2)
                controlChanCmd.execute();
            else
                controlChanCmd.help(false);

        } else if (command == ECommand.DEFROLE) {

            ACommand defroleCmd = new DefaultRole(event, args, command, guild);
            if (args.length == 2)
                defroleCmd.execute();
            else
                defroleCmd.help(false);

        } else if (command == ECommand.SHIELD) {

            ACommand shieldCmd = new Shield(event, args, command, guild);
            if (args.length == 2)
                shieldCmd.execute();
            else
                shieldCmd.help(false);

        } else if (command == ECommand.DISABLE) {

            if (args.length == 2) {
                ACommand disableCmd = new Disable(event, command, guild, ECommandCache.INSTANCE.getCommand(args[1]));
                disableCmd.execute();
            } else {
                ACommand disableCmd = new Disable(event, command, guild);
                disableCmd.execute();
            }
        }
    }

}