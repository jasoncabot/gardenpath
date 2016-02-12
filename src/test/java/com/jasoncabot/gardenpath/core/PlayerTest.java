package com.jasoncabot.gardenpath.core;

public class PlayerTest
{
//    @Test
//    public void shouldMovePlayersToTheirCorrectStartingPoint()
//    {
//
//        final Player one = Player.builder().setPlayerOne().build();
//        one.moveToStart();
//        assertThat(one.getPosition()).isEqualTo(4);
//        final Player two = Player.builder().build();
//        two.moveToStart();
//        assertThat(two.getPosition()).isEqualTo(76);
//    }
//
//    @Test
//    public void shouldHaveCorrectWinningPositionsForPlayer1()
//    {
//        final SoftAssertions softly = new SoftAssertions();
//        final Player.Builder builder = Player.builder().setPlayerOne();
//        for (int position = 0; position < 81; position++) {
//            if (position >= 72 && position <= 80)
//            {
//                softly.assertThat(builder.withPosition(position).build().isInWinningPosition()).as("p1 position " + position + " is winning").isTrue();
//            }
//            else
//            {
//                softly.assertThat(builder.withPosition(position).build().isInWinningPosition()).as("p1 position " + position + " is not winning").isFalse();
//            }
//        }
//        softly.assertAll();
//    }
//
//    @Test
//    public void shouldHaveCorrectWinningPositionsForPlayer2()
//    {
//        final SoftAssertions softly = new SoftAssertions();
//        final Player.Builder builder = Player.builder();
//        for (int position = 0; position < 81; position++) {
//            if (position >= 0 && position <= 8)
//            {
//                softly.assertThat(builder.withPosition(position).build().isInWinningPosition()).as("p2 position " + position + " is winning").isTrue();
//            }
//            else
//            {
//                softly.assertThat(builder.withPosition(position).build().isInWinningPosition()).as("p2 position " + position + " is not winning").isFalse();
//            }
//        }
//        softly.assertAll();
//    }
//
//    @Test
//    public void shouldMovePlayerWhenUpdatingPosition()
//    {
//        final Player player = Player.builder().withPosition(5).build();
//        player.updatePosition(50);
//        assertThat(player.getPosition()).isEqualTo(50);
//    }
//
//    @Test
//    public void shouldHaveFreeFenceWhenConstructingWithDefaultFence()
//    {
//        final Player player = Player.builder().withDefaultFences().build();
//        assertThat(player.hasFreeFence()).isTrue();
//    }
//
//    @Test
//    public void shouldHaveFreeFencesUntilTheLastIsPlayed()
//    {
//        final Fence validFence = Fence.get(8385);
//        assertThat(validFence.isValid()).isTrue();
//
//        final Player player = Player.builder().withDefaultFences().build();
//        for (int i = 0; i < player.getFences().size(); i++)
//        {
//            assertThat(player.hasFreeFence()).isTrue();
//            player.playFence(validFence);
//        }
//        assertThat(player.hasFreeFence()).isFalse();
//    }
}