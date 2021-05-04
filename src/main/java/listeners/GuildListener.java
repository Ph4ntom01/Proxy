package listeners;

import java.sql.Timestamp;
import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.json.JSONObject;

import configuration.cache.EBlacklistCache;
import configuration.cache.ECommandCache;
import configuration.cache.EGuildCache;
import configuration.cache.EGuildMemberCache;
import configuration.constant.ECommand;
import configuration.constant.EID;
import configuration.constant.EPermission;
import configuration.file.Config;
import configuration.file.ConfigFactory;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import dao.pojo.PLogInvite;
import listeners.command.ACommandListener;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import proxy.BotStats;

public class GuildListener extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        if (!EBlacklistCache.INSTANCE.isGuildBlacklisted(event.getGuild().getId()) && !EBlacklistCache.INSTANCE.isMemberBlacklisted(event.getAuthor().getId()) && !event.getAuthor().isBot()) {

            EGuildCache.INSTANCE.getGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(guild -> {
                CommandChecker checker = new CommandChecker(event, guild, event.getMessage().getContentRaw());
                // Check if the command contains the prefix AND exists OR check if the bot is mentioned.
                if (checker.isValid() || checker.isSelfbotMention()) {
                    ECommand command = checker.buildCommand();
                    PGuildMember member = EGuildMemberCache.INSTANCE.getGuildMember(event.getMember());
                    ACommandListener listener = ACommandListener.buildListener(event, checker.getArgs(), command, guild, member);
                    // Check the user's permission.
                    if (listener.isAllowed(member)) {
                        listener.route();
                    }
                }
            });
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        // Create the guild.
        Guild guild = event.getGuild();
        ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
        PGuild pGuild = new PGuild();
        pGuild.setId(guild.getIdLong());
        pGuild.setName(guild.getName());
        guildDao.create(pGuild);

        // Create the guild owner.
        event.getGuild().retrieveOwner().queue(owner -> {
            ADao<PGuildMember> ownerDao = DaoFactory.getGuildMemberDAO();
            PGuildMember guildOwner = new PGuildMember();
            guildOwner.setGuildId(owner.getGuild().getIdLong());
            guildOwner.setId(owner.getIdLong());
            guildOwner.setName(owner.getUser().getName());
            guildOwner.setPermission((owner.getId().equals(EID.BOT_OWNER.getId())) ? EPermission.BOT_OWNER : EPermission.GUILD_OWNER);
            ownerDao.create(guildOwner);
        });

        // Create the json object.
        JSONObject guildLog = new JSONObject();
        guildLog.put("id", guild.getId());
        guildLog.put("name", guild.getName());
        guildLog.put("date", new Timestamp(System.currentTimeMillis()));

        // Create the guild log.
        ADao<PLogInvite> logInviteDao = DaoFactory.getLogInviteDAO();
        PLogInvite logInvite = new PLogInvite();
        logInvite.setId(guild.getIdLong());
        logInvite.setInviteFlag(true);
        logInvite.setGuildLog(guildLog.toString());
        logInviteDao.create(logInvite);

        // Send stats.
        Config conf = ConfigFactory.getConf();
        BotStats stats = new BotStats(conf, event.getJDA().getGuilds().size());
        stats.setDiscordBotListGuildCount();
        stats.setBotsOnDiscordGuildCount();
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        EGuildCache.INSTANCE.getGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(guild -> {
            ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
            guildDao.delete(guild);
            if (guild.getJoinChannel() != null) {
                ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
                PJoinChannel joinChannel = joinChannelDao.find(guild.getJoinChannel());
                joinChannelDao.delete(joinChannel);
            }
            if (guild.getLeaveChannel() != null) {
                ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
                PLeaveChannel leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());
                leaveChannelDao.delete(leaveChannel);
            }

            // Delete the guild from the cache.
            EGuildCache.INSTANCE.invalidate(guild.getId());

            // Create the json object.
            JSONObject guildLog = new JSONObject();
            guildLog.put("id", guild.getId().toString());
            guildLog.put("name", guild.getName());
            guildLog.put("join_channel_id", checkNObject(guild.getJoinChannel()));
            guildLog.put("leave_channel_id", checkNObject(guild.getLeaveChannel()));
            guildLog.put("control_channel_id", checkNObject(guild.getControlChannel()));
            guildLog.put("default_role_id", checkNObject(guild.getDefaultRole()));
            guildLog.put("prefix", guild.getPrefix());
            guildLog.put("shield", guild.getShield());
            guildLog.put("date", new Timestamp(System.currentTimeMillis()));

            // Create the guild log.
            ADao<PLogInvite> logInviteDao = DaoFactory.getLogInviteDAO();
            PLogInvite logInvite = new PLogInvite();
            logInvite.setId(guild.getId());
            logInvite.setInviteFlag(false);
            logInvite.setGuildLog(guildLog.toString());
            logInviteDao.create(logInvite);
        });

        // Send stats.
        Config conf = ConfigFactory.getConf();
        BotStats stats = new BotStats(conf, event.getJDA().getGuilds().size());
        stats.setDiscordBotListGuildCount();
        stats.setBotsOnDiscordGuildCount();
    }

    private String checkNObject(Object object) {
        return (object == null) ? "None" : object.toString();
    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        EGuildCache.INSTANCE.getGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(guild -> {
            if (Objects.equals(event.getChannel().getIdLong(), guild.getJoinChannel())) {
                ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
                ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
                PJoinChannel joinChannel = joinChannelDao.find(guild.getJoinChannel());
                guild.setJoinChannel(null);
                guildDao.update(guild);
                joinChannelDao.delete(joinChannel);
            }
            if (Objects.equals(event.getChannel().getIdLong(), guild.getLeaveChannel())) {
                ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
                ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
                PLeaveChannel leaveChannel = leaveChannelDao.find(guild.getLeaveChannel());
                guild.setLeaveChannel(null);
                guildDao.update(guild);
                leaveChannelDao.delete(leaveChannel);
            }
            if (Objects.equals(event.getChannel().getIdLong(), guild.getControlChannel())) {
                ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
                guild.setControlChannel(null);
                guildDao.update(guild);
            }
        });
    }

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        EGuildCache.INSTANCE.getGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(guild -> {
            if (Objects.equals(event.getRole().getIdLong(), guild.getDefaultRole())) {
                ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
                guild.setDefaultRole(null);
                guildDao.update(guild);
            }
        });
    }

    @Override
    public void onGuildUpdateName(GuildUpdateNameEvent event) {
        EGuildCache.INSTANCE.getGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(guild -> {
            ADao<PGuild> guildDao = DaoFactory.getGuildDAO();
            guild.setName(event.getNewName());
            guildDao.update(guild);
        });
    }

    @Override
    public void onGuildUpdateOwner(GuildUpdateOwnerEvent event) {
        event.getGuild().retrieveMemberById(event.getOldOwnerId()).queue(oldOwn -> {
            ADao<PGuildMember> gMemberDao = DaoFactory.getGuildMemberDAO();
            PGuildMember oldOwner = EGuildMemberCache.INSTANCE.getGuildMember(oldOwn);
            oldOwner.setPermission(EPermission.USER);
            gMemberDao.update(oldOwner);
        });
        event.getGuild().retrieveMemberById(event.getNewOwnerId()).queue(newOwn -> {
            ADao<PGuildMember> gMemberDao = DaoFactory.getGuildMemberDAO();
            PGuildMember newOwner = EGuildMemberCache.INSTANCE.getGuildMember(newOwn);
            newOwner.setPermission(EPermission.GUILD_OWNER);
            gMemberDao.update(newOwner);
        });
    }

    /**
     * Utility class to route the message sent by a user.
     */
    private class CommandChecker {

        private GuildMessageReceivedEvent event;
        private PGuild guild;
        private String[] args;

        private CommandChecker(GuildMessageReceivedEvent event, PGuild guild, String message) {
            this.event = event;
            this.guild = guild;
            args = message.split("\\s+");
        }

        /**
         * Check if the command is valid. It must contains the correct prefix and a valid command name.
         * 
         * @return True if the command is valid, otherwise false.
         */
        private boolean isValid() {
            // Isolate the command name from the prefix.
            String commandName = args[0].substring(guild.getPrefix().length());
            // Check if the command starts with the correct prefix.
            boolean hasPrefix = args[0].startsWith(guild.getPrefix());
            // Check if the command contains a valid command name.
            boolean isCommand = ECommandCache.INSTANCE.getCommands().containsKey(commandName);
            // Check if the command contains the prefix AND exists.
            if (hasPrefix && isCommand) {
                // Remove the prefix from args[0].
                args[0] = commandName;
                return true;
            }
            return false;
        }

        /**
         * Check if the bot is mentioned.
         * 
         * @return True if the bot is mentioned, otherwise false.
         */
        private boolean isSelfbotMention() {
            if (event.getMessage().getMentionedUsers().contains(event.getJDA().getSelfUser()) && args.length == 1) {
                args = new String[2];
                args[0] = ECommand.MEMBERINFO.getName();
                args[1] = EID.BOT_ID.getId();
                return true;
            }
            return false;
        }

        /**
         * Request the command to the cache and build it.
         * 
         * @return The command.
         */
        @Nullable
        private ECommand buildCommand() {
            return ECommandCache.INSTANCE.getCommand(args[0]);
        }

        /**
         * The user's message as a {@code String} array.
         * 
         * @return The user's message as a {@code String} array.
         */
        @Nonnull
        private String[] getArgs() {
            return args;
        }

    }

}