package io.github.underscore11code.logbot.handlers;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.inject.Inject;
import io.github.underscore11code.logbot.models.GuildModel;
import io.github.underscore11code.logbot.models.MessageModel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class MessageDeletedHandler extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MessageDeletedHandler.class);
    @Inject private LoadingCache<String, MessageModel> messageCache;
    @Inject private LoadingCache<String, GuildModel> guildCache;
    @Inject private JDA jda;

    @Override
    public void onGuildMessageDelete(@NotNull GuildMessageDeleteEvent event) {
        MessageModel model = messageCache.get(event.getMessageId());
        if (model == null) {
            logger.warn("Message {} returned a null model", event.getMessageId());
            return;
        }
        model.setDeleted(true);
        model.setLastUpdate(Instant.now());
        sendNotification(model);
        logger.info("Delete {}", model);
        messageCache.put(model.getMessageId(), model);
    }

    private void sendNotification(MessageModel newModel) {
        GuildModel guildModel = guildCache.get(newModel.getGuildId());
        if (guildModel == null) {
            logger.warn("Null Guild Model for ID {}", newModel.getGuildId());
            return;
        }
        if (guildModel.getLogChannelId().isBlank()) {
            logger.debug("Null logChannelId for {}, not sending edit notification", newModel.getGuildId());
        }
        TextChannel channel = jda.getTextChannelById(guildModel.getLogChannelId());
        if (channel == null || !channel.getGuild().getId().equals(guildModel.getGuildId())) return;
        channel.sendMessage(new EmbedBuilder()
                .setTitle("Message Deleted")
                .setDescription("From <@" + newModel.getUserId() + "> in <#" + newModel.getChannelId() + ">")
                .addField("Content:", newModel.getLastContent(), false)
                .build()).queue();
    }
}
