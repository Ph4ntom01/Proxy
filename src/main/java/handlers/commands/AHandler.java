package handlers.commands;

import commands.ACommand;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import dao.pojo.PGuildMember;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public abstract class AHandler {

    protected GuildMessageReceivedEvent event;
    protected String[] args;
    protected ECommand command;
    protected PGuild pguild;
    protected PGuildMember pguildMember;

    protected AHandler(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild) {
        this.event = event;
        this.args = args;
        this.command = command;
        this.pguild = pguild;
    }

    protected AHandler(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild pguild, PGuildMember pguildMember) {
        this.event = event;
        this.args = args;
        this.command = command;
        this.pguild = pguild;
        this.pguildMember = pguildMember;
    }

    public abstract void route();

    protected void executeCommand(ACommand command, int argsLength) {
        if ((args.length == argsLength) || (args.length >= argsLength)) {
            command.execute();
        } else {
            command.help(false);
        }
    }

}