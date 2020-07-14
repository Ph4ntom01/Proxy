package commands.moderator;

import java.awt.Color;

import commands.CommandManager;
import configuration.constants.Command;
import dao.pojo.GuildPojo;
import listeners.commands.ModeratorListener;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import proxy.ProxyEmbed;
import proxy.ProxyUtils;

public class VoiceMute extends ModeratorListener implements CommandManager {

    private GuildMessageReceivedEvent event;
    private GuildPojo guild;

    public VoiceMute(GuildMessageReceivedEvent event, GuildPojo guild) {
        super(event, guild);
        this.event = event;
        this.guild = guild;
    }

    @Override
    public void execute() {
        try {
            event.getGuild().retrieveMemberById(ProxyUtils.getMentionnedEntity(MentionType.USER, event.getMessage(), ProxyUtils.getArgs(event)[1]), false).queue(member -> {
                try {
                    if (!member.getVoiceState().isGuildMuted()) {
                        event.getGuild().mute(member, true).queue();
                        ProxyUtils.sendMessage(event, "**" + member.getUser().getAsTag() + "** is successfully voice muted !");
                    } else {
                        ProxyUtils.sendMessage(event, "**" + member.getUser().getAsTag() + "** has already been voice muted !");
                    }
                } catch (IndexOutOfBoundsException e) {
                    ProxyUtils.sendMessage(event, "Invalid ID or mention.");

                } catch (IllegalStateException e) {
                    ProxyUtils.sendMessage(event, "You cannot mute a member who isn't in a voice channel.");

                } catch (InsufficientPermissionException e) {
                    ProxyUtils.sendMessage(event, "Missing permission: **" + Permission.VOICE_MUTE_OTHERS.getName() + "**.");
                }
            }, ContextException.here(acceptor -> ProxyUtils.sendMessage(event, "Invalid ID or mention.")));

        } catch (IllegalArgumentException | NullPointerException e) {
            ProxyUtils.sendMessage(event, "Invalid ID or mention.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            ProxyEmbed embed = new ProxyEmbed();
            // @formatter:off
            embed.help(Command.VOICEMUTE.getName(),
                    "Mute a specified member from the voice channel he is in.\n\n"
                    + "Example: `" + guild.getPrefix() + Command.VOICEMUTE.getName() + " @aMember`\n"
                    + "*You can also mention a member by his ID*.",
                    Color.ORANGE);
            // @formatter:on
            ProxyUtils.sendEmbed(event, embed);
        } else {
            // @formatter:off
            ProxyUtils.sendMessage(event,
                    "Mute a specified member from the voice channel he is in. "
                    + "**Example:** `" + guild.getPrefix() + Command.VOICEMUTE.getName() + " @aMember`.");
            // @formatter:on
        }
    }

}