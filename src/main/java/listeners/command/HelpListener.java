package listeners.command;

import java.awt.Color;

import commands.administrator.ControlChannel;
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
import commands.user.MemberInfo;
import commands.user.Modolist;
import commands.user.Ping;
import commands.user.SendImage;
import commands.user.TextChanInfo;
import commands.user.Uptime;
import configuration.constant.Category;
import configuration.constant.Command;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.utility.ProxyCache;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyUtils;

public class HelpListener {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public HelpListener(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    public void route() {

        String[] args = ProxyUtils.getArgs(event.getMessage());

        if (args.length == 1) {
            ProxyEmbed embed = new ProxyEmbed();
            embed.helpList();
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            Command command = ProxyCache.getCommands().get(args[1]);

            if (args[1].equalsIgnoreCase(Category.ADMINISTRATION.getName())) {
                ProxyEmbed embed = new ProxyEmbed();
                // @formatter:off
                embed.help("__" + Command.HELP.getName() + " " + Category.ADMINISTRATION.getName() + "__",
                        ProxyUtils.getCommands(Category.ADMINISTRATION)
                        + "\n\nExample: `" + guild.getPrefix() + Command.HELP.getName() + " " + Command.SHIELD.getName() + "`",
                        Color.GREEN);
                // @formatter:on
                ProxyUtils.sendEmbed(event.getChannel(), embed);
            }

            else if (args[1].equalsIgnoreCase(Category.MODERATION.getName())) {
                ProxyEmbed embed = new ProxyEmbed();
                // @formatter:off
                embed.help("__" + Command.HELP.getName() + " " + Category.MODERATION.getName() + "__",
                        ProxyUtils.getCommands(Category.MODERATION)
                        + "\n\nExample: `" + guild.getPrefix() + Command.HELP.getName() + " " + Command.PURGE.getName() + "`",
                        Color.GREEN);
                // @formatter:on
                ProxyUtils.sendEmbed(event.getChannel(), embed);
            }

            else if (args[1].equalsIgnoreCase(Category.UTILITY.getName())) {
                ProxyEmbed embed = new ProxyEmbed();
                // @formatter:off
                embed.help("__" + Command.HELP.getName() + " " + Category.UTILITY.getName() + "__",
                        ProxyUtils.getCommands(Category.UTILITY)
                        + "\n\nExample: `" + guild.getPrefix() + Command.HELP.getName() + " " + Command.GUILDINFO.getName() + "`",
                        Color.GREEN);
                // @formatter:on
                ProxyUtils.sendEmbed(event.getChannel(), embed);
            }

            else if (args[1].equalsIgnoreCase(Category.MEME.getName())) {
                ProxyEmbed embed = new ProxyEmbed();
                // @formatter:off
                embed.help("__" + Command.HELP.getName() + " " + Category.MEME.getName() + "__",
                        ProxyUtils.getCommands(Category.MEME)
                        + "\n\nExample: `" + guild.getPrefix() + Command.HELP.getName() + " " + Command.ISSOU.getName() + "`", Color.GREEN);
                // @formatter:on
                ProxyUtils.sendEmbed(event.getChannel(), embed);
            }

            else if (command == Command.PREFIX) {
                SetPrefix prefixCmd = new SetPrefix(event, guild);
                prefixCmd.help(true);
            }

            else if (command == Command.SETADMIN) {
                SetPermission setAdminCmd = new SetPermission(event, guild);
                setAdminCmd.help(true);
            }

            else if (command == Command.SETMODO) {
                SetPermission setModoCmd = new SetPermission(event, guild);
                setModoCmd.help(true);
            }

            else if (command == Command.SETUSER) {
                SetPermission setUserCmd = new SetPermission(event, guild);
                setUserCmd.help(true);
            }

            else if (command == Command.JOINCHAN) {
                JoinChannel joinChanCmd = new JoinChannel(event, guild);
                joinChanCmd.help(true);
            }

            else if (command == Command.JOINMESSAGE) {
                JoinMessage joinMsgCmd = new JoinMessage(event, guild);
                joinMsgCmd.help(true);
            }

            else if (command == Command.JOINEMBED) {
                JoinEmbed joinEmbedCmd = new JoinEmbed(event, guild);
                joinEmbedCmd.help(true);
            }

            else if (command == Command.LEAVECHAN) {
                LeaveChannel leaveChanCmd = new LeaveChannel(event, guild);
                leaveChanCmd.help(true);
            }

            else if (command == Command.LEAVEMESSAGE) {
                LeaveMessage leaveMsgCmd = new LeaveMessage(event, guild);
                leaveMsgCmd.help(true);
            }

            else if (command == Command.LEAVEEMBED) {
                LeaveEmbed leaveEmbedCmd = new LeaveEmbed(event, guild);
                leaveEmbedCmd.help(true);
            }

            else if (command == Command.CONTROLCHAN) {
                ControlChannel controlChanCmd = new ControlChannel(event, guild);
                controlChanCmd.help(true);
            }

            else if (command == Command.DEFROLE) {
                DefaultRole defRoleCmd = new DefaultRole(event, guild);
                defRoleCmd.help(true);
            }

            else if (command == Command.SHIELD) {
                Shield shieldCmd = new Shield(event, guild);
                shieldCmd.help(true);
            }

            else if (command == Command.DISABLE) {
                Disable disableCmd = new Disable(event, guild);
                disableCmd.help(true);
            }

            else if (command == Command.CLEAN) {
                Clean cleanCmd = new Clean(event, guild);
                cleanCmd.help(true);
            }

            else if (command == Command.SLOWMODE) {
                Slowmode slowmodeCmd = new Slowmode(event, guild);
                slowmodeCmd.help(true);
            }

            else if (command == Command.VOICEKICK) {
                VoiceKick voiceKickCmd = new VoiceKick(event, guild);
                voiceKickCmd.help(true);
            }

            else if (command == Command.VOICEMUTE) {
                VoiceMute voiceMuteCmd = new VoiceMute(event, guild);
                voiceMuteCmd.help(true);
            }

            else if (command == Command.VOICEUNMUTE) {
                VoiceUnmute voiceUnmuteCmd = new VoiceUnmute(event, guild);
                voiceUnmuteCmd.help(true);
            }

            else if (command == Command.KICK) {
                Kick kickCmd = new Kick(event, guild);
                kickCmd.help(true);
            }

            else if (command == Command.BAN) {
                Bans banCmd = new Bans(event, guild);
                banCmd.help(true);
            }

            else if (command == Command.SOFTBAN) {
                Softban softbanCmd = new Softban(event, guild);
                softbanCmd.help(true);
            }

            else if (command == Command.UNBAN) {
                Unban unbanCmd = new Unban(event, guild);
                unbanCmd.help(true);
            }

            else if (command == Command.PURGE) {
                Purge purgeCmd = new Purge(event, guild);
                purgeCmd.help(true);
            }

            else if (command == Command.RESETCHAN) {
                ResetChan resetChanCmd = new ResetChan(event, guild);
                resetChanCmd.help(true);
            }

            else if (command == Command.PING) {
                Ping pingCmd = new Ping(event, guild);
                pingCmd.help(true);
            }

            else if (command == Command.UPTIME) {
                Uptime uptimeCmd = new Uptime(event, guild);
                uptimeCmd.help(true);
            }

            else if (command == Command.GUILDINFO) {
                GuildInfo guildInfoCmd = new GuildInfo(event, guild);
                guildInfoCmd.help(true);
            }

            else if (command == Command.MEMBERINFO) {
                MemberInfo memberInfoCmd = new MemberInfo(event, guild);
                memberInfoCmd.help(true);
            }

            else if (command == Command.AVATAR) {
                Avatar avatarCmd = new Avatar(event, guild);
                avatarCmd.help(true);
            }

            else if (command == Command.TEXTCHANINFO) {
                TextChanInfo textChanInfoCmd = new TextChanInfo(event, guild);
                textChanInfoCmd.help(true);
            }

            else if (command == Command.CONTROLGATE) {
                ControlGate controlGateCmd = new ControlGate(event, guild);
                controlGateCmd.help(true);
            }

            else if (command == Command.BANLIST) {
                Banlist banlistCmd = new Banlist(event, guild);
                banlistCmd.help(true);
            }

            else if (command == Command.MODOLIST) {
                Modolist modolistCmd = new Modolist(event, guild);
                modolistCmd.help(true);
            }

            else if (command == Command.ADMINLIST) {
                Adminlist adminlistCmd = new Adminlist(event, guild);
                adminlistCmd.help(true);
            }

            else if (command == Command.ISSOU) {
                SendImage memeCmd = new SendImage(event, guild);
                memeCmd.help(true);
            }
        }
    }

}