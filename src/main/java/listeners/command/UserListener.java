package listeners.command;

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
import configuration.constant.Command;
import configuration.constant.Folder;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.utility.ProxyUtils;

public class UserListener {

    private GuildMessageReceivedEvent event;
    private PGuild guild;
    private Command command;

    public UserListener(GuildMessageReceivedEvent event, PGuild guild, Command command) {
        this.event = event;
        this.guild = guild;
        this.command = command;
    }

    public void route() {

        String[] args = ProxyUtils.getArgs(event.getMessage());

        if (command == Command.INFO) {

            Info infoCmd = new Info(event, guild);
            infoCmd.execute();

        } else if (command == Command.PING) {
            Ping pingCmd = new Ping(event, guild);
            pingCmd.execute();

        } else if (command == Command.UPTIME) {
            Uptime uptimeCmd = new Uptime(event, guild);
            uptimeCmd.execute();

        } else if (command == Command.GUILDINFO) {
            GuildInfo guildInfoCmd = new GuildInfo(event, guild);
            guildInfoCmd.execute();

        } else if (command == Command.MEMBERINFO) {

            if (args.length == 2) {
                MemberInfo memberInfoCmd = new MemberInfo(event, guild);
                memberInfoCmd.execute();
            } else {
                MemberInfo memberInfoCmd = new MemberInfo(event, guild);
                memberInfoCmd.help(false);
            }

        } else if (command == Command.AVATAR) {

            if (args.length == 2) {
                Avatar avatarCmd = new Avatar(event, guild);
                avatarCmd.execute();
            } else {
                Avatar avatarCmd = new Avatar(event, guild);
                avatarCmd.help(false);
            }

        } else if (command == Command.TEXTCHANINFO) {

            if (args.length == 2) {
                TextChanInfo textChanCmd = new TextChanInfo(event, guild);
                textChanCmd.execute();
            } else {
                TextChanInfo textChanCmd = new TextChanInfo(event, guild);
                textChanCmd.help(false);
            }

        } else if (command == Command.CONTROLGATE) {

            ControlGate controlGateCmd = new ControlGate(event, guild);
            controlGateCmd.execute();

        } else if (command == Command.BANLIST) {

            if (args.length == 1) {
                Banlist banlistCmd = new Banlist(event, guild);
                banlistCmd.execute();
            } else if (args.length == 2) {
                Banlist banlistCmd = new Banlist(event, guild);
                banlistCmd.consultBannedMember();
            } else {
                Banlist banlistCmd = new Banlist(event, guild);
                banlistCmd.help(false);
            }

        } else if (command == Command.MODOLIST) {

            Modolist modolistCmd = new Modolist(event, guild);
            modolistCmd.execute();

        } else if (command == Command.ADMINLIST) {

            Adminlist adminlistCmd = new Adminlist(event, guild);
            adminlistCmd.execute();

        } else if (command == Command.ISSOU) {

            SendImage memeCmd = new SendImage(event, guild, Folder.RESOURCES.getName() + Folder.ISSOU.getName());
            memeCmd.execute();
        }
    }

}