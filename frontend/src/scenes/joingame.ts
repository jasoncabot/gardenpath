
import 'phaser';
import { UserController } from '../model/usercontroller';
import { ImageButton } from '../objects/button';
import BaseScene from './BaseScene';

export default class JoinGameScene extends BaseScene {
    static key = "JoinGameScene";

    constructor() {
        super(JoinGameScene.key);

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
                    <label for="code">Code: </label>
                    <input type="text" id="code" name="code" placeholder="Game code (e.g ABCD)" maxlength="4" size="4" style="font-size: 14px; width: 222px; height: 27px; padding-left: 8px;">
                </p>
            `);

        this.startButton = new ImageButton(this, 274, 490, {
            active: 'blue_button03.png',
            rest: 'blue_button04.png',
            hover: 'blue_button02.png'
        }, "OK", () => {
            // save the name in the user controller
            const name = (<HTMLInputElement>createForm.getChildByName('name')).value;
            const code = ((<HTMLInputElement>createForm.getChildByName('code')).value || '').toUpperCase();
            this.userController.storeName(name);
            this.startGame(name, code);
        });

        this.add.existing(this.startButton);
    }

    startGame = (name: string, code: string) => {
        fetch(`${process.env.API_ENDPOINT}/games/${code}/players`, {
            "method": "POST",
            "headers": {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${this.userController.userId}`
            },
            "body": JSON.stringify({
                "name": name
            })
        }).then(response => {
            if (!response.ok) { return response.json().then(json => { throw json.error; }); }
            return response.json();
        }).then(response => {
            this.router.navigate(`/games/${response.id}/players`);
        }).catch(err => {
            console.error(err);
        });
    }
}

export { JoinGameScene };
