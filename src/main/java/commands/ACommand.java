package commands;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import configuration.cache.ECommandCache;
import configuration.constant.ECategory;
import configuration.constant.ECommand;
import dao.pojo.PGuild;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Guild.Ban;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.MentionType;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.RestAction;

/**
 * The inheritance for commands used in {@link commands.administrator Administrator},
 * {@link commands.moderator Moderator} and {@link commands.user User} commands.
 */
public abstract class ACommand {

    private GuildMessageReceivedEvent event;
    private String[] args;
    private ECommand command;
    private PGuild guild;

    protected ACommand(GuildMessageReceivedEvent event, ECommand command, PGuild guild) {
        this.event = event;
        this.command = command;
        this.guild = guild;
    }

    protected ACommand(GuildMessageReceivedEvent event, String[] args, ECommand command, PGuild guild) {
        this.event = event;
        this.args = args;
        this.command = command;
        this.guild = guild;
    }

    /**
     * Execute the {@link configuration.constant.ECommand Command}.
     */
    public abstract void execute();

    /**
     * Send the description of the {@link configuration.constant.ECommand Command}.
     * 
     * @param embedState If true, send an embed, otherwise a simple message.
     */
    public abstract void help(boolean embedState);

    /**
     * Send a message to the channel where the event occurs. <br>
     * The message will be send after 300 {@link java.util.concurrent.TimeUnit MILLISECONDS}.
     * 
     * @param message The message to send.
     */
    protected void sendMessage(String message) {
        getChannel().sendTyping().queue();
        getChannel().sendMessage(message).queueAfter(300, TimeUnit.MILLISECONDS);
    }

    /**
     * Send a private message.
     * 
     * @param message The message to send.
     */
    protected void sendPrivateMessage(String message) {
        getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessage(message)).queue();
    }

    /**
     * Build an embed and send it to the channel where the event occurs. <br>
     * The embed will be send after 300 {@link java.util.concurrent.TimeUnit MILLISECONDS}.
     * 
     * @param embed The embed to send.
     */
    protected void sendEmbed(EmbedBuilder embed) {
        getChannel().sendTyping().queue();
        getChannel().sendMessage(embed.build()).queueAfter(300, TimeUnit.MILLISECONDS);
    }

    /**
     * Build an embed and send it as a private message to the bot owner.
     * 
     * @param embed The embed to send.
     */
    protected void sendPrivateEmbedToBotOwner(EmbedBuilder embed) {
        getAuthor().openPrivateChannel().flatMap(channel -> channel.sendMessage(embed.build())).queue(response -> response.delete().queueAfter(5, TimeUnit.MINUTES));
    }

    /**
     * Send a help embed.
     * 
     * @param message The message to send.
     */
    protected void sendHelpEmbed(String message) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.ORANGE);
        embed.setTitle(":gear: Command: " + command.getName());
        embed.addField("", message, true);
        sendEmbed(embed);
    }

    /**
     * The {@link configuration.constant.ECommand Command}.
     * 
     * @return The {@link configuration.constant.ECommand Command}.
     */
    protected ECommand getCommand() {
        return command;
    }

    /**
     * The {@link configuration.constant.ECommand Command} name.
     * 
     * @return The {@link configuration.constant.ECommand Command} name.
     */
    protected String getCommandName() {
        return command.getName();
    }

    /**
     * Filter the commands by a specified category.
     * 
     * @param category The category to apply the filter.
     * 
     * @return A string containing the filtered commands.
     */
    @Nonnull
    protected String getCommandsByCategory(ECategory category) {
        List<ECommand> commands = ECommandCache.INSTANCE.getCommands().values().stream().filter(cmd -> cmd.getCategory() == category).sorted().collect(Collectors.toList());
        StringBuilder commandsList = new StringBuilder();
        for (ECommand cmdTmp : commands) {
            commandsList.append("`" + cmdTmp.getName() + "` ");
        }
        return commandsList.toString();
    }

    /**
     * The received {@link net.dv8tion.jda.api.entities.Message Message} as an array of Strings.
     * 
     * @return The received {@link net.dv8tion.jda.api.entities.Message Message} as an array of Strings.
     */
    @Nonnull
    protected String[] getArgs() {
        return args;
    }

    /**
     * Convert the desired position from the {@link net.dv8tion.jda.api.entities.Message Message} into
     * an Integer.
     * 
     * @param position The position to convert.
     * 
     * @return The Integer value or -1 if a {@link NumberFormatException} occurs.
     */
    protected int getIntArg(int position) {
        try {
            return Integer.parseInt(getArgs()[position]);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Gets a {@link net.dv8tion.jda.api.entities.Role Role} from this guild that has the same id as the
     * one provided.
     * 
     * @param position The position from the {@code args} array (contains the id or the mention).
     * 
     * @return The {@link net.dv8tion.jda.api.entities.Role Role}.
     * 
     * @throws NullPointerException If the provided id is not valid.
     * 
     * @see net.dv8tion.jda.api.entities.Guild#getRoleById(long) Guild.getRoleById
     */
    @Nullable
    protected Role retrieveMentionnedRole(int position) {
        try {
            return getGuild().getRoleById(retrieveMentionnedEntity(getMessage(), MentionType.ROLE, 0, getArgs()[position]));
        } catch (NumberFormatException e) {
            sendMessage("**" + getArgs()[position] + "** is not a role.");
            return null;
        }
    }

    /**
     * Gets a {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} from this guild that has the
     * same id as the one provided.
     * 
     * @param position The position from the {@code args} array (contains the id or the mention).
     * 
     * @return The {@link net.dv8tion.jda.api.entities.TextChannel TextChannel}.
     * 
     * @throws NullPointerException If the provided id is not valid.
     * 
     * @see net.dv8tion.jda.api.entities.Guild#getTextChannelById(long) Guild.getTextChannelById
     */
    @Nullable
    protected TextChannel retrieveMentionnedTextChannel(int position) {
        try {
            return getGuild().getTextChannelById(retrieveMentionnedEntity(getMessage(), MentionType.CHANNEL, 0, getArgs()[position]));
        } catch (NumberFormatException e) {
            sendMessage("**" + getArgs()[position] + "** is not a text channel.");
            return null;
        }
    }

    /**
     * Load the member for the specified user.
     * 
     * @param position The position from the {@code args} array (contains the id or the mention).
     * @param update   Whether JDA should perform a request even if the member is already cached to
     *                 update properties such as the name.
     * 
     * @return {@link RestAction} - Type: {@link Member}.
     * 
     * @throws NullPointerException If the provided id is not valid.
     * 
     * @see net.dv8tion.jda.api.entities.Guild#retrieveMemberById(long) Guild.retrieveMemberById
     */
    @Nullable
    protected RestAction<Member> retrieveMentionnedMember(int position, boolean update) {
        try {
            return getGuild().retrieveMemberById(retrieveMentionnedEntity(getMessage(), MentionType.USER, 0, getArgs()[position]), update);
        } catch (NumberFormatException e) {
            sendMessage("**" + getArgs()[position] + "** is not a member.");
            return null;
        }
    }

    /**
     * Retrieves an immutable list of the currently banned {@link net.dv8tion.jda.api.entities.User
     * Users}.
     * 
     * @return {@link net.dv8tion.jda.api.requests.RestAction RestAction} - Type:
     *         {@literal List<}{@link net.dv8tion.jda.api.entities.Guild.Ban Ban}{@literal >}.
     * 
     * @throws NullPointerException if the logged in account does not have the
     *                              {@link net.dv8tion.jda.api.Permission#BAN_MEMBERS BAN_MEMBERS}
     *                              permission.
     * 
     * @see net.dv8tion.jda.api.entities.Guild#retrieveBanList() Guild.retrieveBanList
     */
    @Nullable
    @CheckReturnValue
    protected RestAction<List<Ban>> retrieveBanlist() {
        try {
            return getGuild().retrieveBanList();
        } catch (InsufficientPermissionException e) {
            sendMessage("Missing permission: **" + Permission.BAN_MEMBERS.getName() + "**.");
            return null;
        }
    }

    /**
     * Retrieve (by a mention or an id) an entity into a message filtered by a
     * {@link net.dv8tion.jda.api.entities.Message.MentionType Mention} type.
     * 
     * @param mention  The {@link net.dv8tion.jda.api.entities.Message.MentionType Mention} type.
     * @param position The position of the mention.
     * @param message  The {@link net.dv8tion.jda.api.entities.Message Message}.
     * @param id       The mentioned entity id.
     * 
     * @return The mentioned entity id.
     */
    @Nonnull
    private String retrieveMentionnedEntity(Message message, MentionType mention, int position, String id) {
        return (message.getMentions(mention).isEmpty()) ? id : message.getMentions(mention).get(position).getId();
    }

    /**
     * The {@link dao.pojo.PGuild Guild}.
     * 
     * @return The {@link dao.pojo.PGuild Guild}. Possibly-null if a database/cache access error occurs.
     */
    @Nullable
    protected PGuild getPGuild() {
        return guild;
    }

    /**
     * The {@link dao.pojo.PGuild Guild}'s prefix.
     * 
     * @return The {@link dao.pojo.PGuild Guild}'s prefix. Possibly-null if a database/cache access
     *         error occurs.
     */
    @Nullable
    protected String getGuildPrefix() {
        return guild.getPrefix();
    }

    /**
     * The current JDA instance corresponding to this Event.
     * 
     * @return The corresponding JDA instance.
     */
    @Nonnull
    protected JDA getJDA() {
        return event.getJDA();
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.Guild Guild}.
     * 
     * @return The {@link net.dv8tion.jda.api.entities.Guild Guild}.
     */
    @Nonnull
    protected Guild getGuild() {
        return event.getGuild();
    }

    /**
     * The {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} for this message.
     * 
     * @return The {@link net.dv8tion.jda.api.entities.TextChannel TextChannel} for this message.
     */
    @Nonnull
    protected TextChannel getChannel() {
        return event.getChannel();
    }

    /**
     * The received {@link net.dv8tion.jda.api.entities.Message Message} object.
     * 
     * @return The received {@link net.dv8tion.jda.api.entities.Message Message} object.
     */
    @Nonnull
    protected Message getMessage() {
        return event.getMessage();
    }

    /**
     * The Author of the Message received as {@link net.dv8tion.jda.api.entities.User User} object.
     * 
     * @return The Author of the Message.
     */
    @Nonnull
    protected User getAuthor() {
        return event.getAuthor();
    }

}