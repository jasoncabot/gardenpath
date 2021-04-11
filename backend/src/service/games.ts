import { GameView, Player, validDestinationsInGame, validPostsInGame } from "../../../shared/dist/index";
import { findGameById, findGameByCode, insertGame, removeWaitingGame, uuidv4, shortCode } from "./database";
import { GameId, PlayerId, GameOptions, GameModel, PlayerOptions, PlayMove, PlayFence } from "./model";

interface StartReference {
    start: number,
    end: number[],
    colour: number
};

const reference: StartReference[] = [
    { start: 76, end: [0, 1, 2, 3, 4, 5, 6, 7, 8], colour: 0x00C2FB },
    { start: 4, end: [72, 73, 74, 75, 76, 77, 78, 79, 80], colour: 0x25D3BA },
    { start: 36, end: [8, 17, 26, 35, 44, 53, 62, 71, 80], colour: 0xFF8119 },
    { start: 44, end: [0, 9, 18, 27, 36, 45, 54, 63, 72], colour: 0xAC47C7 },
];

const createGame: (player: PlayerOptions, options: GameOptions) => GameModel = (player: PlayerOptions, options: GameOptions) => {
    const ref = reference[0];
    let players: Record<PlayerId, Player> = {};
    players[player.identifier] = {
        fences: [],
        name: player.name,
        position: ref.start,
        target: ref.end,
        colour: ref.colour
    };

    const game: GameModel = {
        id: uuidv4(),
        code: shortCode(),
        currentTurn: player.identifier,
        lastMoveAt: Date.now(),
        numberOfPlayers: options.numberOfPlayers,
        players: players,
        state: "WAITING_OPPONENT",
        turnOrder: [player.identifier]
    }

    insertGame(game);

    return game;
}

const startGame: (gameId: GameId, player: PlayerId) => (GameModel) = (gameId: GameId, player: PlayerId) => {
    const game = findGameById(gameId);

    if (!game) throw new Error("No game found");

    // ensure that player is the one that created the game
    if (game.turnOrder[0] !== player) throw new Error("You can only start a game that you created");

    // check we have the right number of players
    if (Object.keys(game.players).length !== game.numberOfPlayers) throw new Error("Wrong number of players");

    if (game.state !== "WAITING_OPPONENT") throw new Error("Can only start game that is waiting for opponent");

    // All good, so move the game to in progress 
    game.state = "IN_PROGRESS";
    game.lastMoveAt = Date.now();
    game.currentTurn = game.turnOrder[0];

    removeWaitingGame(game);

    return game;
}

const joinGame: (code: string, player: PlayerOptions) => (GameModel) = (code: string, player: PlayerOptions) => {
    const game = findGameByCode(code);
    if (!game) throw new Error("Unable to find game");
    if (Object.keys(game.players).length >= game.numberOfPlayers) throw new Error("No room to join game");
    if (game.players[player.identifier]) throw new Error("Can't join game again");

    // all good, let's join
    const ref = reference[Object.keys(game.players).length];
    game.players[player.identifier] = {
        fences: [],
        name: player.name,
        position: ref.start,
        target: ref.end,
        colour: ref.colour
    }
    game.lastMoveAt = Date.now();
    game.turnOrder.push(player.identifier);

    return game;
}

const move = (gameId: GameId, move: PlayMove) => {
    const game = findGameById(gameId);
    if (!game) throw new Error("Unable to find game");
    if (game.state !== 'IN_PROGRESS') throw new Error("Can only move when game is in progress");
    if (game.currentTurn !== move.identifier) throw new Error("Can't move when it's not your turn");

    // Ensure position is a valid destination
    const view = viewGameAsUser(game, move.identifier);
    if (!view) throw new Error("Unable to view game as user");
    const validDestinations = validDestinationsInGame(view);
    if (validDestinations.indexOf(move.position) < 0) throw new Error("Invalid move destination, must be one of " + JSON.stringify(validDestinations));

    // all good, update game
    game.players[move.identifier].position = move.position;
    const nextTurnIndex = game.turnOrder.indexOf(game.currentTurn) + 1;
    game.currentTurn = nextTurnIndex < game.turnOrder.length ? game.turnOrder[nextTurnIndex] : game.turnOrder[0];
    game.lastMoveAt = Date.now();

    // If any player is at their target 
    Object.values(game.players).forEach(player => {
        if (player.target.indexOf(player.position) < 0) return;
        game.state = "GAME_OVER";
    });

    return game;
}

const MAX_FENCES = 10;

const fence = (gameId: GameId, fence: PlayFence) => {
    const game = findGameById(gameId);
    if (!game) throw new Error("Unable to find game");
    if (game.state !== 'IN_PROGRESS') throw new Error("Can only play a fence when game is in progress");
    if (game.currentTurn !== fence.identifier) throw new Error("Can't play a fence when it's not your turn");

    // Ensure player has fences
    if (game.players[fence.identifier].fences.length === MAX_FENCES) throw new Error("No more fences left to play");
    // Ensure fence is played in a valid place
    const view = viewGameAsUser(game, fence.identifier);
    if (!view) throw new Error("Unable to view game as user");
    const validEnds = validPostsInGame(fence.start, view);
    if (validEnds.indexOf(fence.end) < 0) throw new Error(`Invalid fence construction. From ${fence.start} valid endpoints are ${JSON.stringify(validEnds)} not ${fence.end}`);

    // all good, update game
    game.players[fence.identifier].fences.push({
        start: fence.start,
        end: fence.end
    });
    const nextTurnIndex = game.turnOrder.indexOf(game.currentTurn) + 1;
    game.currentTurn = nextTurnIndex < game.turnOrder.length ? game.turnOrder[nextTurnIndex] : game.turnOrder[0];
    game.lastMoveAt = Date.now();

    return game;
}

// utility

const viewGameAsUser: (game: GameModel | undefined, playerId: PlayerId | undefined) => GameView | undefined = (game: GameModel | undefined, playerId: PlayerId | undefined) => {
    if (!game) throw new Error("No game found");
    if (!playerId) throw new Error("Unable build game view");

    let view: GameView = {
        id: game.id,
        code: game.code,
        numberOfPlayers: game.numberOfPlayers,
        lastMoveAt: game.lastMoveAt,
        state: game.state,
        myTurn: playerId === game.currentTurn,
        me: game.players[playerId],
        opponents: Object.keys(game.players).filter(p => p !== playerId).map(id => game.players[id])
    }
    return view;
}

export { createGame, viewGameAsUser, joinGame, startGame, move, fence };