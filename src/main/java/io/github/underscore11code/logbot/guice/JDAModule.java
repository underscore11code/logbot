package io.github.underscore11code.logbot.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.underscore11code.logbot.Bootstrap;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

public class JDAModule extends AbstractModule {
    private static final Logger logger = LoggerFactory.getLogger(JDAModule.class);
    protected void configure() {
        //add configuration logic here
    }

    @Provides @Singleton
    public JDA getJda() throws LoginException {
        return JDABuilder.createDefault(Bootstrap.getSetting("token"))
                .setActivity(Activity.playing("@ for help!"))
                .build();
    }
}
