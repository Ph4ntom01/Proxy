package handlers.commands;

import commands.ACommand;
import commands.user.Adminlist;
import commands.user.Avatar;
import commands.user.Banlist;
import commands.user.ControlGate;
import commands.user.GuildInfo;
import commands.user.Info;
import commands.user.MemberInfo;
import commands.user.Modolist;
import commands.user.Ping;
import commands.user.SendImage;
import commands.user.TextChanInfo;
import commands.user.Uptime;
import configuration.constant.ECommand;
import configuration.constant.EFolder;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class UserHandler extends AHandler {

    public UserHandler(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    @Override
    public void route() {

        if (command == ECommand.INFO) {

            ACommand infoCmd = new Info(event, command, pguild);
            infoCmd.execute();

        } else if (command == ECommand.PING) {

            ACommand pingCmd = new Ping(event, command, pguild);
            pingCmd.execute();

        } else if (command == ECommand.UPTIME) {

            ACommand uptimeCmd = new Uptime(event, command, pguild);
            uptimeCmd.execute();

        } else if (command == ECommand.GUILDINFO) {

            ACommand guildInfoCmd = new GuildInfo(event, command, pguild);
            guildInfoCmd.execute();

        } else if (command == ECommand.MEMBERINFO) {

            ACommand memberInfoCmd = new MemberInfo(event, args, command, pguild);
            executeCommand(memberInfoCmd, 2);

        } else if (command == ECommand.AVATAR) {

            ACommand avatarCmd = new Avatar(event, args, command, pguild);
            executeCommand(avatarCmd, 2);

        } else if (command == ECommand.TEXTCHANINFO) {

            ACommand textChanCmd = new TextChanInfo(event, args, command, pguild);
            executeCommand(textChanCmd, 2);

        } else if (command == ECommand.CONTROLGATE) {

            ACommand controlGateCmd = new ControlGate(event, command, pguild);
            controlGateCmd.execute();

        } else if (command == ECommand.BANLIST) {

            ACommand banlistCmd = new Banlist(event, args, command, pguild);
            if (args.length == 1)
                banlistCmd.execute();
            else if (args.length == 2)
                ((Banlist) banlistCmd).consultBannedMember();
            else
                banlistCmd.help(false);

        } else if (command == ECommand.MODOLIST) {

            ACommand modolistCmd = new Modolist(event, command, pguild);
            modolistCmd.execute();

        } else if (command == ECommand.ADMINLIST) {

            ACommand adminlistCmd = new Adminlist(event, command, pguild);
            adminlistCmd.execute();

        } else if (command == ECommand.ISSOU) {

            ACommand memeCmd = new SendImage(event, command, pguild, EFolder.RESOURCES.getName() + EFolder.ISSOU.getName());
            memeCmd.execute();
        }
    }

}