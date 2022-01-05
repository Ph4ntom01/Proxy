package proxy;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.file.ConfigFactory;
import configuration.file.TOMLConfig;
import handlers.actions.GuildListener;
import handlers.actions.GuildModel;
import handlers.actions.MemberListener;
import handlers.actions.MemberModel;
import handlers.actions.MemberView;
import handlers.actions.OnReady;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Discord {

    private static final Logger LOG = LoggerFactory.getLogger(Discord.class);

    private TOMLConfig file;

    protected Discord() {
        file = ConfigFactory.getProxy();
    }

    protected void connect() {
        // Creates a JDABuilder with recommended default settings.
        // MemberCachePolicy is set to DEFAULT.
        // ChunkingFilter is set to NONE.
        // GatewayIntent is set to DEFAULT.
        // CacheFlag.ACTIVITY and CacheFlag.CLIENT_STATUS are disabled.
        JDABuilder builder = JDABuilder.createDefault(file.getString("token.discord"));

        // Sets the OnlineStatus that the connection will display.
        builder.setStatus(OnlineStatus.ONLINE);
        // Sets the Activity for the session.
        builder.setActivity(Activity.playing("@Proxy"));

        // Enables the specified GatewayIntents.
        // GUILD_MEMBERS as a goal to be informed about update/leave/join events of a guild.
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);

        // Configure the Chunking Filter and the Member Cache Policy.
        configureMemoryUsage(builder);

        // Adds all provided listeners to the list of listeners that will be used
        // to populate the JDA object.
        addEventListeners(builder);

        // Builds the JDA object and start the login process with the provided token.
        build(builder);
    }

    private void configureMemoryUsage(JDABuilder builder) {
        // Filters which guilds should use member chunking on guild initialization.
        // To use chunking, the GUILD_MEMBERS intent must be enabled! Otherwise you must use NONE!
        builder.setChunkingFilter(ChunkingFilter.NONE);

        // This will decide whether to cache a member (and its respective user).
        // All members are cached by default.
        // If a guild is enabled for chunking, all members will be cached for it.
        builder.setMemberCachePolicy(MemberCachePolicy.VOICE);
    }

    private void addEventListeners(JDABuilder builder) {
        GuildModel guildModel = new GuildModel();
        MemberModel memberModel = new MemberModel();
        MemberView memberView = new MemberView();
        builder.addEventListeners(new OnReady(), new GuildListener(guildModel), new MemberListener(memberModel, memberView));
    }

    private void build(JDABuilder builder) {
        try {
            JDA jda = builder.build().awaitReady();
            // Send stats.
            BotStats stats = new BotStats(file, Math.toIntExact(jda.getGuildCache().size()));
            stats.setDiscordBotListGuildCount();
            stats.setBotsOnDiscordGuildCount();
        } catch (LoginException | NullPointerException e) {
            LOG.error(e.getMessage());
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}