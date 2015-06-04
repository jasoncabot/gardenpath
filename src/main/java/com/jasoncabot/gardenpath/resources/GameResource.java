package com.jasoncabot.gardenpath.resources;

import com.jasoncabot.gardenpath.GameService;
import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.persistence.GameDao;
import com.jasoncabot.gardenpath.services.GameServiceImpl;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Produces(MediaType.APPLICATION_JSON)
@Path("/games")
public class GameResource
{
    private final GameService service;

    public GameResource()
    {
        this.service = new GameServiceImpl(new GameDao());
    }

    public GameResource(final GameService service)
    {
        this.service = service;
    }

    @Path("{gameId}/fence")
    @POST
    public Game createFence(@PathParam("gameId") final long gameId, @QueryParam("id") final String playerId, @QueryParam("start") final Integer start,
            @QueryParam("end") final Integer end)
    {
        if (start == null || end == null)
        {
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity("start and end are mandatory when playing fence")
                    .build());
        }

        return service.addFence(gameId, playerId, start, end);
    }

    @Path("{gameId}/move")
    @POST
    public Game createMove(@PathParam("gameId") final long gameId, @QueryParam("id") final String playerId, @QueryParam("end") final Integer end)
    {
        if (end == null)
        {
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity("end is mandatory when moving")
                    .build());
        }

        return service.move(gameId, playerId, end);
    }

    @GET
    public Collection<Game> listGames()
    {
        return service.findPublicGames();
    }

    @POST
    public Game create(@QueryParam("name") final String playerName, @QueryParam("id") final String playerId, @QueryParam("gameName") final String gameName,
            @QueryParam("gamePassword") final String gamePassword)
    {
        if (isBlank(playerName) || isBlank(playerId))
        {
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity("id and name are mandatory when creating game")
                    .build());
        }

        if ((isNotBlank(gameName) && isBlank(gamePassword)) || (isBlank(gameName) && isNotBlank(gamePassword)))
        {
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity("gameName and gamePassword are both mandatory if one is specified")
                    .build());
        }

        boolean isPrivateGame = isNotBlank(gameName) && isNotBlank(gamePassword);

        if (isPrivateGame)
        {
            return service.createPrivateGame(playerId, playerName, gameName, gamePassword);
        }
        else
        {
            return service.createPublicGame(playerId, playerName);
        }
    }

    @PUT
    public Game joinGame(@QueryParam("gameId") final Long gameId, @QueryParam("name") final String playerName, @QueryParam("id") final String playerId,
            @QueryParam("gameName") final String gameName,
            @QueryParam("gamePassword") final String gamePassword)
    {
        if (isBlank(playerName) || isBlank(playerId))
        {
            throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                    .entity("id and name are mandatory when joining game")
                    .build());
        }

        boolean isPrivateGame = isNotBlank(gameName) && isNotBlank(gamePassword);

        if (isPrivateGame)
        {
            return service.joinPrivateGame(gameName, gamePassword, playerId, playerName);
        }
        else
        {
            if (gameId == null)
            {
                throw new WebApplicationException(Response.status(HttpURLConnection.HTTP_BAD_REQUEST)
                        .entity("gameId is mandatory when joining game")
                        .build());
            }
            return service.joinPublicGame(gameId, playerId, playerName);
        }
    }
}
