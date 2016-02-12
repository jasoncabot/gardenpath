package com.jasoncabot.gardenpath.core;

import java.util.ArrayList;
import java.util.Collection;

public class PathNode implements Comparable<PathNode>
{
    private Collection<PathNode> children;
    private boolean isVisited;
    private Integer position;

    public PathNode(final int position)
    {
        this.position = position;
        this.setVisited(false);
        this.children = new ArrayList<>();
    }

    public Collection<PathNode> getChildren()
    {
        return this.children;
    }

    public boolean isVisited()
    {
        return isVisited;
    }

    public void setVisited(final boolean isVisited)
    {
        this.isVisited = isVisited;
    }

    public int getPosition()
    {
        return position;
    }

    @Override
    public int compareTo(final PathNode other)
    {
        return Integer.compare(position, other.getPosition());
    }
}
