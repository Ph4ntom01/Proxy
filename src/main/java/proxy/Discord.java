package proxy;

import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

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

    private static final Logger LOG = Logger.getLogger(Discord.class.getName());

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
            LOG.log(java.util.logging.Level.SEVERE, e.getMessage());
        } catch (InterruptedException e) {
            LOG.log(java.util.logging.Level.SEVERE, e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}