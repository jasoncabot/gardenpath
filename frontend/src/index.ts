import 'phaser';
import { MainMenuScene } from './scenes/mainmenu';

const config = {
    type: Phaser.AUTO,
    pixelArt: false,
    backgroundColor: '#F0F0F0',
    width: 548,
    height: 548,
    parent: 'root',
    resolution: 2,
    scale: {
        mode: Phaser.Scale.FIT,
        autoCenter: Phaser.Scale.CENTER_HORIZONTALLY
    },
    dom: {
        createContainer: true
    },
    scene: MainMenuScene
};

const game = new Phaser.Game(config);
