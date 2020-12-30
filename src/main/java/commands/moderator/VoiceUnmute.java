package commands.moderator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constant.Command;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import proxy.utility.ProxyEmbed;
import proxy.utility.ProxyString;
import proxy.utility.ProxyUtils;

public class VoiceUnmute implements CommandManager {

    private GuildMessageReceivedEvent event;
    private PGuild guild;

    public VoiceUnmute(GuildMessageReceivedEvent event, PGuild guild) {
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            event.getGuild().retrieveMemberById(ProxyString.getMentionnedEntity(MentionType.USER, event.getMessage(), ProxyUtils.getArgs(event.getMessage())[1]), false).queue(gMember -> {
                try {
                    if (gMember.getVoiceState().isGuildMuted()) {
                        event.getGuild().mute(gMember, false).queue();
                        ProxyUtils.sendMessage(event.getChannel(), "**" + gMember.getUser().getAsTag() + "** is successfully voice unmuted !");
                    } else {
                        ProxyUtils.sendMessage(event.getChannel(), "**" + gMember.getUser().getAsTag() + "** has already been voice unmuted !");
                    }
                } catch (IndexOutOfBoundsException e) {
                    ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.");

                } catch (IllegalStateException e) {
                    ProxyUtils.sendMessage(event.getChannel(), "You cannot unmute a member who isn't in a voice channel.");

                } catch (InsufficientPermissionException e) {
                    ProxyUtils.sendMessage(event.getChannel(), "Missing permission: **" + Permission.VOICE_MUTE_OTHERS.getName() + "**.");
                }
            }, ContextException.here(acceptor -> ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.")));

        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event.getChannel(), "Invalid ID or mention.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.VOICEUNMUTE.getName(),
                    "Unmute a specified member from the voice channel he is in.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.VOICEUNMUTE.getName() + " @aMember`\n"
                    + "*You can also mention a member by his ID*.",
                    Color.ORANGE);
            // formatter:on
            ProxyUtils.sendEmbed(event.getChannel(), embed);
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event.getChannel(),
                    "Unmute a specified member from the voice channel he is in. "
                    + "**Example:** `" + guild.getPrefix() + Command.VOICEUNMUTE.getName() + " @aMember`.");
            // @formatter:on
        }
    }

}