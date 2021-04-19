import { GameState, mulberry32, predictNextMove } from '../src/ai';
import { Fence, initialConfiguration, Player } from '../src/index';
import { expect, test } from '@jest/globals';

test('should predict next move in game', () => {
    const game: GameState = {
        currentPlayerIndex: 0,
        players: [
            playerOne(initialConfiguration[0].start, []),
            playerTwo(initialConfiguration[1].start, []),
            playerThree(initialConfiguration[2].start, []),
            playerFour(initialConfiguration[3].start, [])
        ]
    };
    const nextMove = predictNextMove(mulberry32(1337), game);
    expect(nextMove.type).toBe("MOVE");
    expect(nextMove.result).toEqual(67);
});

const buildPlayer: (index: number, position: number, fences: Fence[]) => Player = (index, position, fences) => {
    return {
        colour: initialConfiguration[index].colour,
        fences: fences,
        name: 'Player ' + (index + 1),
        position: position,
        target: initialConfiguration[index].end
    }
}

const playerOne: (position: number, fences: Fence[]) => Player = (position, fences) => {
    return buildPlayer(0, position, fences);
}

const playerTwo: (position: number, fences: Fence[]) => Player = (position, fences) => {
    return buildPlayer(1, position, fences);
}

const playerThree: (position: number, fences: Fence[]) => Player = (position, fences) => {
    return buildPlayer(2, position, fences);
}

const playerFour: (position: number, fences: Fence[]) => Player = (position, fences) => {
    return buildPlayer(3, position, fences);
}
