package factory;

import org.discordbots.api.client.DiscordBotListAPI;

import configuration.constant.ID;
import configuration.file.Config;

public class StatsFactory {

    private StatsFactory() {
    }

    public static DiscordBotListAPI getDBL(Config conf) {
        return new DiscordBotListAPI.Builder().token(conf.getString("token.dbl")).botId(ID.PROXY.getId()).build();
    }

}