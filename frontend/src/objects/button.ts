interface ImageButtonStateImages {
    rest: string
    hover: string
    active: string
}

class ImageButton extends Phaser.GameObjects.Image {

    states: ImageButtonStateImages
    label: Phaser.GameObjects.Text

    constructor(scene: Phaser.Scene, x: number, y: number, states: ImageButtonStateImages, text: string, callback: () => (void)) {
        super(scene, x, y, 'blueSheet', states.rest);

        this.states = states;

        this.setInteractive({ useHandCursor: true })
            .on('pointerover', () => this.enterButtonHoverState())
            .on('pointerout', () => this.enterButtonRestState())
            .on('pointerdown', () => this.enterButtonActiveState())
            .on('pointerup', () => {
                this.enterButtonHoverState();
                callback();
            });

        this.label = scene.add.text(x, y, text, { color: '#DEF6FF' });
        this.label.setOrigin(0.5, 0.5);
        this.label.setDepth(this.depth + 1);
    }

    enterButtonHoverState() {
        this.setFrame(this.states.hover);
    }

    enterButtonRestState() {
        this.setFrame(this.states.rest);
    }

    enterButtonActiveState() {
        this.setFrame(this.states.active);
    }
}

export { ImageButton };
