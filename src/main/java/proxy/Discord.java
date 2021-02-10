package proxy;

import javax.security.auth.login.LoginException;

import configuration.file.Config;
import configuration.file.ConfigFactory;
import listeners.GuildListener;
import listeners.MemberListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class Discord {

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
        builder.addEventListeners(new Ready(), new GuildListener(), new MemberListener());
    }

    private void build(JDABuilder builder) {
        try {
            JDA jda = builder.build().awaitReady();
            // Send stats.
            BotStats stats = new BotStats(conf, Math.toIntExact(jda.getGuildCache().size()));
            stats.setDiscordBotListGuildCount();
            stats.setBotsOnDiscordGuildCount();
        } catch (LoginException e) {
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (NullPointerException e) {
        }
    }

}