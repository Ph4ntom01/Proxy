package proxy;

import javax.security.auth.login.LoginException;

import commands.CommandRouter;
import configuration.file.Config;
import factory.ConfigFactory;
import factory.StatsFactory;
import listeners.guild.GuildChannelDelete;
import listeners.guild.GuildJoin;
import listeners.guild.GuildLeave;
import listeners.guild.GuildRoleDelete;
import listeners.guild.GuildUpdateName;
import listeners.guild.GuildUpdateOwner;
import listeners.member.MemberJoin;
import listeners.member.MemberLeave;
import listeners.member.MemberName;
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
        conf = ConfigFactory.getToken();
    }

    protected void connect() {
        JDABuilder builder = JDABuilder.createDefault(conf.getValue("Discord"));
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("@Proxy"));
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
        configureMemoryUsage(builder);
        addEventListeners(builder);
        build(builder);
    }

    private void configureMemoryUsage(JDABuilder builder) {
        builder.setChunkingFilter(ChunkingFilter.NONE);
        builder.setMemberCachePolicy(MemberCachePolicy.ALL);
    }

    private void addEventListeners(JDABuilder builder) {
        builder.addEventListeners(new Ready(), new GuildJoin(), new GuildLeave(), new MemberJoin(), new MemberLeave(), new MemberName(), new GuildChannelDelete(), new GuildRoleDelete(),
                new GuildUpdateName(), new GuildUpdateOwner(), new CommandRouter());
    }

    private void build(JDABuilder builder) {
        try {
            JDA jda = builder.build().awaitReady();
            setStats(jda);
        } catch (LoginException e) {
        } catch (InterruptedException e) {
        } catch (NullPointerException e) {
        }
    }

    private void setStats(JDA jda) {
        StatsFactory.getDBL(conf).setStats(Math.toIntExact(jda.getGuildCache().size()));
    }

}