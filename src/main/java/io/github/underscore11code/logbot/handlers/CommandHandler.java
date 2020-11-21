package io.github.underscore11code.logbot.handlers;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.inject.Inject;
import io.github.underscore11code.logbot.models.GuildModel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CommandHandler extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);
    @Inject private LoadingCache<String, GuildModel> guildCache;

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        JDA jda = event.getJDA();
        if (!event.getMessage().getMentionedUsers().contains(jda.getSelfUser())) return;
        if (event.getAuthor().isBot()) return;
        List<String> commandList = Arrays.asList(event.getMessage().getContentRaw().split(" "));
        commandList = new ArrayList<>(commandList);
        commandList.remove(0);
        String reply = "";
        switch (commandList.size() >= 1 ? commandList.remove(0) : "") {
            case "log":
                if (!Objects.requireNonNull(event.getMember()).isOwner()) {
                    reply = "Only the server owner may do that!";
                    break;
                }
                logger.info("COMMAND log");
                GuildModel model = guildCache.get(event.getGuild().getId());
                if (model == null) {
                    throw new NullPointerException("model");
                }
                model.setLogChannelId(event.getChannel().getId());
                guildCache.invalidate(event.getGuild().getId());
                guildCache.put(event.getGuild().getId(), model);
                logger.debug(model.toString());
                reply = "Done! Set the log channel to " + event.getChannel().getAsMention();
                break;
            default:
                logger.info("COMMAND default");
                reply = "**LogBot**\n> " + jda.getSelfUser().getAsMention() + " log\nSet the log channel";
                break;
        }
        event.getMessage().getChannel().sendMessage(reply)
                .reference(event.getMessage())
                .queue();
    }
}
