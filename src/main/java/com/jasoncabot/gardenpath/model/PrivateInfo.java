package com.jasoncabot.gardenpath.model;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class PrivateInfo
{
    private final String name;
    private final String hashedPassword;

    private PrivateInfo(final String name, final String hashed)
    {
        this.name = name;
        this.hashedPassword = hashed;
    }

    public static PrivateInfo fromPlaintext(String name, String plaintextPassword)
    {
        return new PrivateInfo(name, hash(plaintextPassword));
    }

    private static String hash(String plaintextPassword)
    {
        return DigestUtils.sha256Hex(plaintextPassword);
    }

    public static PrivateInfo fromHashed(String name, String hashedPassword)
    {
        return new PrivateInfo(name, hashedPassword);
    }

    public String getName()
    {
        return name;
    }

    public String getHashedPassword()
    {
        return hashedPassword;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("hashedPassword", hashedPassword)
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
                .append(this.hashedPassword, rhs.hashedPassword)
                .isEquals();
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder()
                .append(name)
                .append(hashedPassword)
                .toHashCode();
    }
}
