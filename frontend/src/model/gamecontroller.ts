import 'phaser';
import { GameView, Fence } from '../../../shared/dist/index';

interface FenceEvent {
    start: number,
    end: number
}

interface MoveEvent {
    from: number,
    to: number
}

class GameController extends Phaser.Events.EventEmitter {

    fence = (game: GameView, fence: Fence) => {
        game.me.fences.push(fence);
        this.emit("fence", { start: fence.start, end: fence.end });
    }

    move = (game: GameView, position: number) => {
        const oldPosition = game.me.position;
        game.me.position = position;
        this.emit("move", { from: oldPosition, to: position });
    }
}

export {
    FenceEvent,
    MoveEvent,
    GameController
}