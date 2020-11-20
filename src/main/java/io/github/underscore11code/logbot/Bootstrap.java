package io.github.underscore11code.logbot;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.underscore11code.logbot.guice.CacheModule;
import io.github.underscore11code.logbot.guice.DBModule;
import io.github.underscore11code.logbot.guice.JDAModule;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bootstrap {
    private static final Logger logger = LoggerFactory.getLogger(Bootstrap.class);
    @Getter private static Injector injector;
    public static void main(String[] args) {
        injector = Guice.createInjector(new JDAModule(), new DBModule(), new CacheModule());
        LogBot bot = injector.getInstance(LogBot.class);
        Runtime.getRuntime().addShutdownHook(new Thread(bot::shutdown, "Shutdown"));
        bot.startup();
    }

    public static String getSetting(String name) {
        if (System.getProperty(name.toUpperCase()) != null)
            return System.getProperty(name.toUpperCase());
        if (System.getenv("logbot_" + name.toLowerCase()) != null)
            return System.getenv("logbot_" + name.toLowerCase());
        return null;
    }
}
