interface ImageButtonStateImages {
    rest: string
    hover: string
    active: string
}

class ImageButton extends Phaser.GameObjects.Image {

    states: ImageButtonStateImages

    constructor(scene: Phaser.Scene, x: number, y: number, states: ImageButtonStateImages, callback: () => (void)) {
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
