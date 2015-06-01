package com.jasoncabot.gardenpath.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Fence
{
    private int startIndex = -1;
    private int endIndex = -1;

    protected Fence()
    {

    }

    public static Fence get(int id)
    {
        // convert our unique id back into start and end indexes
        int a = (id / 100) - 1;
        int b = id - ((id / 100) * 100);
        return Fence.get(a, b);
    }

    public static Fence get(int start, int end)
    {
        Fence fence = new Fence();

        // ensure start always smaller than end
        if (start < end)
        {
            fence.startIndex = start;
            fence.endIndex = end;
        }
        else
        {
            fence.startIndex = end;
            fence.endIndex = start;
        }
        return fence;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Fence fence = (Fence) o;

        return new EqualsBuilder()
                .append(startIndex, fence.startIndex)
                .append(endIndex, fence.endIndex)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(17, 37)
                .append(startIndex)
                .append(endIndex)
                .toHashCode();
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("startIndex", startIndex)
                .append("endIndex", endIndex)
                .toString();
    }
}
