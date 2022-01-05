package commands.moderator;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;

public class Slowmode extends ACommand {

    public Slowmode(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        super(event, args, command, pguild);
    }

    public Slowmode(GuildMessageReceivedEvent event, ECommand command, PGuild pguild) {
        super(event, command, pguild);
    }

    @Override
    public void execute() {
        int slowmode = getIntArg(1);
        if (slowmode == 0) {
            if (getChannel().getSlowmode() == 0) {
                sendMessage("Slowmode has already been **disabled** !");
            } else {
                setSlowmode(slowmode);
                sendMessage("Slowmode is now **disabled** !");
            }
        } else if (slowmode == getChannel().getSlowmode()) {
            sendMessage("This amount has already been defined.");

        } else if (slowmode > 0 && slowmode <= 30) {
            setSlowmode(slowmode);
            sendMessage("Slowmode is now **enabled** !" + " (" + slowmode + "s).");
        } else {
            sendMessage("Please enter a number between **0** and **30**.");
        }
    }

    private void setSlowmode(int slowmode) {
        try {
            getChannel().getManager().setSlowmode(slowmode).queue();
        } catch (InsufficientPermissionException e) {
            sendMessage("Missing permission: **" + Permission.MESSAGE_MANAGE.getName() + " **or** " + Permission.MANAGE_CHANNEL.getName() + "**.");
        }
    }

    @Override
    public void help(boolean embedState) {
        if (embedState) {
            sendHelpEmbed("Change the slowmode amount on the current channel.\n\nExample: `" + getPGuildPrefix() + getCommandName() + " 5`");
        } else {
            sendMessage("Change the slowmode amount on the current channel. **Example:** `" + getPGuildPrefix() + getCommandName() + " 5`.");
        }
    }

}