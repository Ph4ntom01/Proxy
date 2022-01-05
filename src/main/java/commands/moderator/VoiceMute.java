package commands.moderator;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;

public class VoiceMute extends ACommand {

    public VoiceMute(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    public VoiceMute(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        RestAction<Member> command = retrieveMentionnedMember(1, false);
        if (command == null) { return; }
        command.queue(gMember -> {
            try {
                if (!gMember.getVoiceState().isGuildMuted()) {
                    getGuild().mute(gMember, true).queue();
                    sendMessage("**" + gMember.getUser().getAsTag() + "** is successfully voice muted !");
                } else {
                    sendMessage("**" + gMember.getUser().getAsTag() + "** has already been voice muted !");
                }
            } catch (IndexOutOfBoundsException e) {
                sendMessage("**" + getArgs()[1] + "** is not a member.");

            } catch (IllegalStateException e) {
                sendMessage("You cannot mute a member who isn't in a voice channel.");

            } catch (InsufficientPermissionException e) {
                sendMessage("Missing permission: **" + Permission.VOICE_MUTE_OTHERS.getName() + "**.");
            }
        }, ContextException.here(acceptor -> sendMessage("**" + getArgs()[1] + "** is not a member.")));
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Mute a specified member from the voice channel he is in.\n\n"
                    + "Example: `" + getPGuildPrefix() + getCommandName() + " @aMember`\n"
                    + "*You can also mention a member by his ID*.");
            // @formatter:on
        } else {
            // @formatter:off
            sendMessage(
                    "Mute a specified member from the voice channel he is in. "
                    + "**Example:** `" + getPGuildPrefix() + getCommandName() + " @aMember`.");
            // @formatter:on
        }
    }

}