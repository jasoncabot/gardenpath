package com.jasoncabot.gardenpath;

import com.jasoncabot.gardenpath.resources.GameResource;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath(value = "/api/*")
public class GardenPathApplication extends Application
{
    @Override
    public Set<Class<?>> getClasses()
    {
        final Set<Class<?>> resourceClasses = new HashSet<Class<?>>();
        resourceClasses.add(GameResource.class);
        return resourceClasses;
    }
}
