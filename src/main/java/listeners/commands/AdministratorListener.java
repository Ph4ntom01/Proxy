package listeners.commands;

import commands.administrator.DefaultRole;
import commands.administrator.Disable;
import commands.administrator.JoinChannel;
import commands.administrator.JoinEmbed;
import commands.administrator.JoinMessage;
import commands.administrator.LeaveChannel;
import commands.administrator.LeaveEmbed;
import commands.administrator.LeaveMessage;
import commands.administrator.SetPermission;
import commands.administrator.SetPrefix;
import commands.administrator.Shield;
import configuration.cache.Commands;
import configuration.constants.Command;
import dao.pojo.GuildPojo;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyUtils;

public class AdministratorListener {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;
    private Command command;

    public AdministratorListener(GuildMessageReceivedEvent event, GuildPojo guild) {
        this.event = event;
        this.guild = guild;
    }

    public AdministratorListener(GuildMessageReceivedEvent event, GuildPojo guild, Command command) {
        this.event = event;
        this.guild = guild;
        this.command = command;
    }

    public void route() {

        String[] args = ProxyUtils.getArgs(event);

        if (command == Command.PREFIX) {

            if (args.length == 2) {
                SetPrefix prefixCmd = new SetPrefix(event, guild);
                prefixCmd.execute();
            } else {
                SetPrefix prefixCmd = new SetPrefix(event, guild);
                prefixCmd.help(false);
            }

        } else if (command == Command.SETUSER || command == Command.SETMODO || command == Command.SETADMIN) {

            if (args.length == 2) {
                SetPermission permCmd = new SetPermission(event, guild, ProxyUtils.getPermission(command.getName()));
                permCmd.execute();
            } else {
                SetPermission permCmd = new SetPermission(event, guild);
                permCmd.help(false);
            }

        } else if (command == Command.JOINCHAN) {

            if (args.length == 2) {
                JoinChannel joinChanCmd = new JoinChannel(event, guild);
                joinChanCmd.execute();
            } else {
                JoinChannel joinChanCmd = new JoinChannel(event, guild);
                joinChanCmd.help(false);
            }

        } else if (command == Command.JOINMESSAGE) {

            if (args.length >= 2) {
                JoinMessage joinMsgCmd = new JoinMessage(event, guild);
                joinMsgCmd.execute();
            } else {
                JoinMessage joinMsgCmd = new JoinMessage(event, guild);
                joinMsgCmd.help(false);
            }

        } else if (command == Command.JOINEMBED) {

            if (args.length == 2) {
                JoinEmbed joinEmbebCmd = new JoinEmbed(event, guild);
                joinEmbebCmd.execute();
            } else {
                JoinEmbed joinEmbebCmd = new JoinEmbed(event, guild);
                joinEmbebCmd.help(false);
            }

        } else if (command == Command.LEAVECHAN) {

            if (args.length == 2) {
                LeaveChannel leaveChanCmd = new LeaveChannel(event, guild);
                leaveChanCmd.execute();
            } else {
                LeaveChannel leaveChanCmd = new LeaveChannel(event, guild);
                leaveChanCmd.help(false);
            }

        } else if (command == Command.LEAVEMESSAGE) {

            if (args.length >= 2) {
                LeaveMessage leaveMsgCmd = new LeaveMessage(event, guild);
                leaveMsgCmd.execute();
            } else {
                LeaveMessage leaveMsgCmd = new LeaveMessage(event, guild);
                leaveMsgCmd.help(false);
            }

        } else if (command == Command.LEAVEEMBED) {

            if (args.length == 2) {
                LeaveEmbed leaveEmbebCmd = new LeaveEmbed(event, guild);
                leaveEmbebCmd.execute();
            } else {
                LeaveEmbed leaveEmbebCmd = new LeaveEmbed(event, guild);
                leaveEmbebCmd.help(false);
            }

        } else if (command == Command.DEFROLE) {

            if (args.length == 2) {
                DefaultRole defroleCmd = new DefaultRole(event, guild);
                defroleCmd.execute();
            } else {
                DefaultRole defroleCmd = new DefaultRole(event, guild);
                defroleCmd.help(false);
            }

        } else if (command == Command.SHIELD) {

            if (args.length == 2) {
                Shield shieldCmd = new Shield(event, guild);
                shieldCmd.execute();
            } else {
                Shield shieldCmd = new Shield(event, guild);
                shieldCmd.help(false);
            }

        } else if (command == Command.DISABLE) {

            if (args.length == 2) {
                Disable disableCmd = new Disable(event, guild, Commands.getInstance().get(args[1]));
                disableCmd.execute();
            } else {
                Disable disableCmd = new Disable(event, guild);
                disableCmd.execute();
            }
        }
    }

}