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
import commands.guildowner.SetPermission;
import commands.moderator.Bans;
import commands.moderator.Clean;
import commands.moderator.Kick;
import commands.moderator.Purge;
import commands.moderator.ResetChan;
import commands.moderator.Slowmode;
import commands.moderator.Softban;
import commands.moderator.Unban;
import commands.moderator.VoiceKick;
import commands.moderator.VoiceMute;
import commands.moderator.VoiceUnmute;
import commands.user.Adminlist;
import commands.user.Avatar;
import commands.user.Banlist;
import commands.user.ControlGate;
import commands.user.GuildInfo;
import commands.user.Help;
import commands.user.MemberInfo;
import commands.user.Modolist;
import commands.user.Ping;
import commands.user.SendImage;
import commands.user.TextChanInfo;
import commands.user.Uptime;
import configuration.cache.ECommandCache;
import configuration.constant.ECategory;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandHelp extends ACommandListener {

    public CommandHelp(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        super(event, args, command, guild);
    }

    @Override
    public void route() {

        Help helpCmd = new Help(event, command, guild);

        if (args.length == 1) {
            helpCmd.categories();
        } else {

            ECommand command = ECommandCache.INSTANCE.getCommand(args[1]);

            if (args[1].equalsIgnoreCase(ECategory.ADMINISTRATION.getName())) {
                helpCmd.administration();
            }

            else if (args[1].equalsIgnoreCase(ECategory.MODERATION.getName())) {
                helpCmd.moderation();
            }

            else if (args[1].equalsIgnoreCase(ECategory.UTILITY.getName())) {
                helpCmd.utility();
            }

            else if (args[1].equalsIgnoreCase(ECategory.MEME.getName())) {
                helpCmd.meme();
            }

            else if (command == ECommand.PREFIX) {
                ACommand prefixCmd = new Prefix(event, command, guild);
                prefixCmd.help(true);
            }

            else if (command == ECommand.SETADMIN) {
                ACommand setAdminCmd = new SetPermission(event, command, guild);
                setAdminCmd.help(true);
            }

            else if (command == ECommand.SETMODO) {
                ACommand setModoCmd = new SetPermission(event, command, guild);
                setModoCmd.help(true);
            }

            else if (command == ECommand.SETUSER) {
                ACommand setUserCmd = new SetPermission(event, command, guild);
                setUserCmd.help(true);
            }

            else if (command == ECommand.JOINCHAN) {
                ACommand joinChanCmd = new JoinChannel(event, command, guild);
                joinChanCmd.help(true);
            }

            else if (command == ECommand.JOINMESSAGE) {
                ACommand joinMsgCmd = new JoinMessage(event, command, guild);
                joinMsgCmd.help(true);
            }

            else if (command == ECommand.JOINEMBED) {
                ACommand joinEmbedCmd = new JoinEmbed(event, command, guild);
                joinEmbedCmd.help(true);
            }

            else if (command == ECommand.LEAVECHAN) {
                ACommand leaveChanCmd = new LeaveChannel(event, command, guild);
                leaveChanCmd.help(true);
            }

            else if (command == ECommand.LEAVEMESSAGE) {
                ACommand leaveMsgCmd = new LeaveMessage(event, command, guild);
                leaveMsgCmd.help(true);
            }

            else if (command == ECommand.LEAVEEMBED) {
                ACommand leaveEmbedCmd = new LeaveEmbed(event, command, guild);
                leaveEmbedCmd.help(true);
            }

            else if (command == ECommand.CONTROLCHAN) {
                ACommand controlChanCmd = new ControlChannel(event, command, guild);
                controlChanCmd.help(true);
            }

            else if (command == ECommand.DEFROLE) {
                ACommand defRoleCmd = new DefaultRole(event, command, guild);
                defRoleCmd.help(true);
            }

            else if (command == ECommand.SHIELD) {
                ACommand shieldCmd = new Shield(event, command, guild);
                shieldCmd.help(true);
            }

            else if (command == ECommand.DISABLE) {
                ACommand disableCmd = new Disable(event, command, guild);
                disableCmd.help(true);
            }

            else if (command == ECommand.CLEAN) {
                ACommand cleanCmd = new Clean(event, command, guild);
                cleanCmd.help(true);
            }

            else if (command == ECommand.SLOWMODE) {
                ACommand slowmodeCmd = new Slowmode(event, command, guild);
                slowmodeCmd.help(true);
            }

            else if (command == ECommand.VOICEKICK) {
                ACommand voiceKickCmd = new VoiceKick(event, command, guild);
                voiceKickCmd.help(true);
            }

            else if (command == ECommand.VOICEMUTE) {
                ACommand voiceMuteCmd = new VoiceMute(event, command, guild);
                voiceMuteCmd.help(true);
            }

            else if (command == ECommand.VOICEUNMUTE) {
                ACommand voiceUnmuteCmd = new VoiceUnmute(event, command, guild);
                voiceUnmuteCmd.help(true);
            }

            else if (command == ECommand.KICK) {
                ACommand kickCmd = new Kick(event, command, guild);
                kickCmd.help(true);
            }

            else if (command == ECommand.BAN) {
                ACommand banCmd = new Bans(event, command, guild);
                banCmd.help(true);
            }

            else if (command == ECommand.SOFTBAN) {
                ACommand softbanCmd = new Softban(event, command, guild);
                softbanCmd.help(true);
            }

            else if (command == ECommand.UNBAN) {
                ACommand unbanCmd = new Unban(event, command, guild);
                unbanCmd.help(true);
            }

            else if (command == ECommand.PURGE) {
                ACommand purgeCmd = new Purge(event, command, guild);
                purgeCmd.help(true);
            }

            else if (command == ECommand.RESETCHAN) {
                ACommand resetChanCmd = new ResetChan(event, command, guild);
                resetChanCmd.help(true);
            }

            else if (command == ECommand.PING) {
                ACommand pingCmd = new Ping(event, command, guild);
                pingCmd.help(true);
            }

            else if (command == ECommand.UPTIME) {
                ACommand uptimeCmd = new Uptime(event, command, guild);
                uptimeCmd.help(true);
            }

            else if (command == ECommand.GUILDINFO) {
                ACommand guildInfoCmd = new GuildInfo(event, command, guild);
                guildInfoCmd.help(true);
            }

            else if (command == ECommand.MEMBERINFO) {
                ACommand memberInfoCmd = new MemberInfo(event, command, guild);
                memberInfoCmd.help(true);
            }

            else if (command == ECommand.AVATAR) {
                ACommand avatarCmd = new Avatar(event, command, guild);
                avatarCmd.help(true);
            }

            else if (command == ECommand.TEXTCHANINFO) {
                ACommand textChanInfoCmd = new TextChanInfo(event, command, guild);
                textChanInfoCmd.help(true);
            }

            else if (command == ECommand.CONTROLGATE) {
                ACommand controlGateCmd = new ControlGate(event, command, guild);
                controlGateCmd.help(true);
            }

            else if (command == ECommand.BANLIST) {
                ACommand banlistCmd = new Banlist(event, command, guild);
                banlistCmd.help(true);
            }

            else if (command == ECommand.MODOLIST) {
                ACommand modolistCmd = new Modolist(event, command, guild);
                modolistCmd.help(true);
            }

            else if (command == ECommand.ADMINLIST) {
                ACommand adminlistCmd = new Adminlist(event, command, guild);
                adminlistCmd.help(true);
            }

            else if (command == ECommand.ISSOU) {
                ACommand memeCmd = new SendImage(event, command, guild);
                memeCmd.help(true);
            }
        }
    }

}