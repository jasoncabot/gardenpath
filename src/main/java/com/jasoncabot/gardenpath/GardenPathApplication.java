package com.jasoncabot.gardenpath;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.MetricSet;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.jersey2.InstrumentedResourceMethodApplicationListener;
import com.codahale.metrics.jvm.BufferPoolMetricSet;
import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.jasoncabot.gardenpath.resources.GameResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.lang.management.ManagementFactory;
import java.util.Map;
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

        registerAll("gc", new GarbageCollectorMetricSet(), metrics);
        registerAll("buffers", new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()), metrics);
        registerAll("memory", new MemoryUsageGaugeSet(), metrics);
        registerAll("threads", new ThreadStatesGaugeSet(), metrics);

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

    private void registerAll(String prefix, MetricSet metricSet, MetricRegistry registry)
    {
        for (Map.Entry<String, Metric> entry : metricSet.getMetrics().entrySet())
        {
            if (entry.getValue() instanceof MetricSet)
            {
                registerAll(prefix + "." + entry.getKey(), (MetricSet) entry.getValue(), registry);
            }
            else
            {
                registry.register(prefix + "." + entry.getKey(), entry.getValue());
            }
        }
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
