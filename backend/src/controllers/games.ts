import { IRouter, Request, Response } from "express";
import { findGameById } from "service/database";
import { createGame, startGame, joinGame, viewGameAsUser, move, fence } from "service/games";
import { requireUser } from "./auth";

const registerRoutes = (router: IRouter) => {

    // Create Game
    // POST /games
    router.post("/games", requireUser, (request: Request, response: Response) => {
        const numPlayers = parseInt(request.body.numberOfPlayers, 10) || 2;
        try {
            if ([2, 3, 4].indexOf(numPlayers) < 0) throw new Error("numberOfPlayers must be 2, 3 or 4");
            const game = createGame({ name: validatedName(request.body.name), identifier: request.user! }, { numberOfPlayers: numPlayers });
            const view = viewGameAsUser(game, request.user);
            response.status(201).json(view);
        } catch (error) {
            response.status(500).json({ error: error.message });
        }
    })

    // View Game
    // GET /games/10
    router.get("/games/:id", requireUser, (request: Request, response: Response) => {
        // find game from database
        try {
            const game = findGameById(request.params.id);
            const view = viewGameAsUser(game, request.user);
            response.status(200).json(view)
        } catch (error) {
            response.status(404).json({ error: error.message });
        }
    });

    // Start Game
    // POST /games/10/turn
    router.post("/games/:id/turn", requireUser, (request: Request, response: Response) => {
        try {
            const game = startGame(request.params.id, request.user!);
            const view = viewGameAsUser(game, request.user);
            response.status(200).json(view)
        } catch (error) {
            response.status(500).json({ error: error.message });
        }
    });

    // Join Game
    // POST /games/10/players
    router.post("/games/:id/players", requireUser, (request: Request, response: Response) => {
        try {
            const game = joinGame(request.params.id, { name: validatedName(request.body.name), identifier: request.user! })
            const view = viewGameAsUser(game, request.user);
            response.status(200).json(view)
        } catch (error) {
            response.status(500).json({ error: error.message });
        }
    });

    // Move Player
    // POST /games/10/move
    router.post("/games/:id/move", requireUser, (request: Request, response: Response) => {
        try {
            const game = move(request.params.id, {
                identifier: request.user!,
                position: request.body.position
            });
            const view = viewGameAsUser(game, request.user);

            response.status(200).json(view)
        } catch (error) {
            response.status(500).json({ error: error.message });
        }
    });

    // Play Fence
    // POST /games/10/fence
    router.post("/games/:id/fence", requireUser, (request: Request, response: Response) => {
        try {
            const game = fence(request.params.id, {
                identifier: request.user!,
                start: request.body.start,
                end: request.body.end
            });
            const view = viewGameAsUser(game, request.user);

            response.status(200).json(view)
        } catch (error) {
            response.status(500).json({ error: error.message });
        }
    });
}

const validatedName: (name: string) => (string) = (name: string) => {
    if (name.length === 0 || name.length > 40) throw new Error("Invalid name");
    return name;
}

export { registerRoutes };
