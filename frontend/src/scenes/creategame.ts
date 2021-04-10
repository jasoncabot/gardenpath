
import 'phaser';
import { UserController } from '../model/usercontroller';
import { ImageButton } from '../objects/button';
import { StartGameScene } from './startgame';

export default class CreateGameScene extends Phaser.Scene {
    constructor() {
        super('CreateGameScene');

        this.userController = new UserController();
    }

    userController: UserController
    startButton: ImageButton | undefined

    preload() {
        this.load.atlasXML('blueSheet', 'assets/buttons/blueSheet.png', 'assets/buttons/blueSheet.xml')
    }

    create() {
        this.startButton = new ImageButton(this, 274, 90, {
            active: 'blue_button03.png',
            rest: 'blue_button04.png',
            hover: 'blue_button02.png'
        }, "OK", () => {

            fetch(`${process.env.API_ENDPOINT}/games`, {
                "method": "POST",
                "headers": {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${this.userController.userId}`
                },
                "body": JSON.stringify({
                    "name": "Player 1",
                    "numberOfPlayers": 1
                })
            }).then(response => {
                if (!response.ok) { return response.json().then(json => { throw json.error; }); }
                return response.json();
            }).then(response => {
                this.scene.add('StartGameScene', StartGameScene, true, { id: response.id });
                this.scene.remove('CreateGameScene');
            }).catch(err => {
                console.error(err);
            });
        });

        this.add.existing(this.startButton);
    }
}

export { CreateGameScene };
