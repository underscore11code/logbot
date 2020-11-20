package io.github.underscore11code.logbot.handlers;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.inject.Inject;
import io.github.underscore11code.logbot.models.GuildModel;
import io.github.underscore11code.logbot.models.MessageModel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MessageEditedHandler extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MessageEditedHandler.class);
    @Inject private LoadingCache<String, MessageModel> messageCache;
    @Inject private LoadingCache<String, GuildModel> guildCache;
    @Inject private JDA jda;

    @Override
    public void onGuildMessageUpdate(@NotNull GuildMessageUpdateEvent event) {
        MessageModel model = messageCache.get(event.getMessage().getId());
        if (model == null) {
            logger.warn("Message {} returned a null model", event.getMessage().getId());
            return;
        }
        model.setEdited(true);
        String lastContent = model.getLastContent();
        model.setLastContent(event.getMessage().getContentRaw());
        model.setLastUpdate(Objects.requireNonNull(event.getMessage().getTimeEdited()).toInstant());
        sendNotification(lastContent, model);
        logger.info("Edit {}", model);
        messageCache.invalidate(model.getMessageId());
        messageCache.put(model.getMessageId(), model);
    }

    private void sendNotification(String lastContent, MessageModel newModel) {
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
                .setTitle("Message Edited")
                .setDescription("From <@" + newModel.getUserId() + "> in <#" + newModel.getChannelId() + ">\n" +
                        "[Jump Link](https://discord.com/channels/" + newModel.getGuildId() +
                        "/" + newModel.getChannelId() +
                        "/" + newModel.getMessageId() + ")")
                .addField("Old Content:", lastContent, false)
                .addField("New Content:", newModel.getLastContent(), false)
                .build()).queue();
    }
}
