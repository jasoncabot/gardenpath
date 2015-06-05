package com.jasoncabot.gardenpath;

import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.persistence.InMemoryGameDao;
import com.jasoncabot.gardenpath.services.GameServiceImpl;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.NotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

public class GameServiceTestIT
{
    private GameService service;

    @Before
    public void setUp()
    {
        this.service = new GameServiceImpl(new InMemoryGameDao());
    }

    @Test
    public void shouldBeAbleToJoinAPublicGame()
    {
        Game game1 = service.createPublicGame("one", "player_one");
        Game game2 = service.joinPublicGame(game1.getId(), "two", "player_two");
        game1 = service.findGame(game1.getId(), "one");

        assertThat(game1.getId()).isEqualTo(game2.getId());

        assertThat(game1.getMe().getName()).isEqualTo("player_one");
        assertThat(game1.getYou().getName()).isEqualTo("player_two");
        assertThat(game2.getMe().getName()).isEqualTo("player_two");
        assertThat(game2.getYou().getName()).isEqualTo("player_one");
    }

    @Test
    public void shouldBeAbleToJoinAPrivateGame()
    {
        Game game1 = service.createPrivateGame("one", "player_one", "game", "secret");
        Game game2 = service.joinPrivateGame("game", "secret", "two", "player_two");
        game1 = service.findGame(game1.getId(), "one");

        assertThat(game1.getId()).isEqualTo(game2.getId());

        assertThat(game1.getMe().getName()).isEqualTo("player_one");
        assertThat(game1.getYou().getName()).isEqualTo("player_two");
        assertThat(game2.getMe().getName()).isEqualTo("player_two");
        assertThat(game2.getYou().getName()).isEqualTo("player_one");
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotBeAbleToJoinPrivateGameWithIncorrectPassword()
    {
        service.createPrivateGame("one", "player_one", "game", "secret");
        service.joinPrivateGame("game", "secret1", "two", "player_two");
    }

}
