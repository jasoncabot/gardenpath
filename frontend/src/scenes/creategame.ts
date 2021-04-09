
import 'phaser';
import { ImageButton } from '../objects/button';
import { StartGameScene } from './startgame';

export default class CreateGameScene extends Phaser.Scene {
    constructor() {
        super('CreateGameScene');
    }

    startButton: ImageButton | undefined

    preload() {
        this.load.atlasXML('blueSheet', 'assets/buttons/blueSheet.png', 'assets/buttons/blueSheet.xml')
    }

    create() {
        this.startButton = new ImageButton(this, 205, 90, {
            active: 'blue_button03.png',
            rest: 'blue_button04.png',
            hover: 'blue_button02.png'
        }, () => {
            this.scene.add('StartGameScene', StartGameScene, true);
            this.scene.remove('CreateGameScene');
        });

        this.add.existing(this.startButton);
    }


}

export { CreateGameScene };