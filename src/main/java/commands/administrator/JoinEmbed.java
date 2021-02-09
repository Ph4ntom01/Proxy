package commands.administrator;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.database.ADao;
import dao.database.DaoFactory;
import dao.pojo.PGuild;
import dao.pojo.PJoinChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class JoinEmbed extends ACommand {

    public JoinEmbed(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        super(event, args, command, guild);
    }

    public JoinEmbed(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        super(event, command, guild);
    }

    @Override
    public void execute() {
        if (getPGuild().getJoinChannel() != null) {
            ADao<PJoinChannel> joinChannelDao = DaoFactory.getJoinChannelDAO();
            PJoinChannel joinChannel = joinChannelDao.find(getPGuild().getJoinChannel());
            if (getArgs()[1].equalsIgnoreCase("on")) {
                if (joinChannel.getEmbed()) {
                    sendMessage("Welcoming box has already been **enabled**.");
                } else if (!joinChannel.getEmbed()) {
                    joinChannel.setEmbed(true);
                    joinChannelDao.update(joinChannel);
                    sendMessage("Welcoming box is now **enabled**.");
                }
            } else if (getArgs()[1].equalsIgnoreCase("off")) {
                if (!joinChannel.getEmbed()) {
                    sendMessage("Welcoming box has already been **disabled**.");
                } else if (joinChannel.getEmbed()) {
                    joinChannel.setEmbed(false);
                    joinChannelDao.update(joinChannel);
                    sendMessage("Welcoming box is now **disabled**, you will no longer receive a box when a member joins the server.");
                }
            } else {
                sendMessage("Please specify **on** or **off**.");
            }
        } else {
            sendMessage("In order to create a welcoming box, please select your welcoming channel first using `" + getGuildPrefix() + ECommand.JOINCHAN.getName() + " #aTextChannel`.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            // @formatter:off
            sendHelpEmbed(
                    "Set the welcoming box.\n\n"
                    + "Example:\n\n`"
                    + getGuildPrefix() + getCommandName() + " on` *enables the welcoming box*.\n`"
                    + getGuildPrefix() + getCommandName() + " off` *disables the welcoming box*.");
            // @formatter:on
        } else {
            sendMessage("Set the welcoming box. **Example:** `" + getGuildPrefix() + getCommandName() + " on` *enables the welcoming box*.");
        }
    }

}