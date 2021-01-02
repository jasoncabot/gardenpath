package com.jasoncabot.gardenpath;

import com.jasoncabot.gardenpath.api.GameServiceImpl;
import com.jasoncabot.gardenpath.db.GameDao;
import com.jasoncabot.gardenpath.db.GameMapper;
import com.jasoncabot.gardenpath.resources.GameResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.java8.jdbi.OptionalContainerFactory;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

public class GardenpathApplication extends Application<GardenpathConfiguration>
{

    public static void main(final String[] args) throws Exception
    {
        new GardenpathApplication().run(args);
    }

    @Override
    public String getName()
    {
        return "Gardenpath";
    }

    @Override
    public void initialize(final Bootstrap<GardenpathConfiguration> bootstrap)
    {
        bootstrap.addBundle(new AssetsBundle("/assets/", "/", "index.html"));
        bootstrap.addBundle(new MigrationsBundle<GardenpathConfiguration>()
        {
            @Override
            public DataSourceFactory getDataSourceFactory(final GardenpathConfiguration configuration)
            {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(new DBIExceptionsBundle());
    }

    @Override
    public void run(final GardenpathConfiguration configuration,
                    final Environment environment)
    {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "db");
        jdbi.registerContainerFactory(new OptionalContainerFactory());
        jdbi.registerMapper(new GameMapper());
        final GameDao gameDAO = jdbi.onDemand(GameDao.class);
        environment.jersey().register(new GameResource(new GameServiceImpl(gameDAO)));
    }
}
