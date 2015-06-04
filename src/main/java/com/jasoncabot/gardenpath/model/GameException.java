package com.jasoncabot.gardenpath.model;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.HashMap;

public class GameException extends WebApplicationException
{
    public GameException()
    {
        super();
    }

    public GameException(String message)
    {
        super(message);
    }

    public GameException(String message, Throwable cause)
    {
        super(message, cause);
    }

    @Override
    public Response getResponse()
    {
        final HashMap<String, String> data = new HashMap<>();
        data.put("message", getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(data).build();
    }
}
