import { GameId, GameModel } from "./model";

const games: Record<GameId, GameModel> = {};

const uuidv4: () => string = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

const findGameById: (identifier: GameId) => (GameModel | undefined) = (identifier: GameId) => {
    const game = games[identifier];
    if (!game) throw new Error("Unable to find game");
    return game;
}

const insertGame: (game: GameModel) => (void) = (game: GameModel) => {
    games[game.id] = game;
}

export { findGameById, uuidv4, insertGame };