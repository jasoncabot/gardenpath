import 'phaser';
import { GameView, Fence, validPostsInGame, validDestinationsInGame } from '../../../shared/dist/index';

// Don't expose dependency on shared code into the view

interface FenceViewModel {
    start: number,
    end: number
}

interface MoveViewModel {
    from: number,
    to: number
}

interface PlayerViewModel {
    controllable: boolean
    position: number
    colour: number
}

interface GameViewModel {
    players: PlayerViewModel[]
    fences: FenceViewModel[]
}

class GameController extends Phaser.Events.EventEmitter {

    id: string
    game: GameView | undefined

    constructor(gameId: string) {
        super();
        this.id = gameId;
    }

    fence = (start: number, end: number) => {
        if (!this.game) throw new Error("Game must have loaded before attempting to play fence");
        const fence: Fence = { start, end };
        this.game?.me.fences.push(fence);
        const fenceViewModel: FenceViewModel = fence;
        this.emit("fence", fenceViewModel);
    }

    move = (position: number) => {
        if (!this.game) throw new Error("Game must have loaded before attempting to move");
        const oldPosition = this.game.me.position;
        this.game.me.position = position;
        this.emit("move", { from: oldPosition, to: position });
    }

    validPosts: (from: number) => (number[]) = (from: number) => {
        if (!this.game) throw new Error("Game must have loaded before attempting to see which posts are valid");
        return validPostsInGame(from, this.game);
    }

    validDestinations: () => (number[]) = () => {
        if (!this.game) throw new Error("Game must have loaded before attempting to see which positions are valid");
        return validDestinationsInGame(this.game);
    }

    load = () => {
        this.game = {
            id: "1",
            me: {
                name: "Player 1",
                position: 76,
                colour: 0x00C2FB,
                fences: [
                ],
                target: [0, 1, 2, 3, 4, 5, 6, 7, 8]
            },
            opponents: [{
                name: "Player 2",
                position: 4,
                colour: 0x25D3BA,
                fences: [
                ],
                target: [72, 73, 74, 75, 76, 77, 78, 79, 80]
            }, {
                name: "Player 3",
                position: 36,
                colour: 0xFF8119,
                fences: [
                ],
                target: [8, 17, 26, 35, 44, 53, 62, 71, 80]
            }, {
                name: "Player 4",
                position: 44,
                colour: 0xAC47C7,
                fences: [
                ],
                target: [0, 9, 18, 27, 36, 45, 54, 63, 72]
            }],
            myTurn: true,
            lastMoveAt: Date.now(),
            state: "IN_PROGRESS",
        }

        this.emit("game", this.toViewModel(this.game));
    }

    toViewModel: (game: GameView) => (GameViewModel) = (game: GameView) => {
        const vm: GameViewModel = {
            players: [game.me].concat(game.opponents).map(p => {
                return {
                    colour: p.colour,
                    controllable: p === game.me,
                    position: p.position
                }
            }),
            fences: game.me.fences.concat(game.opponents.map(p => p.fences).reduce((acc, val) => acc.concat(val), []))
        };
        return vm;
    }
}

export {
    FenceViewModel,
    MoveViewModel,
    GameViewModel,
    GameController
}
