package com.jasoncabot.gardenpath;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.jersey2.InstrumentedResourceMethodApplicationListener;
import com.jasoncabot.gardenpath.resources.GameResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationPath(value = "/api/*")
public class GardenPathApplication extends Application
{
    private static MetricRegistry metrics = new MetricRegistry();

    public GardenPathApplication()
    {
        super();

        final JmxReporter reporter = JmxReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start();

        final Graphite graphite = new Graphite("127.0.0.1", 2003);
        final GraphiteReporter graphiteReporter = GraphiteReporter.forRegistry(metrics)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .build(graphite);
        graphiteReporter.start(1, TimeUnit.MINUTES);
    }

    @Override
    public Set<Class<?>> getClasses()
    {
        return Stream.of(GameResource.class).collect(Collectors.toSet());
    }

    @Override
    public Set<Object> getSingletons()
    {
        return Stream.of(new InstrumentedResourceMethodApplicationListener(metrics)).collect(Collectors.toSet());
    }
}
