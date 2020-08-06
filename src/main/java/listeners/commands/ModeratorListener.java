package listeners.commands;

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
import configuration.constants.Command;
import dao.pojo.GuildPojo;
import dao.pojo.MemberPojo;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.ProxyUtils;

public class ModeratorListener {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;
    private MemberPojo author;
    private Command command;

    public ModeratorListener(GuildMessageReceivedEvent event, GuildPojo guild) {
        this.event = event;
        this.guild = guild;
    }

    public ModeratorListener(GuildMessageReceivedEvent event, GuildPojo guild, MemberPojo author) {
        this.event = event;
        this.guild = guild;
        this.author = author;
    }

    public ModeratorListener(GuildMessageReceivedEvent event, GuildPojo guild, MemberPojo author, Command command) {
        this.event = event;
        this.guild = guild;
        this.author = author;
        this.command = command;
    }

    public void route() {

        String[] args = ProxyUtils.getArgs(event.getMessage());

        if (command == Command.CLEAN) {

            if (args.length == 1) {
                Clean cleanCmd = new Clean(event, guild);
                cleanCmd.deleteMessages(10);
            } else {
                Clean cleanCmd = new Clean(event, guild);
                cleanCmd.execute();
            }

        } else if (command == Command.SLOWMODE) {

            if (args.length == 1) {
                Slowmode slowmodeCmd = new Slowmode(event, guild);
                slowmodeCmd.help(false);
            } else {
                Slowmode slowmodeCmd = new Slowmode(event, guild);
                slowmodeCmd.execute();
            }

        } else if (command == Command.VOICEKICK) {

            if (args.length == 2) {
                VoiceKick voicekickCmd = new VoiceKick(event, guild);
                voicekickCmd.execute();
            } else {
                VoiceKick voicekickCmd = new VoiceKick(event, guild);
                voicekickCmd.help(false);
            }

        } else if (command == Command.VOICEMUTE) {

            if (args.length == 2) {
                VoiceMute voicemuteCmd = new VoiceMute(event, guild);
                voicemuteCmd.execute();
            } else {
                VoiceMute voicemuteCmd = new VoiceMute(event, guild);
                voicemuteCmd.help(false);
            }

        } else if (command == Command.VOICEUNMUTE) {

            if (args.length == 2) {
                VoiceUnmute voiceUnmuteCmd = new VoiceUnmute(event, guild);
                voiceUnmuteCmd.execute();
            } else {
                VoiceUnmute voiceUnmuteCmd = new VoiceUnmute(event, guild);
                voiceUnmuteCmd.help(false);
            }

        } else if (command == Command.KICK) {

            if (args.length == 2) {
                Kick kickCmd = new Kick(event, guild, author);
                kickCmd.execute();
            } else {
                Kick kickCmd = new Kick(event, guild, author);
                kickCmd.help(false);
            }

        } else if (command == Command.BAN) {

            if (args.length == 2) {
                Bans banCmd = new Bans(event, guild, author);
                banCmd.execute();
            } else {
                Bans banCmd = new Bans(event, guild, author);
                banCmd.help(false);
            }

        } else if (command == Command.SOFTBAN) {

            if (args.length == 2) {
                Softban softbanCmd = new Softban(event, guild, author);
                softbanCmd.execute();
            } else {
                Softban softbanCmd = new Softban(event, guild, author);
                softbanCmd.help(false);
            }

        } else if (command == Command.UNBAN) {

            if (args.length == 2) {
                Unban unbanCmd = new Unban(event, guild);
                unbanCmd.execute();
            } else {
                Unban unbanCmd = new Unban(event, guild);
                unbanCmd.help(false);
            }

        } else if (command == Command.PURGE) {

            if (args.length == 1) {
                Purge purgeCmd = new Purge(event, guild);
                purgeCmd.help(false);
            } else if (args.length == 2) {
                Purge purgeCmd = new Purge(event, guild);
                purgeCmd.execute();
            } else if (args.length == 3) {
                Purge purgeCmd = new Purge(event, guild);
                purgeCmd.purgeByRole();
            }

        } else if (command == Command.RESETCHAN) {
            ResetChan resetChanCmd = new ResetChan(event, guild);
            resetChanCmd.execute();
        }
    }

}