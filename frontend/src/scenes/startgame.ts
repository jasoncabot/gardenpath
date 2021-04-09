
import 'phaser';
import { ImageButton } from '../objects/button';
import { GameScene } from './game';

export default class StartGameScene extends Phaser.Scene {
    constructor() {
        super('StartGameScene');
    }

    startButton: ImageButton | undefined

    preload() {
    }

    create() {
        this.startButton = new ImageButton(this, 274, 90, {
            active: 'blue_button03.png',
            rest: 'blue_button04.png',
            hover: 'blue_button02.png'
        }, "Game", () => {
            this.scene.add('GameScene', GameScene, true);
            this.scene.remove('StartGameScene');
        });

        this.add.existing(this.startButton);

    }


}

export { StartGameScene };