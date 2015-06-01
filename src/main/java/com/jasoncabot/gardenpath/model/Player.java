package com.jasoncabot.gardenpath.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Collection;

public class Player
{
    private static final int NUM_FENCES = 10;
    private String identifier;
    private String name;
    private boolean isPlayerOne;
    private int position;

    private Collection<Fence> fences;

    public Player(final String id, final String name)
    {
        this.identifier = id;
        this.name = name;
        this.position = -1;
        this.isPlayerOne = false;
        this.fences = new ArrayList<>(NUM_FENCES);
        for (int i = 0; i < NUM_FENCES; i++)
        {
            fences.add(new Fence());
        }
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public String getName()
    {
        return name;
    }

    public boolean isPlayerOne()
    {
        return isPlayerOne;
    }

    public int getPosition()
    {
        return position;
    }

    public Collection<Fence> getFences()
    {
        return fences;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("identifier", identifier)
                .append("name", name)
                .append("isPlayerOne", isPlayerOne)
                .append("position", position)
                .append("fences", fences)
                .toString();
    }
}
