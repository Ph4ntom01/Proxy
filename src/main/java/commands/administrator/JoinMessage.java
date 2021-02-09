package commands.administrator;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class JoinMessage extends ACommand {

    public JoinMessage(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        super(event, args, command, guild);
    }

    public JoinMessage(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        if (getPGuild().getJoinChannel() != null) {
            ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
            PJoinChannel joinChannel = joinChannelDao.find(getPGuild().getJoinChannel());
            joinChannel.setMessage(getJoinMessage(getArgs()));
            joinChannelDao.update(joinChannel);
            sendMessage("Welcoming message has been successfully defined.");
        } else {
            sendMessage("In order to create a welcoming message, please select your welcoming channel first using `" + getGuildPrefix() + ECommand.JOINCHAN.getName() + " #aTextChannel`.");
        }
    }

    private String getJoinMessage(String[] args) {
        StringBuilder welcomeMessage = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            welcomeMessage.append(args[i]);
            welcomeMessage.append(" ");
        }
        return welcomeMessage.deleteCharAt(welcomeMessage.length() - 1).toString();
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Set the welcoming message.\n\nExample: `" + getGuildPrefix() + getCommandName() + " Welcome [member] !`\n\n"
                    + "*Add `[member]` if you want the bot to mention the joining member*.");
            // @formatter:on
        } else {
            // @formatter:off
            sendMessage(
                    "Set the welcoming message. **Example:** `" + getGuildPrefix() + getCommandName()
                    + " Welcome [member] !`\n*Add `[member]` if you want the bot to mention the joining member*.");
            // @formatter:on
        }
    }

}