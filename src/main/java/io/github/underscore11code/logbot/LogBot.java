package io.github.underscore11code.logbot;

import com.google.inject.Inject;
import io.github.underscore11code.logbot.handlers.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jdbi.v3.core.Jdbi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class LogBot {
    private static final Logger logger = LoggerFactory.getLogger(LogBot.class);
    @Inject private JDA jda;
    @Inject private Jdbi jdbi;

    public void startup() {
        Set<Class<? extends ListenerAdapter>> listeners = Set.of(
                MessageReceivedHandler.class,
                MessageEditedHandler.class,
                MessageDeletedHandler.class,
                GuildAddHandler.class,
                CommandHandler.class
        );
        listeners.forEach(aClass -> {
            jda.addEventListener(Bootstrap.getInjector().getInstance(aClass));
        });

        jdbi.useHandle(handle -> handle.execute("CREATE TABLE if not exists messages (" +
                "messageId varchar(20) PRIMARY KEY ," +
                "userId varchar(20)," +
                "channelId varchar(20)," +
                "guildId varchar(20)," +
                "lastContent text(20000)," +
                "lastModified timestamp," +
                "isEdited bool," +
                "isDeleted bool" +
                ")"));
        jdbi.useHandle(handle -> handle.execute("CREATE TABLE if not exists guilds (" +
                "guildId varchar(20) PRIMARY KEY , " +
                "logChannelId varchar(20)" +
                ")"));
    }

    public void shutdown() {
        logger.info("Shutting down...");
        jda.shutdown();
    }
}
