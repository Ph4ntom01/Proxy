package proxy;

import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.file.Config;
import configuration.file.ConfigFactory;
import listeners.generics.GuildListener;
import listeners.generics.MemberListener;
import listeners.generics.ReadyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Discord {

    private static final Logger LOG = LoggerFactory.getLogger(Discord.class);

    private Config conf;

    protected Discord() {
        conf = ConfigFactory.getConf();
    }

    protected void connect() {
        JDABuilder builder = JDABuilder.createDefault(conf.getString("token.discord"));
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("@Proxy"));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        configureMemoryUsage(builder);
        addEventListeners(builder);
        build(builder);
    }

    private void configureMemoryUsage(JDABuilder builder) {
        builder.setChunkingFilter(ChunkingFilter.NONE);
        builder.setMemberCachePolicy(MemberCachePolicy.OWNER);
    }

    private void addEventListeners(JDABuilder builder) {
        builder.addEventListeners(new ReadyListener(), new GuildListener(), new MemberListener());
    }

    private void build(JDABuilder builder) {
        try {
            JDA jda = builder.build().awaitReady();
            // Send stats.
            BotStats stats = new BotStats(conf, Math.toIntExact(jda.getGuildCache().size()));
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