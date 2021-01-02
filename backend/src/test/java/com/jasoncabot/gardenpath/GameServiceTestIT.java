package com.jasoncabot.gardenpath;


import com.jasoncabot.gardenpath.api.GameService;
import com.jasoncabot.gardenpath.api.GameServiceImpl;
import com.jasoncabot.gardenpath.core.Game;
import com.jasoncabot.gardenpath.core.GameException;
import com.jasoncabot.gardenpath.db.GameDao;
import com.jasoncabot.gardenpath.db.GameMapper;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import javax.ws.rs.NotFoundException;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class GameServiceTestIT extends IntegrationTest
{
    private GameService service;

    @Test
    public void shouldBeAbleToJoinAPublicGame()
    {
        Game game1 = service.createPublicGame("one", "player_one");
        Game game2 = service.joinPublicGame(game1.getId(), "two", "player_two");
        game1 = service.findGame(game1.getId(), "one");

        assertThat(game1.getId(), is(game2.getId()));

        assertThat(game1.getMe().getName(), is("player_one"));
        assertThat(game1.getYou().getName(), is("player_two"));
        assertThat(game2.getMe().getName(), is("player_two"));
        assertThat(game2.getYou().getName(), is("player_one"));
    }

    @Test
    public void shouldBeAbleToJoinAPrivateGame()
    {
        Game game1 = service.createPrivateGame("one", "player_one", "game", "secret");
        Game game2 = service.joinPrivateGame("game", "secret", "two", "player_two");
        game1 = service.findGame(game1.getId(), "one");

        assertThat(game1.getId(), is(game2.getId()));

        assertThat(game1.getMe().getName(), is("player_one"));
        assertThat(game1.getYou().getName(), is("player_two"));
        assertThat(game2.getMe().getName(), is("player_two"));
        assertThat(game2.getYou().getName(), is("player_one"));
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
        assertThat(publicGames.iterator().next().getYou().getName(), is("Jason"));
    }

    @Override
    protected void setUpDataAccessObjects(final DBI dbi) {
        dbi.registerMapper(new GameMapper());
        final GameDao gameDAO = dbi.onDemand(GameDao.class);
        service = new GameServiceImpl(gameDAO);
    }
}
