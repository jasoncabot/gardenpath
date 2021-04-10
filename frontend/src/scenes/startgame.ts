
import 'phaser';
import { GameController } from '../model/gamecontroller';
import { UserController } from '../model/usercontroller';
import { ImageButton } from '../objects/button';
import { GameScene } from './game';

export default class StartGameScene extends Phaser.Scene {
    constructor() {
        super('StartGameScene');
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
        this.startButton = new ImageButton(this, 274, 90, {
            active: 'blue_button03.png',
            rest: 'blue_button04.png',
            hover: 'blue_button02.png'
        }, "Start Game", () => {

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
                this.scene.add('GameScene', GameScene, true, { id: response.id });
                this.scene.remove('StartGameScene');
            }).catch(err => {
                console.error(err);
            });
        });

        this.add.existing(this.startButton);

    }


}

export { StartGameScene };