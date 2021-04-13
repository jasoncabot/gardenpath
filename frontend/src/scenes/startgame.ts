
import 'phaser';
import { GameController, GameViewModel } from '../model/gamecontroller';
import { UserController } from '../model/usercontroller';
import { ImageButton } from '../objects/button';
import BaseScene from './BaseScene';

export default class StartGameScene extends BaseScene {
    static key = "StartGameScene";

    constructor() {
        super(StartGameScene.key);
        this.userController = new UserController();
    }

    gameController: GameController | undefined
    userController: UserController
    startButton: ImageButton | undefined

    init(data: any) {
        this.gameController = new GameController(data.id);
    }

    preload() {
    }

    create() {
        this.startButton = new ImageButton(this, 274, 490, {
            active: 'blue_button03.png',
            rest: 'blue_button04.png',
            hover: 'blue_button02.png'
        }, "Start Game", () => {
            this.startGame();
        });

        this.add.existing(this.startButton);

        this.gameController?.on('game', this.onGameUpdated);
        this.gameController?.load();
    }

    destroy() {
        this.gameController?.off('game');
    }

    onGameUpdated: (game: GameViewModel) => (void) = (game: GameViewModel) => {
        this.add.text(8, 0, `Code: ${game.code}`, { color: '#1F2F3F' }).setOrigin(0, 0);

        for (let index = 0; index < game.numberOfPlayers; index++) {
            const y = 24 + 22 + (index * 44) + (index * 8);
            const player = game.players[index] || { name: "Waiting ...", colour: 0x1ff1f1 };

            this.add.circle(30, y, 22, player.colour);
            this.add.text(60, y, player.name, { color: '#1F2F3F' }).setOrigin(0, 0.5);
        }
    }

    startGame: () => (void) = () => {
        fetch(`${process.env.API_ENDPOINT}/games/${this.gameController!.id}/turn`, {
            "method": "POST",
            "headers": {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${this.userController.userId}`
            }
        }).then(response => {
            if (!response.ok) { return response.json().then(json => { throw json.error; }); }
            return response.json();
        }).then(response => {
            this.router.navigate(`/games/${response.id}`);
        }).catch(err => {
            console.error(err);
        });
    }


}

export { StartGameScene };
