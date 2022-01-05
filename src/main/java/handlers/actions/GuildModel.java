package handlers.actions;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import configuration.constant.ECommand;
import configuration.constant.EInviteState;
import configuration.constant.ELogInvite;
import configuration.constant.EPermission;
import configuration.file.ConfigFactory;
import configuration.file.TOMLConfig;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import dao.pojo.PLogInvite;
import handlers.commands.AHandler;
import handlers.commands.AdministratorHandler;
import handlers.commands.BotOwnerHandler;
import handlers.commands.GuildOwnerHandler;
import handlers.commands.HelpHandler;
import handlers.commands.ModeratorHandler;
import handlers.commands.UserHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import proxy.BotStats;

public class GuildModel {

    protected void sendStats(int guildCount) {
        TOMLConfig conf = ConfigFactory.getProxy();
        BotStats stats = new BotStats(conf, guildCount);
        stats.setDiscordBotListGuildCount();
        stats.setBotsOnDiscordGuildCount();
    }

    protected void createInviteLog(Guild guild) {
        ADao<PLogInvite> logInviteDao = DaoFactory.getLogInviteDAO();
        PLogInvite logInvite = new PLogInvite();
        JSONObject joinedLog = new JSONObject();

        joinedLog.put(ELogInvite.STATE.getName(), EInviteState.JOIN_STATE.getName());
        joinedLog.put(ELogInvite.ID.getName(), guild.getId());
        joinedLog.put(ELogInvite.NAME.getName(), guild.getName());
        joinedLog.put(ELogInvite.DATE.getName(), new Timestamp(System.currentTimeMillis()));

        logInvite.setGuildLog(joinedLog.toString());
        logInviteDao.create(logInvite);
    }

    protected void createLeaveLog(PGuild pguild) {
        ADao<PLogInvite> logInviteDao = DaoFactory.getLogInviteDAO();
        PLogInvite logInvite = new PLogInvite();
        JSONObject leftLog = new JSONObject();

        leftLog.put(ELogInvite.STATE.getName(), EInviteState.LEAVE_STATE.getName());
        leftLog.put(ELogInvite.ID.getName(), pguild.getId().toString());
        leftLog.put(ELogInvite.NAME.getName(), pguild.getName());
        leftLog.put(ELogInvite.JOIN_CHANNEL.getName(), (pguild.getJoinChannel() == null) ? "None" : pguild.getJoinChannel().toString());
        leftLog.put(ELogInvite.LEAVE_CHANNEL.getName(), (pguild.getLeaveChannel() == null) ? "None" : pguild.getLeaveChannel().toString());
        leftLog.put(ELogInvite.CONTROL_CHANNEL.getName(), (pguild.getControlChannel() == null) ? "None" : pguild.getControlChannel().toString());
        leftLog.put(ELogInvite.DEFAULT_ROLE.getName(), (pguild.getDefaultRole() == null) ? "None" : pguild.getDefaultRole().toString());
        leftLog.put(ELogInvite.PREFIX.getName(), pguild.getPrefix());
        leftLog.put(ELogInvite.SHIELD.getName(), pguild.getShield());
        leftLog.put(ELogInvite.DATE.getName(), new Timestamp(System.currentTimeMillis()));

        logInvite.setGuildLog(leftLog.toString());
        logInviteDao.create(logInvite);
    }

    /**
     * Check if the user is allowed to execute the command.
     * 
     * @param pguildMember The member to check.
     * 
     * @return True if the user is allowed, otherwise false.
     */
    protected boolean isMemberAllowed(GuildMessageReceivedEvent event, ECommand command, PGuildMember pguildMember) {
        if (command.getPermission() == EPermission.USER) { return true; }
        if (command.getPermission() == EPermission.BOT_OWNER && pguildMember.getPermission() == EPermission.BOT_OWNER) { return true; }
        if (command.getPermission().getLevel() <= pguildMember.getPermission().getLevel()) { return true; }
        event.getChannel().sendTyping().queue();
        // @formatter:off
        event.getChannel().sendMessage(
                "You don't have the required permission to execute this command. " +
                "(command's permission is : **" + command.getPermission().getName().toLowerCase() + "**).")
                .queueAfter(300, TimeUnit.MILLISECONDS);
        // @formatter:on
        return false;
    }

    /**
     * Factory method used to build the correct handler based on the requested command's permission.
     * <br>
     * <br>
     * Handlers :
     * <ul>
     * <li>{@link handlers.commands.AdministratorHandler Administrator}</li>
     * <li>{@link handlers.commands.ModeratorHandler Moderator}</li>
     * <li>{@link handlers.commands.UserHandler User}</li>
     * <li>{@link handlers.commands.HelpHandler Help}</li>
     * </ul>
     * 
     * @param event        The GuildMessageReceivedEvent object.
     * @param args         The message as a {@code String} array.
     * @param command      The command.
     * @param pguild       The guild.
     * @param pguildMember The author.
     * 
     * @return The correct listener.
     */
    protected static final AHandler buildHandler(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild, PGuildMember pguildMember) {
        EPermission permission = command.getPermission();
        if (permission == EPermission.BOT_OWNER) { return new BotOwnerHandler(event, args, command, pguild); }
        if (permission == EPermission.GUILD_OWNER) { return new GuildOwnerHandler(event, args, command, pguild); }
        if (permission == EPermission.ADMINISTRATOR) { return new AdministratorHandler(event, args, command, pguild); }
        if (permission == EPermission.MODERATOR) { return new ModeratorHandler(event, args, command, pguild, pguildMember); }
        if (command == ECommand.HELP) { return new HelpHandler(event, args, command, pguild); }
        return new UserHandler(event, args, command, pguild);
    }

}