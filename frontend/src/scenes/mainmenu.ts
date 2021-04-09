
import 'phaser';
import { ImageButton } from '../objects/button';
import { CreateGameScene } from './creategame';

export default class MainMenuScene extends Phaser.Scene {
    constructor() {
        super('MainMenuScene');
    }

    startButton: ImageButton | undefined

    preload() {
        this.load.atlasXML('blueSheet', 'assets/buttons/blueSheet.png', 'assets/buttons/blueSheet.xml')
    }

    create() {
        this.startButton = new ImageButton(this, 274, 90, {
            active: 'blue_button03.png',
            rest: 'blue_button04.png',
            hover: 'blue_button02.png'
        }, "Create", () => {
            this.scene.add('CreateGameScene', CreateGameScene, true);
            this.scene.remove('MainMenuScene');
        });

        this.add.existing(this.startButton);
    }


}

export { MainMenuScene };