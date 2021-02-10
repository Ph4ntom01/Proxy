package listeners.command;

import commands.ACommand;
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
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandModerator extends ACommandListener {

    public CommandModerator(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild, PGuildMember member) {
        super(event, args, command, guild, member);
    }

    @Override
    public void route() {

        if (command == ECommand.CLEAN) {

            ACommand cleanCmd = new Clean(event, args, command, guild);
            if (args.length == 1)
                ((Clean) cleanCmd).deleteMessages(10);
            else
                cleanCmd.execute();

        } else if (command == ECommand.SLOWMODE) {

            ACommand slowmodeCmd = new Slowmode(event, args, command, guild);
            if (args.length == 1)
                slowmodeCmd.help(false);
            else
                slowmodeCmd.execute();

        } else if (command == ECommand.VOICEKICK) {

            ACommand voicekickCmd = new VoiceKick(event, args, command, guild);
            if (args.length == 2)
                voicekickCmd.execute();
            else
                voicekickCmd.help(false);

        } else if (command == ECommand.VOICEMUTE) {

            ACommand voicemuteCmd = new VoiceMute(event, args, command, guild);
            if (args.length == 2)
                voicemuteCmd.execute();
            else
                voicemuteCmd.help(false);

        } else if (command == ECommand.VOICEUNMUTE) {

            ACommand voiceUnmuteCmd = new VoiceUnmute(event, args, command, guild);
            if (args.length == 2)
                voiceUnmuteCmd.execute();
            else
                voiceUnmuteCmd.help(false);

        } else if (command == ECommand.KICK) {

            ACommand kickCmd = new Kick(event, args, command, guild, member);
            if (args.length == 2)
                kickCmd.execute();
            else
                kickCmd.help(false);

        } else if (command == ECommand.BAN) {

            ACommand banCmd = new Bans(event, args, command, guild, member);
            if (args.length == 2)
                banCmd.execute();
            else
                banCmd.help(false);

        } else if (command == ECommand.SOFTBAN) {

            ACommand softbanCmd = new Softban(event, args, command, guild, member);
            if (args.length == 2)
                softbanCmd.execute();
            else
                softbanCmd.help(false);

        } else if (command == ECommand.UNBAN) {

            ACommand unbanCmd = new Unban(event, args, command, guild);
            if (args.length == 2)
                unbanCmd.execute();
            else
                unbanCmd.help(false);

        } else if (command == ECommand.PURGE) {

            ACommand purgeCmd = new Purge(event, args, command, guild);
            if (args.length == 1)
                purgeCmd.help(false);
            else if (args.length == 2)
                purgeCmd.execute();
            else if (args.length == 3)
                ((Purge) purgeCmd).purgeByRole();

        } else if (command == ECommand.RESETCHAN) {

            ACommand resetChanCmd = new ResetChan(event, args, command, guild);
            resetChanCmd.execute();
        }
    }

}