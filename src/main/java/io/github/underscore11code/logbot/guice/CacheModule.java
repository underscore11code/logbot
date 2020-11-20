package io.github.underscore11code.logbot.guice;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.underscore11code.logbot.models.GuildModel;
import io.github.underscore11code.logbot.models.MessageModel;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.Jdbi;

import java.util.concurrent.TimeUnit;

public class CacheModule extends AbstractModule {
    protected void configure() {
        //add configuration logic here
    }

    // Message#getId : MessageModel
    @Provides @Singleton
    public LoadingCache<String, MessageModel> getMessageCache(MessageModel.Loader loader, MessageModel.Writer writer) {
        return Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .writer(writer)
                .build(loader);
    }

    // Guild#getId : GuildModel
    @Provides @Singleton
    public LoadingCache<String, GuildModel> getGuildCache(GuildModel.Loader loader, GuildModel.Writer writer) {
        return Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .writer(writer)
                .build(loader);
    }
}
