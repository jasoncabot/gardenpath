
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
        this.load.atlasXML('blueSheet', '/assets/buttons/blueSheet.png', '/assets/buttons/blueSheet.xml');
    }

    create() {
        const createForm = this.add.dom(0, 0)
            .setDisplayOrigin(0, 0)
            .createFromHTML(`
                <p>
                    <label for="name">Name: </label>
                    <input type="text" id="name" name="name" value="${this.userController.name}" placeholder="Enter your name" style="font-size: 14px; width: 222px; height: 27px; padding-left: 8px;">
                </p>
                <p>
                    <label for="players">Players: </label>
                    <input type="number" min="1" max="4" id="players" name="players" value="2" placeholder="Number of players" style="font-size: 14px; width: 222px; height: 27px; padding-left: 8px;">
                </p>
            `);

        this.startButton = new ImageButton(this, 274, 490, {
            active: 'blue_button03.png',
            rest: 'blue_button04.png',
            hover: 'blue_button02.png'
        }, "OK", () => {
            // save the name in the user controller
            const name = (<HTMLInputElement>createForm.getChildByName('name')).value;
            const numPlayers = parseInt((<HTMLInputElement>createForm.getChildByName('players')).value, 10);
            this.userController.storeName(name);
            this.startGame(name, numPlayers);
        });

        this.add.existing(this.startButton);
    }

    startGame = (name: string, numPlayers: number) => {
        fetch(`${process.env.API_ENDPOINT}/games`, {
            "method": "POST",
            "headers": {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${this.userController.userId}`
            },
            "body": JSON.stringify({
                "name": name,
                "numberOfPlayers": numPlayers
            })
        }).then(response => {
            if (!response.ok) { return response.json().then(json => { throw json.error; }); }
            return response.json();
        }).then(response => {
            this.scene.add('StartGameScene', StartGameScene, true, { id: response.id, playerCount: numPlayers });
            this.scene.remove('CreateGameScene');
        }).catch(err => {
            console.error(err);
        });
    }
}

export { CreateGameScene };
