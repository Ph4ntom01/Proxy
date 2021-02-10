package listeners.command;

import java.util.concurrent.TimeUnit;

import configuration.constant.ECommand;
import configuration.constant.EPermission;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public abstract class ACommandListener {

    protected GuildMessageReceivedEvent event;
    protected String[] args;
    protected ECommand command;
    protected PGuild guild;
    protected PGuildMember member;

    protected ACommandListener(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        this.event = event;
        this.args = args;
        this.command = command;
        this.guild = guild;
    }

    protected ACommandListener(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild, PGuildMember member) {
        this.event = event;
        this.args = args;
        this.command = command;
        this.guild = guild;
        this.member = member;
    }

    public abstract void route();

    /**
     * Check if the user is allowed to execute the command.
     * 
     * @param member The member to check.
     * 
     * @return True if the user is allowed, otherwise false.
     */
    public final boolean isAllowed(PGuildMember member) {
        if (command.getPermission() == EPermission.USER) { return true; }
        if (command.getPermission() == EPermission.BOT_OWNER && member.getPermission() == EPermission.BOT_OWNER) { return true; }
        if (command.getPermission().getLevel() <= member.getPermission().getLevel()) { return true; }
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage("You need to be **" + command.getPermission().getName().toLowerCase() + "**. ").queueAfter(300, TimeUnit.MILLISECONDS);
        return false;
    }

    /**
     * Factory method used to build the correct listener based on the requested command's permission.
     * <br>
     * <br>
     * Listeners :
     * <ul>
     * <li>{@link listeners.command.CommandAdministrator Administrator}</li>
     * <li>{@link listeners.command.CommandModerator Moderator}</li>
     * <li>{@link listeners.command.CommandUser User}</li>
     * <li>{@link listeners.command.CommandHelp Help}</li>
     * </ul>
     * 
     * @param event   The GuildMessageReceivedEvent object.
     * @param args    The message as a {@code String} array.
     * @param command The command.
     * @param guild   The guild.
     * @param member  The author.
     * 
     * @return The correct listener.
     */
    public static final ACommandListener buildListener(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild, PGuildMember member) {
        EPermission permission = command.getPermission();
        if (permission == EPermission.BOT_OWNER) { return new CommandBotOwner(event, args, command, guild); }
        if (permission == EPermission.GUILD_OWNER) { return new CommandGuildOwner(event, args, command, guild); }
        if (permission == EPermission.ADMINISTRATOR) { return new CommandAdministrator(event, args, command, guild, member); }
        if (permission == EPermission.MODERATOR) { return new CommandModerator(event, args, command, guild, member); }
        if (command == ECommand.HELP) { return new CommandHelp(event, args, command, guild); }
        return new CommandUser(event, args, command, guild);
    }

}