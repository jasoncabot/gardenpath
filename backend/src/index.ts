import express from 'express';

import { InProgressGame, validDestinationsInGame } from '../../shared/dist/index';

const app = express();
const port = 8080; // default port to listen

// define a route handler for the default home page
app.get("/", (req, res) => {
    const game: InProgressGame = {
        id: 1,
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

    const valid = validDestinationsInGame(game);

    res.send(valid);
});

// start the Express server
app.listen(port, () => {
    console.log(`server started at http://localhost:${port}`);
});
