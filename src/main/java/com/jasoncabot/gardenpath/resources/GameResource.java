package com.jasoncabot.gardenpath.resources;

import com.jasoncabot.gardenpath.model.Game;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("/games")
@Produces(MediaType.APPLICATION_JSON)
public class GameResource
{
    @GET
    public Collection<Game> listGames()
    {
        final List<Game> games = new ArrayList<Game>();
        games.add(new Game());
        return games;
    }
}
