import { Player, GameState } from "@shared/index";

type GameId = string;
type PlayerId = string;

interface GameOptions {
    numberOfPlayers: number
}

interface PlayerOptions {
    name: string,
    identifier: PlayerId
}

interface PlayMove {
    identifier: PlayerId,
    position: number
}

interface PlayFence {
    identifier: PlayerId,
    start: number,
    end: number
}

interface GameModel {
    id: GameId,
    numberOfPlayers: number,
    players: Record<PlayerId, Player>,
    turnOrder: PlayerId[],
    currentTurn: PlayerId,
    lastMoveAt: number,
    state: GameState
}

export {
    GameId
    , GameModel
    , GameOptions
    , PlayerId
    , PlayerOptions
    , PlayFence
    , PlayMove
}