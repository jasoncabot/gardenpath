import 'phaser';
import { GameObjects } from 'phaser';
import { FenceViewModel, GameController, GameViewModel, MoveViewModel } from '../model/gamecontroller';
import BaseScene from './BaseScene';

const POST_GAP = 12;
const POST_WIDTH = 44;
const CELL_WIDTH = 44;
const CELL_OVERLAP = (CELL_WIDTH - POST_GAP) / 2;
const CELL_COLOUR = 0xE5E5E5;
const CELL_COLOUR_HIGHLIGHT = 0xB5B5B5;
const POST_COLOUR = 0xA5A5A5;

export default class GameScene extends BaseScene {
    static key = "GameScene";

    constructor() {
        super(GameScene.key);
    }

    controller: GameController | undefined
    views: {
        posts: Phaser.GameObjects.Arc[],
        cells: Phaser.GameObjects.Rectangle[],
        players: Phaser.GameObjects.GameObject[]
    } = { posts: [], cells: [], players: [] };

    init(data: any) {
        this.controller = new GameController(data.id);
    }

    preload() {
    }

    create() {
        this.controller!.on('game', this.onGameLoaded);
        this.controller!.on('fence', (fence: FenceViewModel) => {
            this.drawFence(fence.start, fence.end, POST_COLOUR);
        });
        this.controller!.on('move', (move: MoveViewModel) => {
            // A player was moved, may not have been us
            const { x, y } = this.positionForCell(move.to);
            // so we look through all views (max. NUM_PLAYERS) until we find the one that corresponds to this event
            const movedPlayer = this.views.players.find(playerView => {
                return playerView?.getData('id') === move.from
            });
            // then we update it's position
            (movedPlayer as Phaser.GameObjects.Arc).setPosition(x, y);
            movedPlayer?.setData("id", move.to);
        });
        this.controller!.load();
    }

    destroy() {
        this.controller!.off('game');
        this.controller!.off('fence');
        this.controller!.off('move');
    }

    onGameLoaded = (game: GameViewModel) => {

        for (let y = 0; y < 9; y++) {
            for (let x = 0; x < 9; x++) {
                const cellId = ((y * 9) + x);
                const pos = this.positionForCell(cellId);

                const cell = this.add.rectangle(pos.x, pos.y, CELL_WIDTH, CELL_WIDTH, CELL_COLOUR, 1.0)
                    .setData("id", cellId)
                    .setData("type", "cell")
                    .setInteractive({ dropZone: true });
                this.views.cells[cellId] = cell;
            }
        }

        let fenceUnderConstruction: GameObjects.GameObject | undefined = undefined;
        let validFencesForConstruction: number[] = [];
        const onDraggingFenceEnded = () => {
            validFencesForConstruction.forEach((id: number) => {
                this.views.posts[id].alpha = 0;
                this.views.posts[id].setScale(1, 1);
            });
            validFencesForConstruction = [];

            // destroy any temporary fences we had rendered
            fenceUnderConstruction?.destroy();
            fenceUnderConstruction = undefined;
        }

        // Render all posts
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
                        validFencesForConstruction = this.controller!.validPosts(postId);
                        validFencesForConstruction.forEach((id: number) => {
                            this.views.posts[id].alpha = 0.5;
                            this.views.posts[id].setScale(1.1, 1.1);
                        });
                    })
                    .on('dragend', (_pointer: Phaser.Input.Pointer, _dragX: number, _dragY: number) => {
                        onDraggingFenceEnded();
                    })
                    .on('dragenter', (_pointer: Phaser.Input.Pointer, target: Phaser.GameObjects.GameObject) => {
                        const targetId = target.getData('id');
                        if (target.getData('type') !== 'post' || !validFencesForConstruction.includes(targetId)) return;
                        // create temporary fence element
                        fenceUnderConstruction = this.drawFence(postId, targetId, POST_COLOUR);
                    })
                    .on('dragleave', (_pointer: Phaser.Input.Pointer, target: Phaser.GameObjects.GameObject) => {
                        const targetId = target.getData('id');
                        if (target.getData('type') !== 'post' || !validFencesForConstruction.includes(targetId)) return;
                        // destroy temporary fence element
                        fenceUnderConstruction?.destroy();
                        fenceUnderConstruction = undefined;
                    })
                    .on('drop', (_pointer: Phaser.Input.Pointer, target: Phaser.GameObjects.GameObject, _dropZone: any) => {
                        const targetId = target.getData('id');
                        if (target.getData('type') !== 'post' || !validFencesForConstruction.includes(targetId)) return;

                        onDraggingFenceEnded();
                        this.controller!.fence(postId, targetId);
                    })
                    ;
                post.input.alwaysEnabled = true;

                this.views.posts[postId] = post;
            }
        }

        let validDestinations: number[] = [];
        const onDraggingPlayerEnded = () => {
            validDestinations.forEach((id: number) => {
                this.views.cells[id].fillColor = CELL_COLOUR;
            });
            this.views.cells.forEach(e => e.setDepth(0));
            validDestinations = [];
        }

        // Render all players
        game.players.forEach(player => {
            const { x, y } = this.positionForCell(player.position);
            const view = this.add.circle(x, y, CELL_WIDTH / 2, player.colour, 1.0)
                .setInteractive({ draggable: true })
                .setData("id", player.position)
                .setData("type", "player")
                .setDepth(3);

            if (player.controllable) {
                view
                    .on('dragstart', (_pointer: Phaser.Input.Pointer, _dragX: number, _dragY: number) => {
                        validDestinations = this.controller!.validDestinations();
                        validDestinations.forEach((id: number) => {
                            this.views.cells[id].fillColor = CELL_COLOUR_HIGHLIGHT;
                        });
                        this.views.cells.forEach(e => e.setDepth(2));
                    })
                    .on('dragend', (_pointer: Phaser.Input.Pointer, _dragX: number, _dragY: number) => {
                        onDraggingPlayerEnded();
                    })
                    .on('drop', (_pointer: Phaser.Input.Pointer, target: Phaser.GameObjects.GameObject) => {
                        if (target.getData('type') !== "cell") return;
                        if (!validDestinations.includes(target.getData("id"))) return;

                        onDraggingPlayerEnded();
                        this.controller!.move(target.getData("id"));
                    })
                    ;
            }

            this.views.players.push(view);
        });

        // Render all fences
        game.fences.forEach(fence => this.drawFence(fence.start, fence.end, POST_COLOUR));
    }

    drawFence: (start: number, end: number, colour: number) => (Phaser.GameObjects.GameObject) = (start, end, colour) => {
        const rect = this.rectForFence(start, end);
        return this.add.rectangle(rect.x + (rect.width / 2), rect.y + (rect.height / 2), rect.width, rect.height, colour, 1.0);
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

export { GameScene };
