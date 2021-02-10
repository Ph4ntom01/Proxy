package proxy;

import org.discordbots.api.client.DiscordBotListAPI;
import org.json.JSONObject;

import configuration.constant.EID;
import configuration.file.Config;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class BotStats {

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
        // @formatter:off
    }

    /**
     * Send stats to https://bots.ondiscord.xyz/bots/592680274962415635
     */
    public void setBotsOnDiscordGuildCount() {
        JSONObject json = new JSONObject();
        json.put("guildCount", guildCount);
        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
        // @formatter:off
        new Request.Builder()
            .url("https://bots.ondiscord.xyz/bot-api/bots/" + EID.BOT_ID.getId() + "/guilds")
            .addHeader("Authorization", conf.getString("token.bod"))
            .post(body)
            .build();
        // @formatter:on
    }

}