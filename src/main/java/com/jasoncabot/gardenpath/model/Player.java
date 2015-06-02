package com.jasoncabot.gardenpath.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jasoncabot.gardenpath.persistence.GameMemento;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class Player
{
    private static final int NUM_FENCES = 10;
    private String identifier;
    private String name;
    private boolean isPlayerOne;
    private int position;

    private Collection<Fence> fences;

    public static Optional<Player> fromMemento(final GameMemento memento, final boolean p1)
    {
        final Player player = new Player();
        player.fences = new ArrayList<>(NUM_FENCES);
        player.isPlayerOne = p1;
        if (p1)
        {
            player.identifier = memento.getPlayer1Id();
            player.name = memento.getPlayer1Name();
            player.position = memento.getPlayer1Position();
            player.fences.add(Fence.get(memento.getPlayer1Fence1()));
            player.fences.add(Fence.get(memento.getPlayer1Fence2()));
            player.fences.add(Fence.get(memento.getPlayer1Fence3()));
            player.fences.add(Fence.get(memento.getPlayer1Fence4()));
            player.fences.add(Fence.get(memento.getPlayer1Fence5()));
            player.fences.add(Fence.get(memento.getPlayer1Fence6()));
            player.fences.add(Fence.get(memento.getPlayer1Fence7()));
            player.fences.add(Fence.get(memento.getPlayer1Fence8()));
            player.fences.add(Fence.get(memento.getPlayer1Fence9()));
            player.fences.add(Fence.get(memento.getPlayer1Fence10()));
        }
        else
        {
            player.identifier = memento.getPlayer2Id();
            player.name = memento.getPlayer2Name();
            player.position = memento.getPlayer2Position();
            player.fences.add(Fence.get(memento.getPlayer2Fence1()));
            player.fences.add(Fence.get(memento.getPlayer2Fence2()));
            player.fences.add(Fence.get(memento.getPlayer2Fence3()));
            player.fences.add(Fence.get(memento.getPlayer2Fence4()));
            player.fences.add(Fence.get(memento.getPlayer2Fence5()));
            player.fences.add(Fence.get(memento.getPlayer2Fence6()));
            player.fences.add(Fence.get(memento.getPlayer2Fence7()));
            player.fences.add(Fence.get(memento.getPlayer2Fence8()));
            player.fences.add(Fence.get(memento.getPlayer2Fence9()));
            player.fences.add(Fence.get(memento.getPlayer2Fence10()));
        }

        if (isBlank(player.identifier))
        {
            return Optional.empty();
        }
        return Optional.of(player);
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
