package io.github.underscore11code.logbot.handlers;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.inject.Inject;
import io.github.underscore11code.logbot.models.MessageModel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jdbi.v3.core.Jdbi;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageReceivedHandler extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MessageReceivedHandler.class);
    @Inject LoadingCache<String, MessageModel> messageCache;
    @Inject Jdbi jdbi;

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        MessageModel model = new MessageModel(event.getMessage());
        logger.info("Receive {}", model);
        messageCache.put(model.getMessageId(), model);
    }
}
