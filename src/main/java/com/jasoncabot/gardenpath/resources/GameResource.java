package com.jasoncabot.gardenpath.resources;

import com.jasoncabot.gardenpath.GameService;
import com.jasoncabot.gardenpath.model.Game;
import com.jasoncabot.gardenpath.persistence.GameDao;
import com.jasoncabot.gardenpath.services.GameServiceImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Produces(MediaType.APPLICATION_JSON)
@Path("/")
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

    @GET
    @Path("games")
    public Collection<Game> listGames()
    {
        return service.findPublicGames();
    }
}
