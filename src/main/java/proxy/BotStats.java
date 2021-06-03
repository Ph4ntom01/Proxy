package proxy;

import java.io.IOException;

import org.discordbots.api.client.DiscordBotListAPI;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import configuration.constant.EID;
import configuration.file.Config;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BotStats {

    private static final Logger LOG = LoggerFactory.getLogger(BotStats.class);

    private Config conf;
    private int guildCount;

    public BotStats(Config conf, int guildCount) {
        this.conf = conf;
        this.guildCount = guildCount;
    }

    /**
     * Send stats to https://top.gg/bot/592680274962415635
     */
    public void setDiscordBotListGuildCount() {
        // @formatter:off
        new DiscordBotListAPI.Builder()
            .token(conf.getString("token.dbl"))
            .botId(EID.BOT_ID.getId())
            .build()
            .setStats(guildCount);
        // @formatter:on
    }

    /**
     * Send stats to https://bots.ondiscord.xyz/bots/592680274962415635
     */
    public void setBotsOnDiscordGuildCount() {
        JSONObject json = new JSONObject();
        json.put("guildCount", guildCount);
        OkHttpClient client = new OkHttpClient();
        @SuppressWarnings("deprecation")
        RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json.toString());
        // @formatter:off
        Request request = new Request.Builder()
                .url("https://bots.ondiscord.xyz/bot-api/bots/" + EID.BOT_ID.getId() + "/guilds")
                .addHeader("Authorization", conf.getString("token.bod"))
                .post(body)
                .build();
        // @formatter:on
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                LOG.info("Successfully sent guild count to API.");
            } else {
                LOG.error("An error occurred (Response : {})", response.body().string());
            }
        } catch (IOException e) {
            LOG.error(e.getMessage());
        }
    }

}