import 'phaser';
import { Fence, GameView, validDestinationsInGame, validPostsInGame } from '../../../shared/dist/index';
import { UserController } from './usercontroller';

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
    name: string
    controllable: boolean
    position: number
    colour: number
}

interface GameViewModel {
    numberOfPlayers: number
    code: string
    myTurn: boolean
    players: PlayerViewModel[]
    fences: FenceViewModel[]
}

class GameController extends Phaser.Events.EventEmitter {

    id: string
    game: GameView | undefined
    userController: UserController

    constructor(gameId: string) {
        super();

        this.userController = new UserController();
        this.id = gameId;
    }

    fence = (start: number, end: number) => {
        if (!this.game) throw new Error("Game must have loaded before attempting to play fence");
        const fence: Fence = { start, end };
        this.game?.me.fences.push(fence);
        const fenceViewModel: FenceViewModel = fence;
        this.emit("fence", fenceViewModel);

        // Submit move to server
        fetch(`${process.env.API_ENDPOINT}/games/${this.id}/fence`, {
            "method": "POST",
            "headers": {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${this.userController.userId}`
            },
            "body": JSON.stringify(fence)
        }).then(response => {
            if (!response.ok) { return response.json().then(json => { throw json.error; }); }
            return response.json();
        }).then(_response => {
            // TODO: if response disagrees with how we updated the model we probably
            // need to do something - our optimistic update has failed :(
        }).catch(err => {
            console.error(err);
        });
    }

    move = (position: number) => {
        if (!this.game) throw new Error("Game must have loaded before attempting to move");
        const oldPosition = this.game.me.position;
        this.game.me.position = position;
        this.emit("move", { from: oldPosition, to: position });

        // Submit move to server
        fetch(`${process.env.API_ENDPOINT}/games/${this.id}/move`, {
            "method": "POST",
            "headers": {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${this.userController.userId}`
            },
            "body": JSON.stringify({ position })
        }).then(response => {
            if (!response.ok) { return response.json().then(json => { throw json.error; }); }
            return response.json();
        }).then(_response => {
            // TODO: if response disagrees with how we updated the model we probably
            // need to do something - our optimistic update has failed :(
        }).catch(err => {
            console.error(err);
        });
    }

    validPosts: (from: number) => (number[]) = (from: number) => {
        if (!this.game) throw new Error("Game must have loaded before attempting to see which posts are valid");
        return validPostsInGame(from, this.game);
    }

    validDestinations: () => (Set<number>) = () => {
        if (!this.game) throw new Error("Game must have loaded before attempting to see which positions are valid");
        return validDestinationsInGame(this.game);
    }

    load = () => {
        fetch(`${process.env.API_ENDPOINT}/games/${this.id}`, {
            "method": "GET",
            "headers": {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${this.userController.userId}`
            }
        }).then(response => {
            if (!response.ok) { return response.json().then(json => { throw json.error; }); }
            return response.json();
        }).then(response => {
            this.game = response;
            this.emit("game", this.toViewModel(this.game!));
        }).catch(err => {
            console.error(err);
        });
    }

    toViewModel: (game: GameView) => (GameViewModel) = (game: GameView) => {
        // You can view a game you aren't playing, and therefore game.me may not exist
        const me = game.me || { fences: [] };
        const vm: GameViewModel = {
            numberOfPlayers: game.numberOfPlayers,
            code: game.code,
            myTurn: game.myTurn,
            players: [me].concat(game.opponents).map(p => {
                return {
                    name: p.name,
                    colour: p.colour,
                    controllable: game.myTurn && p === me,
                    position: p.position
                }
            }),
            fences: me.fences.concat(game.opponents.map(p => p.fences).reduce((acc, val) => acc.concat(val), []))
        };
        return vm;
    }
}

export {
    FenceViewModel,
    MoveViewModel,
    GameViewModel,
    GameController
};

