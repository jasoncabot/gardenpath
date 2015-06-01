package com.jasoncabot.gardenpath.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PrivateInfo
{
    private final String name;
    private final String password;

    public PrivateInfo(final String name, final String password)
    {
        this.name = name;
        this.password = password;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("password", password)
                .toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (obj == this)
        {
            return true;
        }
        if (obj.getClass() != getClass())
        {
            return false;
        }
        PrivateInfo rhs = (PrivateInfo) obj;
        return new EqualsBuilder()
                .append(this.name, rhs.name)
                .append(this.password, rhs.password)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(name)
                .append(password)
                .toHashCode();
    }
}
