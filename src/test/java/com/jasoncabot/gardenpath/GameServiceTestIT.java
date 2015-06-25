package com.jasoncabot.gardenpath;

import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.model.GameException;
import com.jasoncabot.gardenpath.persistence.InMemoryGameDao;
import com.jasoncabot.gardenpath.services.GameServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.NotFoundException;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class GameServiceTestIT
{
    private GameService service;
    private InMemoryGameDao dao;

    @Before
    public void setUp()
    {
        dao = new InMemoryGameDao();
        this.service = new GameServiceImpl(dao);
    }

    @After
    public void tearDown()
    {
        dao.clear();
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

    @Test(expected = NotFoundException.class)
    public void shouldNotBeAbleToJoinPrivateGameUsingPublicMethod()
    {
        final Game privateGame = service.createPrivateGame("one", "player_one", "game", "secret");
        service.joinPublicGame(privateGame.getId(), "two", "player_two");
    }

    @Test(expected = GameException.class)
    public void shouldNotBeAbleToCreateGameWithSameNameAndPasswordAsAnotherGameWaitingOpponent()
    {
        service.createPrivateGame("player_one_id", "player_one", "game", "secret");
        service.createPrivateGame("player_two_id", "player_two", "game", "secret");
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotBeAbleToJoinPublicGameInProgress()
    {
        final Game game = service.createPublicGame("one", "player_one");
        service.joinPublicGame(game.getId(), "two", "player_two");
        service.joinPublicGame(game.getId(), "three", "player_three");
    }

    @Test(expected = NotFoundException.class)
    public void shouldNotBeAbleToJoinPrivateGameInProgress()
    {
        service.createPrivateGame("one", "player_one", "game_name", "game_pass");
        service.joinPrivateGame("game_name", "game_pass", "two", "player_two");
        service.joinPrivateGame("game_name", "game_pass", "three", "player_three");
    }

    @Test
    public void shouldListPlayersNameInPublicGames()
    {
        service.createPublicGame("id", "Jason");
        final Collection<Game> publicGames = service.findPublicGames();
        assertThat(publicGames.iterator().next().getYou().getName()).isEqualTo("Jason");
    }

}
