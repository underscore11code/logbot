package io.github.underscore11code.logbot.models;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.CacheWriter;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.google.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

@Data @AllArgsConstructor @NoArgsConstructor
public class GuildModel {
    private static final Logger logger = LoggerFactory.getLogger(GuildModel.class);
    private String guildId;
    private String logChannelId;

    public static class Mapper implements RowMapper<GuildModel> {
        @Override
        public GuildModel map(ResultSet rs, StatementContext ctx) throws SQLException {
            return new GuildModel(
                    rs.getString("guildId"),
                    rs.getString("logChannelId")
            );
        }
    }

    public static class Writer implements CacheWriter<String, GuildModel> {
        @Inject private Jdbi jdbi;

        public Writer() {
            logger.debug("init");
        }

        @Override
        public void write(@NonNull String key, @NonNull GuildModel model) {
            logger.debug("Writing {}", model.toString());
            jdbi.useHandle(handle -> handle.createUpdate("REPLACE INTO guilds " +
                    "(guildId, logChannelId) " +
                    "VALUES (:guildId, :logChannelId)")
                    .bind("guildId", model.getGuildId())
                    .bind("logChannelId", model.getLogChannelId())
                    .execute());
        }

        @Override
        public void delete(@NonNull String key, @Nullable GuildModel model, @NonNull RemovalCause cause) {}
    }

    public static class Loader implements CacheLoader<String, GuildModel> {
        @Inject private Jdbi jdbi;

        @Override
        public @Nullable GuildModel load(@NonNull String key) throws Exception {
            GuildModel model;
            try (Handle handle = jdbi.open()) {
                model = handle.select("SELECT * from guilds WHERE guildId = ?", key)
                        .map(new GuildModel.Mapper())
                        .findOne().get();
            }
            return model;
        }
    }
}
