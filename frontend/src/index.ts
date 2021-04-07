import 'phaser';
import { MainMenuScene } from './scenes/mainmenu';

const config = {
    type: Phaser.AUTO,
    backgroundColor: '#F3F3F3',
    width: 548,
    height: 548,
    scale: {
        mode: Phaser.Scale.FIT,
        autoCenter: Phaser.Scale.CENTER_BOTH
    },
    scene: MainMenuScene
};

const game = new Phaser.Game(config);
