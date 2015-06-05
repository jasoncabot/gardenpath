package com.jasoncabot.gardenpath.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PrivateInfoTest
{
    private static final String PASSWORD_TWO = "two";
    private static final String HASH_OF_PASSWORD_TWO = "3fc4ccfe745870e2c0d99f71f30ff0656c8dedd41cc1d7d3d376b0dbe685e2f3";

    @Test
    public void shouldHashPlaintextPassword()
    {
        assertThat(PrivateInfo.fromPlaintext("one", PASSWORD_TWO).getHashedPassword()).isEqualTo(HASH_OF_PASSWORD_TWO);
    }

    @Test
    public void shouldCreateIdenticalPrivateInformationFromPlaintextAndHash()
    {
        final PrivateInfo one = PrivateInfo.fromHashed("one", HASH_OF_PASSWORD_TWO);
        final PrivateInfo two = PrivateInfo.fromPlaintext("one", PASSWORD_TWO);

        assertThat(one).isEqualTo(two);
    }

    @Test
    public void shouldNotIncludePlaintextPasswordInToString()
    {
        final PrivateInfo two = PrivateInfo.fromPlaintext("privateName", "privatePassword");
        assertThat(two.toString()).doesNotContain("privatePassword");
    }

}