package com.jasoncabot.gardenpath.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Player
{

    private static final int NUM_FENCES = 10;
    private static final int[] P1_WINNING_POSITIONS = new int[] { 72, 73, 74, 75, 76, 77, 78, 79, 80 };
    private static final int[] P2_WINNING_POSITIONS = new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
    private String identifier;
    private String name;
    private boolean isPlayerOne;
    private int position;

    private List<Fence> fences;

    public Player(final String id, final String name, final int position, final List<Fence> fences) {

    }

    @JsonIgnore
    public String getIdentifier()
    {
        return identifier;
    }

    public String getName()
    {
        return name;
    }

    @JsonIgnore
    public boolean isPlayerOne()
    {
        return isPlayerOne;
    }

    public int getPosition()
    {
        return position;
    }

    public List<Fence> getFences()
    {
        return fences;
    }

    public void moveToStart()
    {
        position = isPlayerOne ? 4 : 76;
    }

    @JsonIgnore
    public boolean isInWinningPosition()
    {
        return Arrays.binarySearch(getWinningPositions(), position) >= 0;
    }

    @JsonIgnore
    public int[] getWinningPositions()
    {
        return isPlayerOne ? P1_WINNING_POSITIONS : P2_WINNING_POSITIONS;
    }

    public void updatePosition(int end)
    {
        position = end;
    }

    @JsonIgnore
    public boolean hasFreeFence()
    {
        return getFences().stream().anyMatch(f -> !f.isValid());
    }

    public void playFence(final Fence fence)
    {
        final Fence free = fences.stream().filter(f -> !f.isValid()).findFirst().get();
        fences.remove(free);
        fences.add(fence);
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
