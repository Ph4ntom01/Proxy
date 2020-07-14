package factory;

import org.discordbots.api.client.DiscordBotListAPI;

import configuration.constants.ID;
import configuration.files.Config;

public class StatsFactory {

    private StatsFactory() {
    }

    public static DiscordBotListAPI getDBL(Config conf) {
        return new DiscordBotListAPI.Builder().token(conf.getValue("DBL")).botId(ID.PROXY.getId()).build();
    }

}