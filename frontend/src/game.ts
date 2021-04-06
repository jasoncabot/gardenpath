import 'phaser';
import { GameView, validPostsInGame, validDestinationsInGame } from '../../shared/dist/index';

const POST_GAP = 12;
const POST_WIDTH = 44;
const CELL_WIDTH = 44;
const CELL_OVERLAP = (CELL_WIDTH - POST_GAP) / 2;

export default class InProgress extends Phaser.Scene {
    constructor() {
        super('game');
    }

    preload() {
    }

    create() {

        const game: GameView = {
            id: "1",
            me: {
                name: "Player 1",
                position: 76,
                colour: 0x00C2FB,
                fences: [
                ],
                target: [0, 1, 2, 3, 4, 5, 6, 7, 8]
            },
            opponents: [{
                name: "Player 2",
                position: 4,
                colour: 0x25D3BA,
                fences: [
                ],
                target: [72, 73, 74, 75, 76, 77, 78, 79, 80]
            }, {
                name: "Player 3",
                position: 36,
                colour: 0xFF8119,
                fences: [
                ],
                target: [8, 17, 26, 35, 44, 53, 62, 71, 80]
            }, {
                name: "Player 4",
                position: 44,
                colour: 0xAC47C7,
                fences: [
                ],
                target: [0, 9, 18, 27, 36, 45, 54, 63, 72]
            }],
            myTurn: true,
            lastMoveAt: Date.now(),
            state: "IN_PROGRESS",
        };

        let views: {
            posts: Phaser.GameObjects.Arc[],
            cells: Phaser.GameObjects.Rectangle[],
            me?: Phaser.GameObjects.GameObject,
            opponents: Phaser.GameObjects.GameObject[]
        } = {
            posts: [],
            cells: [],
            me: undefined,
            opponents: []
        };

        const CELL_COLOUR = 0xE5E5E5;
        const CELL_COLOUR_HIGHLIGHT = 0xB5B5B5;
        const POST_COLOUR = 0xA5A5A5;

        for (let y = 0; y < 9; y++) {
            for (let x = 0; x < 9; x++) {
                const cellId = ((y * 9) + x);
                const pos = this.positionForCell(cellId);

                const cell = this.add.rectangle(pos.x, pos.y, CELL_WIDTH, CELL_WIDTH, CELL_COLOUR, 1.0)
                    .setData("id", cellId)
                    .setData("type", "cell")
                    .setInteractive({ dropZone: true });
                views.cells[cellId] = cell;
            }
        }

        for (let y = 0; y < 10; y++) {
            for (let x = 0; x < 10; x++) {
                const postId = ((y * 10) + x);
                const pos = this.positionForPost(postId);

                const post = this.add.circle(pos.x, pos.y, POST_WIDTH / 2, POST_COLOUR, 1.0)
                    .setInteractive({ draggable: true, dropZone: true })
                    .setData("id", postId)
                    .setData("type", "post")
                    .setDepth(1)
                    .setAlpha(0)
                    .on('dragstart', (_pointer: Phaser.Input.Pointer, _dragX: number, _dragY: number) => {
                        validPostsInGame(postId, game).forEach((id: number) => {
                            views.posts[id].alpha = 0.5;
                            views.posts[id].setScale(1.1, 1.1);
                        });
                    })
                    .on('dragend', (_pointer: Phaser.Input.Pointer, _dragX: number, _dragY: number) => {
                        validPostsInGame(postId, game).forEach((id: number) => {
                            views.posts[id].alpha = 0;
                            views.posts[id].setScale(1, 1);
                        });
                    })
                    .on('drop', (_pointer: any, gameObject: any, _dropZone: any) => {
                        const valid = validPostsInGame(postId, game);
                        // check dragging from another post
                        if (gameObject.getData('type') !== "post") return;
                        const targetId = gameObject.getData('id');
                        // is this a valid construction?
                        if (!valid.includes(targetId)) return;
                        valid.forEach((id: number) => {
                            views.posts[id].alpha = 0;
                            views.posts[id].setScale(1, 1);
                        });
                        drawFence(postId, targetId, POST_COLOUR);
                        game.me.fences.push({ start: postId, end: targetId });
                    })
                    ;
                post.input.alwaysEnabled = true;

                views.posts[postId] = post;
            }
        }

        const me = this.positionForCell(game.me.position);
        views.me = this.add.circle(me.x, me.y, CELL_WIDTH / 2, game.me.colour, 1.0)
            .setInteractive({ draggable: true })
            .setData("id", game.me.position)
            .setData("type", "player")
            .setDepth(3)
            .on('dragstart', (_pointer: Phaser.Input.Pointer, _dragX: number, _dragY: number) => {
                validDestinationsInGame(game).forEach((id: number) => {
                    views.cells[id].fillColor = CELL_COLOUR_HIGHLIGHT;
                });
                views.cells.forEach(e => e.setDepth(2));
            })
            .on('dragend', (_pointer: Phaser.Input.Pointer, _dragX: number, _dragY: number) => {
                validDestinationsInGame(game).forEach((id: number) => {
                    views.cells[id].fillColor = CELL_COLOUR;
                });
                views.cells.forEach(e => e.setDepth(0));
            })
            .on('drop', (_pointer: Phaser.Input.Pointer, target: Phaser.GameObjects.GameObject) => {
                if (target.getData('type') !== "cell") return;
                const validDropZones = validDestinationsInGame(game);
                if (!validDropZones.includes(target.getData("id"))) return;

                const { x, y } = this.positionForCell(target.getData("id"));
                (views.me as Phaser.GameObjects.Arc).setPosition(x, y);
                validDropZones.forEach((id: number) => {
                    views.cells[id].fillColor = CELL_COLOUR;
                });
                game.me.position = target.getData("id");
                views.me?.setData("id", target.getData("id"));
                views.cells.forEach(e => e.setDepth(0));
            })
            ;
        game.opponents.forEach(opponent => {
            const you = this.positionForCell(opponent.position);
            views.opponents.push(this.add.circle(you.x, you.y, CELL_WIDTH / 2, opponent.colour, 1.0)
                .setData("id", opponent.position)
                .setData("type", "player")
                .setDepth(3)
            );
        });

        const drawFence: (start: number, end: number, colour: number) => (void) = (start, end, colour) => {
            const rect = this.rectForFence(start, end);
            this.add.rectangle(rect.x + (rect.width / 2), rect.y + (rect.height / 2), rect.width, rect.height, colour, 1.0);
        }

        game.me.fences.forEach(fence => drawFence(fence.start, fence.end, POST_COLOUR));
        game.opponents.forEach(p => p.fences.forEach(fence => drawFence(fence.start, fence.end, POST_COLOUR)));
    }

    positionForPost = (index: number) => {
        const x = index % 10;
        const y = Math.floor(index / 10);
        return {
            'x': (x * POST_WIDTH) + (x * POST_GAP) + (POST_WIDTH / 2),
            'y': (y * POST_WIDTH) + (y * POST_GAP) + (POST_WIDTH / 2)
        };
    }

    positionForCell = (index: number) => {
        const x = index % 9;
        const y = Math.floor(index / 9);
        return {
            'x': CELL_OVERLAP + (x * CELL_WIDTH) + ((x + 1) * POST_GAP) + (CELL_WIDTH / 2),
            'y': CELL_OVERLAP + (y * CELL_WIDTH) + ((y + 1) * POST_GAP) + (CELL_WIDTH / 2)
        };
    }

    rectForFence = (start: number, end: number) => {
        var startPos = this.positionForPost(start);
        var endPos = this.positionForPost(end);
        return {
            'x': Math.min(startPos.x, endPos.x) - (POST_WIDTH / 2) + (CELL_OVERLAP + POST_GAP)
            , 'y': Math.min(startPos.y, endPos.y) - (POST_WIDTH / 2) + (CELL_OVERLAP + POST_GAP)
            , 'width': (POST_WIDTH + (Math.max(startPos.x, endPos.x) - Math.min(startPos.x, endPos.x))) - (2 * (CELL_OVERLAP + POST_GAP))
            , 'height': (POST_WIDTH + (Math.max(startPos.y, endPos.y) - Math.min(startPos.y, endPos.y))) - (2 * (CELL_OVERLAP + POST_GAP))
        };
    }


}

const config = {
    type: Phaser.AUTO,
    backgroundColor: '#F3F3F3',
    width: 548,
    height: 548,
    scale: {
        mode: Phaser.Scale.FIT,
        autoCenter: Phaser.Scale.CENTER_BOTH
    },
    scene: InProgress
};

const game = new Phaser.Game(config);
