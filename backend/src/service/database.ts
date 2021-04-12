import { GameId, GameModel } from "./model";

const games: Record<GameId, GameModel> = {};
const waitingGames: Record<string, GameModel> = {};

const uuidv4: () => string = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

const shortCode: () => string = () => {
    let result = "";
    const available = "ABCDEFGHJKMNOPQRSTUVWXYZ023456789";
    for (let index = 0; index < 4; index++) {
        result += available.charAt(Math.floor(Math.random() * available.length))
    }
    return result;
}

const findGameById: (identifier: GameId) => (GameModel | undefined) = (identifier: GameId) => {
    const game = games[identifier];
    if (!game) throw new Error("Unable to find game");
    return game;
}

const findGameByCode: (code: string) => (GameModel | undefined) = (code: string) => {
    const game = waitingGames[code];
    if (!game) throw new Error("Unable to find game");
    return game;
}

const insertGame: (game: GameModel) => (void) = (game: GameModel) => {
    games[game.id] = game;
    waitingGames[game.code] = game;
}

const removeWaitingGame: (game: GameModel) => (void) = (game: GameModel) => {
    delete waitingGames[game.code];
}

const removeStaleGames: () => (void) = () => {
    // Find all games where the last move was a long time ago and remove them
    const now = Date.now();
    const keys = Object.keys(games);
    keys.forEach(key => {
        const game = games[key];
        const sinceLastMove = (now - game.lastMoveAt);
        if (sinceLastMove >= 24 * 60 * 60 * 1000) {
            console.log(`Removing old game with id ${game.id}`);
            delete games[key];
            delete waitingGames[game.code];
        }
    });
}

export {
    findGameByCode,
    findGameById,
    insertGame,
    removeStaleGames,
    removeWaitingGame,
    shortCode,
    uuidv4,
};