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

public class VoiceKick extends ACommand {

    public VoiceKick(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    public VoiceKick(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        RestAction<Member> command = retrieveMentionnedMember(1, false);
        if (command == null) { return; }
        command.queue(mentionnedMember -> {
            try {
                getGuild().kickVoiceMember(mentionnedMember).queue();
                sendMessage("**" + mentionnedMember.getUser().getAsTag() + "** is successfully voice kicked !");

            } catch (IndexOutOfBoundsException e) {
                sendMessage("**" + getArgs()[1] + "** is not a member.");

            } catch (IllegalStateException e) {
                sendMessage("You cannot kick a member who isn't in a voice channel.");

            } catch (InsufficientPermissionException e) {
                sendMessage("Missing permission: **" + Permission.VOICE_MOVE_OTHERS.getName() + "**.");
            }
        }, ContextException.here(acceptor -> sendMessage("**" + getArgs()[1] + "** is not a member.")));
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Kick a specified member from the voice channel he is in.\n\n"
                    + "Example: `" + getPGuildPrefix() + getCommandName() + " @aMember`\n"
                    + "*You can also mention a member by his ID*.");
            // @formatter:on
        } else {
            // @formatter:off
            sendMessage(
                    "Kick a specified member from the voice channel he is in. "
                    + "**Example:** `" + getPGuildPrefix() + getCommandName() + " @aMember`.");
            // @formatter:on
        }
    }

}