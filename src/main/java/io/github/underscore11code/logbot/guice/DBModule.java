package io.github.underscore11code.logbot.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.underscore11code.logbot.Bootstrap;
import org.jdbi.v3.core.Jdbi;

public class DBModule extends AbstractModule {
    protected void configure() {
        //add configuration logic here
    }

    @Provides @Singleton
    public HikariDataSource getHikariDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Bootstrap.getSetting("dbUrl"));
        config.setUsername(Bootstrap.getSetting("dbUsername"));
        config.setPassword(Bootstrap.getSetting("dbPassword"));
        return new HikariDataSource(config);
    }

    @Provides @Singleton
    public Jdbi getJdbi(HikariDataSource hds) {
        return Jdbi.create(hds);
    }
}
