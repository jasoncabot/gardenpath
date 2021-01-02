package com.jasoncabot.gardenpath.core;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.contains;

public class PrivateInfoTest {
    private static final String PASSWORD_TWO = "two";
    private static final String HASH_OF_PASSWORD_TWO = "3fc4ccfe745870e2c0d99f71f30ff0656c8dedd41cc1d7d3d376b0dbe685e2f3";

    @Test
    public void shouldHashPlaintextPassword() {
        assertThat(PrivateInfo.fromPlaintext("one", PASSWORD_TWO).getHashedPassword(), is(HASH_OF_PASSWORD_TWO));
    }

    @Test
    public void shouldCreateIdenticalPrivateInformationFromPlaintextAndHash() {
        final PrivateInfo one = PrivateInfo.fromHashed("one", HASH_OF_PASSWORD_TWO);
        final PrivateInfo two = PrivateInfo.fromPlaintext("one", PASSWORD_TWO);

        assertThat(one, is(two));
    }

    @Test
    public void shouldNotIncludePlaintextPasswordInToString() {
        final PrivateInfo two = PrivateInfo.fromPlaintext("privateName", "privatePassword");
        assertThat(two.toString(), not(containsString("privatePassword")));
    }

}