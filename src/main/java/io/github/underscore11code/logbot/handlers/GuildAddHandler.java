package io.github.underscore11code.logbot.handlers;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.inject.Inject;
import io.github.underscore11code.logbot.models.GuildModel;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.UnavailableGuildJoinedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuildAddHandler extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(GuildAddHandler.class);
    @Inject private LoadingCache<String, GuildModel> guildCache;

    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        handleNewGuild(event.getGuild().getId());
    }

    @Override
    public void onUnavailableGuildJoined(@NotNull UnavailableGuildJoinedEvent event) {
        handleNewGuild(event.getGuildId());
    }

    private void handleNewGuild(String guildId) {
        guildCache.put(guildId, new GuildModel(guildId, null));
    }
}
