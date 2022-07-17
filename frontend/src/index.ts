import 'phaser';
import 'bulma/css/bulma.css';
import { RouterPlugin } from './plugins/router';

const config = {
    type: Phaser.AUTO,
    pixelArt: false,
    backgroundColor: '#F0F0F0',
    width: 548,
    height: 710,
    parent: 'root',
    plugins: {
        global: [
            { key: 'RouterPlugin', plugin: RouterPlugin, start: true, data: { window }, mapping: 'router' }
        ]
    },
    scale: {
        mode: Phaser.Scale.FIT,
        autoCenter: Phaser.Scale.CENTER_HORIZONTALLY
    },
    dom: {
        createContainer: true
    }
};

const game = new Phaser.Game(config);
