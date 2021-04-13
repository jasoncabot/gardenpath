
import 'phaser';
import { ImageButton } from '../objects/button';
import BaseScene from './BaseScene';

export default class MainMenuScene extends BaseScene {
    static key = "MainMenuScene";

    constructor() {
        super(MainMenuScene.key);
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
            this.router.navigate("/games/new");
        });
        this.add.existing(this.startButton);

        this.joinButton = new ImageButton(this, 274, 140, {
            active: 'blue_button03.png',
            rest: 'blue_button04.png',
            hover: 'blue_button02.png'
        }, "Join Game", () => {
            this.router.navigate("/games");
        });
        this.add.existing(this.joinButton);
    }
}

export { MainMenuScene };
