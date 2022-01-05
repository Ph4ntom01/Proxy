package handlers.actions;

import java.util.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import configuration.cache.EBlacklistCache;
import configuration.cache.ECommandCache;
import configuration.cache.EGuildCache;
import configuration.cache.EGuildMemberCache;
import configuration.constant.ECommand;
import configuration.constant.EID;
import configuration.constant.EPermission;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import dao.pojo.PJoinChannel;
import dao.pojo.PLeaveChannel;
import handlers.commands.AHandler;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateNameEvent;
import net.dv8tion.jda.api.events.guild.update.GuildUpdateOwnerEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GuildListener extends ListenerAdapter {

    private GuildModel guildModel;

    public GuildListener(GuildModel guildModel) {
        this.guildModel = guildModel;
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        // @formatter:off
        if (!EBlacklistCache.INSTANCE.isGuildBlacklisted(event.getGuild().getId()) && 
            !EBlacklistCache.INSTANCE.isMemberBlacklisted(event.getAuthor().getId()) && 
            !event.getAuthor().isBot()) {
        // @formatter:on
            EGuildCache.INSTANCE.getPGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(pguild -> {
                CommandChecker checker = new CommandChecker(event, pguild);
                // Check if the command contains the prefix AND exists OR check if the bot is mentioned.
                if (checker.isValid() || checker.isSelfbotMention()) {
                    ECommand command = checker.buildCommand();
                    PGuildMember pguildMember = EGuildMemberCache.INSTANCE.getPGuildMember(event.getMember());
                    // Check the user's permission.
                    if (guildModel.isMemberAllowed(event, command, pguildMember)) {
                        AHandler handler = GuildModel.buildHandler(event, checker.getArgs(), command, pguild, pguildMember);
                        handler.route();
                    }
                }
            });
        }
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        // Create the guild.
        ADao<PGuild> pguildDao = DaoFactory.getPGuildDAO();
        PGuild pguild = new PGuild();
        pguild.setId(event.getGuild().getIdLong());
        pguild.setName(event.getGuild().getName());
        pguildDao.create(pguild);

        // Create the guild log.
        guildModel.createInviteLog(event.getGuild());

        // Send stats.
        guildModel.sendStats(event.getJDA().getGuilds().size());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        EGuildCache.INSTANCE.getPGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(pguild -> {
            // Delete the guild.
            ADao<PGuild> pguildDao = DaoFactory.getPGuildDAO();
            pguildDao.delete(pguild);

            // Delete the controlgate channels.
            if (pguild.getJoinChannel() != null) {
                ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
                PJoinChannel joinChannel = joinChannelDao.find(pguild.getJoinChannel());
                joinChannelDao.delete(joinChannel);
            }
            if (pguild.getLeaveChannel() != null) {
                ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
                PLeaveChannel leaveChannel = leaveChannelDao.find(pguild.getLeaveChannel());
                leaveChannelDao.delete(leaveChannel);
            }

            // Delete the guild from the cache.
            EGuildCache.INSTANCE.invalidate(pguild.getId());

            // Create the guild log.
            guildModel.createLeaveLog(pguild);

            // Send stats.
            guildModel.sendStats(event.getJDA().getGuilds().size());
        });
    }

    @Override
    public void onTextChannelDelete(TextChannelDeleteEvent event) {
        EGuildCache.INSTANCE.getPGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(pguild -> {
            if (Objects.equals(event.getChannel().getIdLong(), pguild.getJoinChannel())) {
                ADao<PGuild> pguildDao = DaoFactory.getPGuildDAO();
                ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
                PJoinChannel joinChannel = joinChannelDao.find(pguild.getJoinChannel());
                pguild.setJoinChannel(null);
                pguildDao.update(pguild);
                joinChannelDao.delete(joinChannel);
            }
            if (Objects.equals(event.getChannel().getIdLong(), pguild.getLeaveChannel())) {
                ADao<PGuild> pguildDao = DaoFactory.getPGuildDAO();
                ADao<PLeaveChannel> leaveChannelDao = DaoFactory.getLeaveChannelDAO();
                PLeaveChannel leaveChannel = leaveChannelDao.find(pguild.getLeaveChannel());
                pguild.setLeaveChannel(null);
                pguildDao.update(pguild);
                leaveChannelDao.delete(leaveChannel);
            }
            if (Objects.equals(event.getChannel().getIdLong(), pguild.getControlChannel())) {
                ADao<PGuild> pguildDao = DaoFactory.getPGuildDAO();
                pguild.setControlChannel(null);
                pguildDao.update(pguild);
            }
        });
    }

    @Override
    public void onRoleDelete(RoleDeleteEvent event) {
        EGuildCache.INSTANCE.getPGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(pguild -> {
            if (Objects.equals(event.getRole().getIdLong(), pguild.getDefaultRole())) {
                ADao<PGuild> pguildDao = DaoFactory.getPGuildDAO();
                pguild.setDefaultRole(null);
                pguildDao.update(pguild);
            }
        });
    }

    @Override
    public void onGuildUpdateName(GuildUpdateNameEvent event) {
        EGuildCache.INSTANCE.getPGuildAsync(event.getGuild().getIdLong()).thenAcceptAsync(pguild -> {
            ADao<PGuild> pguildDao = DaoFactory.getPGuildDAO();
            pguild.setName(event.getNewName());
            pguildDao.update(pguild);
        });
    }

    @Override
    public void onGuildUpdateOwner(GuildUpdateOwnerEvent event) {
        event.getGuild().retrieveMemberById(event.getOldOwnerId()).queue(oldOwn -> {
            ADao<PGuildMember> pguildMemberDao = DaoFactory.getPGuildMemberDAO();
            PGuildMember oldOwner = EGuildMemberCache.INSTANCE.getPGuildMember(oldOwn);
            oldOwner.setPermission(EPermission.USER);
            pguildMemberDao.update(oldOwner);
        });
        event.getGuild().retrieveMemberById(event.getNewOwnerId()).queue(newOwn -> {
            ADao<PGuildMember> pguildMemberDao = DaoFactory.getPGuildMemberDAO();
            PGuildMember newOwner = EGuildMemberCache.INSTANCE.getPGuildMember(newOwn);
            newOwner.setPermission(EPermission.GUILD_OWNER);
            pguildMemberDao.update(newOwner);
        });
    }

    /**
     * Utility class to route the message sent by a user.
     */
    private class CommandChecker {

        private GuildMessageReceivedEvent event;
        private PGuild pguild;
        private String[] args;

        private CommandChecker(GuildMessageReceivedEvent event, PGuild pguild) {
            this.event = event;
            this.pguild = pguild;
            args = event.getMessage().getContentRaw().split("\\s+");
        }

        /**
         * Check if the command is valid. It must contains the correct prefix and a valid command name.
         * 
         * @return True if the command is valid, otherwise false.
         */
        private boolean isValid() {
            // Isolate the command name from the prefix.
            String commandName = args[0].substring(pguild.getPrefix().length());
            // Check if the command starts with the correct prefix.
            boolean hasPrefix = args[0].startsWith(pguild.getPrefix());
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