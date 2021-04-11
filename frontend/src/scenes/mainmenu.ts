
import 'phaser';
import { ImageButton } from '../objects/button';
import { CreateGameScene } from './creategame';
import { JoinGameScene } from './joingame';

export default class MainMenuScene extends Phaser.Scene {
    constructor() {
        super('MainMenuScene');
    }

    startButton: ImageButton | undefined
    joinButton: ImageButton | undefined

    preload() {
        this.load.atlasXML('blueSheet', 'assets/buttons/blueSheet.png', 'assets/buttons/blueSheet.xml')
    }

    create() {
        this.startButton = new ImageButton(this, 274, 90, {
            active: 'blue_button03.png',
            rest: 'blue_button04.png',
            hover: 'blue_button02.png'
        }, "Create Game", () => {
            this.scene.add('CreateGameScene', CreateGameScene, true);
            this.scene.remove('MainMenuScene');
        });
        this.add.existing(this.startButton);

        this.joinButton = new ImageButton(this, 274, 140, {
            active: 'blue_button03.png',
            rest: 'blue_button04.png',
            hover: 'blue_button02.png'
        }, "Join Game", () => {
            this.scene.add('JoinGameScene', JoinGameScene, true);
            this.scene.remove('MainMenuScene');
        });
        this.add.existing(this.joinButton);
    }


}

export { MainMenuScene };