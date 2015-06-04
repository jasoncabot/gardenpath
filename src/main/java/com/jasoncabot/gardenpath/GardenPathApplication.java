package com.jasoncabot.gardenpath;

import com.jasoncabot.gardenpath.resources.GameResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationPath(value = "/api/*")
public class GardenPathApplication extends Application
{
    @Override
    public Set<Class<?>> getClasses()
    {
        return Stream.of(GameResource.class).collect(Collectors.toSet());
    }
}
