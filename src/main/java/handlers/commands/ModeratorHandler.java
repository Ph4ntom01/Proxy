package handlers.commands;

import commands.ACommand;
import commands.moderator.Bans;
import commands.moderator.Clean;
import commands.moderator.Kick;
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

public class ModeratorHandler extends AHandler {

    public ModeratorHandler(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild, PGuildMember pguildMember) {
        super(event, args, command, pguild, pguildMember);
    }

    @Override
    public void route() {

        if (command == ECommand.CLEAN) {

            ACommand cleanCmd = new Clean(event, args, command, pguild);
            cleanCmd.execute();

        } else if (command == ECommand.SLOWMODE) {

            ACommand slowmodeCmd = new Slowmode(event, args, command, pguild);
            executeCommand(slowmodeCmd, 2);

        } else if (command == ECommand.VOICEKICK) {

            ACommand voicekickCmd = new VoiceKick(event, args, command, pguild);
            executeCommand(voicekickCmd, 2);

        } else if (command == ECommand.VOICEMUTE) {

            ACommand voicemuteCmd = new VoiceMute(event, args, command, pguild);
            executeCommand(voicemuteCmd, 2);

        } else if (command == ECommand.VOICEUNMUTE) {

            ACommand voiceUnmuteCmd = new VoiceUnmute(event, args, command, pguild);
            executeCommand(voiceUnmuteCmd, 2);

        } else if (command == ECommand.KICK) {

            ACommand kickCmd = new Kick(event, args, command, pguild, pguildMember);
            executeCommand(kickCmd, 2);

        } else if (command == ECommand.BAN) {

            ACommand banCmd = new Bans(event, args, command, pguild, pguildMember);
            executeCommand(banCmd, 2);

        } else if (command == ECommand.SOFTBAN) {

            ACommand softbanCmd = new Softban(event, args, command, pguild, pguildMember);
            executeCommand(softbanCmd, 2);

        } else if (command == ECommand.UNBAN) {

            ACommand unbanCmd = new Unban(event, args, command, pguild);
            executeCommand(unbanCmd, 2);

        } else if (command == ECommand.RESETCHAN) {

            ACommand resetChanCmd = new ResetChan(event, args, command, pguild);
            resetChanCmd.execute();
        }
    }

}