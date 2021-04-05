import express from 'express';

import { InProgressGame, validDestinationsInGame } from '../../shared/dist/index';

const app = express();
const port = 8080; // default port to listen

// define a route handler for the default home page
app.get("/", (req, res) => {
    const game: InProgressGame = {
        id: 1,
        me: {
            name: "Jason",
            position: 71,
            fences: [
            ],
            target: [0, 1, 2, 3, 4, 5, 6, 7, 8]
        },
        opponents: [{
            name: "Jason",
            position: 70,
            fences: [
            ],
            target: [72, 73, 74, 75, 76, 77, 78, 79, 80]
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
