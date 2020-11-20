package io.github.underscore11code.logbot.models;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.CacheWriter;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

@Data @AllArgsConstructor @NoArgsConstructor
public class MessageModel {
    private static final Logger logger = LoggerFactory.getLogger(MessageModel.class);

    public MessageModel(Message message) {
        this.messageId = message.getId();
        this.userId = message.getAuthor().getId();
        this.channelId = message.getChannel().getId();
        this.guildId = message.getGuild().getId();
        this.lastContent = message.getContentRaw();
        this.isEdited = message.isEdited();
        this.lastUpdate = message.getTimeCreated().toInstant();
        this.isDeleted = false;
    }

    private String messageId;
    private String userId;
    private String channelId;
    private String guildId;

    private String lastContent;

    private Instant lastUpdate;

    boolean isEdited;
    boolean isDeleted;

    public static class Mapper implements RowMapper<MessageModel> {
        @Override
        public MessageModel map(ResultSet rs, StatementContext ctx) throws SQLException {
            return new MessageModel(
                    rs.getString("messageId"),
                    rs.getString("userId"),
                    rs.getString("channelId"),
                    rs.getString("guildId"),
                    rs.getString("lastContent"),
                    rs.getTimestamp("lastModified").toInstant(),
                    rs.getBoolean("isEdited"),
                    rs.getBoolean("isDeleted")
            );
        }
    }

    public static class Writer implements CacheWriter<String, MessageModel> {
        @Inject
        private Jdbi jdbi;

        public Writer() {
            logger.debug("init");
        }

        @Override
        public void write(@NonNull String key, @NonNull MessageModel model) {
            logger.debug("Writing {}", model.toString());
            jdbi.useHandle(handle -> handle.createUpdate("REPLACE INTO messages " +
                    "(messageId, userId, channelId, guildId, lastContent, lastModified, isEdited, isDeleted) " +
                    "VALUES (:messageId, :userId, :channelId, :guildId, :lastContent, :lastModified, :isEdited, :isDeleted)")
                    .bind("messageId", model.getMessageId())
                    .bind("userId", model.getUserId())
                    .bind("channelId", model.getChannelId())
                    .bind("guildId", model.getGuildId())
                    .bind("lastContent", model.getLastContent())
                    .bind("lastModified", model.getLastUpdate())
                    .bind("isEdited", model.isEdited())
                    .bind("isDeleted", model.isDeleted())
                    .execute());
        }

        @Override
        public void delete(@NonNull String key, @Nullable MessageModel model, @NonNull RemovalCause cause) {}
    }

    public static class Loader implements CacheLoader<String, MessageModel> {
        @Inject private Jdbi jdbi;

        @Override
        public @Nullable MessageModel load(@NonNull String key) throws Exception {
            MessageModel model;
            try (Handle handle = jdbi.open()) {
                model = handle.select("SELECT * from messages WHERE messageId = ?", key)
                        .map(new MessageModel.Mapper())
                        .findOne().get();
            }
            return model;
        }
    }
}
