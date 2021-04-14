type GameState = "UNKNOWN" | "WAITING_OPPONENT" | "IN_PROGRESS" | "GAME_OVER";

interface Fence {
    start: number,
    end: number
}

interface Player {
    name: string,
    position: number,
    colour: number,
    fences: Fence[],
    target: number[]
}

interface GameView {
    id: string,
    code: string,
    numberOfPlayers: number,
    me: Player,
    opponents: Player[],
    myTurn: boolean,
    lastMoveAt: number,
    state: GameState
}

interface PathNode {
    visited: boolean,
    children: number[]
}

const buildNodes = (blockedPositions: number[], fences: Fence[]) => {
    let nodes: PathNode[] = [];

    // Create the set of all nodes
    for (let index = 0; index < 9 * 9; index++) {
        nodes.push({
            visited: false,
            children: validDestinationsFromPosition(index, blockedPositions, fences)
        });
    }

    return nodes;
}

const allFences: (game: GameView) => (Fence[]) = (game) => {
    let allFences = game.me.fences;
    game.opponents.forEach(opponent => allFences = allFences.concat(opponent.fences));
    return allFences;
}

const validDestinationsInGame = (game: GameView) => {
    const myPosition = game.me.position;
    const opponentPositions = game.opponents.map(p => p.position);
    return validDestinationsFromPosition(myPosition, opponentPositions, allFences(game));
}

const validDestinationsFromPosition = (from: number, blockedPositions: number[], fences: Fence[]) => {
    // Finds all valid next steps for a current position, taking into account 1..n opponents
    let destinations = validDestinations(from)
        .filter(to => !pathBlockedByFences(from, to, fences));

    const validDestinationsThatAreBlocked = destinations.filter(to => blockedPositions.indexOf(to) >= 0);

    // Check for being able to jump over the opponent
    validDestinationsThatAreBlocked.forEach(blockedPosition => {
        const validMovesFromOpponent = validDestinations(blockedPosition);

        // moving in this direction would land us on an opponent, so check if we can jump over them
        const straightJump = blockedPosition - (from - blockedPosition);
        // if straight jump is a valid move and not blocked by a fence, then that's it!
        if (validMovesFromOpponent.indexOf(straightJump) >= 0 && !pathBlockedByFences(blockedPosition, straightJump, fences)) {
            // if this lands us on another player, just return
            // we don't need to carry on and explore diagonal moves as it's
            // against the rules
            if (blockedPositions.indexOf(straightJump) >= 0) return

            // otherwise, record the fact we can jump straight over the player
            // and don't bother exploring diagonals
            destinations.push(straightJump);
            return;
        }

        // otherwise, if we can't straight jump them we are allowed to go diagonally
        // provided we don't land on another player or pass through a fence
        validMovesFromOpponent.forEach(jumped => {
            // If this would mean going back to where we started, ignore it
            if (jumped === from) return;
            // If we already know about this position, ignore it
            if (destinations.indexOf(jumped) >= 0) return;
            // If a fence is blocking
            if (pathBlockedByFences(blockedPosition, jumped, fences)) return;
            // Otherwise it's a valid destination
            destinations.push(jumped);
        });
    });

    // You can't move on top of the opponent
    return destinations.filter(id => !(blockedPositions.indexOf(id) >= 0));
}

const pathBlockedByFences = (start: number, end: number, fences: Fence[]) => {
    const blocking = fencesThatBlock(start, end);
    for (let index = 0; index < blocking.length; index++) {
        const fence = blocking[index];

        // Check if we have played any fences that would block this move
        for (let fenceIndex = 0; fenceIndex < fences.length; fenceIndex++) {
            const potential = fences[fenceIndex];
            if (
                (potential.start === fence.start && potential.end === fence.end) ||
                (potential.start === fence.end && potential.end === fence.start)
            ) return true;
        }
    }
    return false;
}

const fencesThatBlock: (start: number, end: number) => Fence[] = (start: number, end: number) => {
    // normalise our endpoints as it doesn't matter which direction
    // you move through a fence, it is still blocked
    const min = Math.min(start, end);
    const max = Math.max(start, end);

    let fences: Fence[] = [];

    // 1) Find the two posts that would block this move
    // 2) Find both fences that would include these two posts
    if (min === max - 1) { // horizontal
        const a = min + (Math.floor(min / 9) + 1);
        const b = a + 10;
        if (a >= 9) {
            fences.push({ start: a - 10, end: b });
        }
        if (b <= 89) {
            fences.push({ start: a, end: b + 10 });
        }
    } else { // vertical
        const a = min + 9 + (Math.floor(min / 9) + 1);
        const b = a + 1;
        if (a % 10 !== 0) {
            fences.push({ start: a - 1, end: b });
        }
        if ((b + 1) % 10 !== 0) {
            fences.push({ start: a, end: b + 1 });
        }
    }
    return fences;
}

const validDestinations = (id: number) => {
    let valid: number[] = [];
    if (id >= 1 && (id % 9) !== 0) {
        valid.push(id - 1);
    }
    if (id <= 79 && ((id + 1) % 9) !== 0) {
        valid.push(id + 1);
    }
    if (id >= 9) {
        valid.push(id - 9);
    }
    if (id <= 71) {
        valid.push(id + 9);
    }
    return valid;
}

const validPostsInGame = (from: number, game: GameView) => {
    let valid: number[] = validPosts(from);
    const currentFences = allFences(game);

    // ensure that this fence doesn't cross another
    valid = valid.filter(to => {

        // for each fence played in the game
        // check if it crosses this proposed position
        for (let idx = 0; idx < currentFences.length; idx++) {
            const { start, end } = currentFences[idx];

            // check for vertical/horizontal crossing
            if ((start + end) === (from + to)) return false;

            // check for overlap along the same axis
            // from = the potential start point
            // to = the potential end point
            if (((start + end) / 2) === to || ((start + end) / 2 === from)) {
                const midpoint = (from + to) / 2;
                if (midpoint === start || midpoint === end) return false;
            }
        }

        // The fence { start: from, end: to } can be placed here but
        // we need to check if placing it causes either player to not 
        // be able to reach their winning positions, so we build a list
        // of all the fences that would be in the game and check if both
        // players can still reach their goal
        const fences: Fence[] = [{ start: from, end: to }].concat(currentFences);

        const players: Player[] = [game.me].concat(game.opponents).filter(p => !!p);
        // is any player blocked from reaching their target position?
        for (let index = 0; index < players.length; index++) {
            const player = players[index];
            const otherPlayers = players.filter(p => p.position !== player.position);
            // are we blocking the player from reaching their winning positions?
            if (!validPathToDestinations(player.target, player.position, otherPlayers.map(p => p.position), fences)) return false;
        }

        // if no blocking fences are found, then this position is
        // ok to place a new one in
        return true;
    });
    return valid;
}

const validPathToDestinations = (destinations: number[], from: number, blockedPositions: number[], fences: Fence[]) => {
    const nodes = buildNodes(blockedPositions, fences);

    // start on this current position
    const toExplore: number[] = [from];
    // breadth-first search of all valid destinations
    while (toExplore.length > 0) {
        const index: number = toExplore.shift()!;

        // base
        // if any of this nodes connections are a winning cell, then a path is possible
        if (nodes[index].children.find(childIndex => destinations.indexOf(childIndex) >= 0)) return true;

        // otherwise, search all reachable nodes
        nodes[index].children.forEach(childIndex => {
            if (!nodes[index].visited) toExplore.push(childIndex);
        });

        nodes[index].visited = true;
    }
    return false;
}

const validPosts = (id: number) => {
    let valid: number[] = [];
    if (id >= 20 && (id % 10) !== 0 && ((id + 1) % 10) !== 0) {
        valid.push(id - 20);
    }
    if (id <= 79 && (id % 10) !== 0 && ((id + 1) % 10) !== 0) {
        valid.push(id + 20);
    }
    if (((id + 2) % 10) !== 0 && ((id + 1) % 10) !== 0 && id >= 10 && id <= 89) {
        valid.push(id + 2);
    }
    if ((id % 10) !== 0 && ((id - 1) % 10) !== 0 && id >= 10 && id <= 89) {
        valid.push(id - 2);
    }
    return valid;
}

export {
    Player
    , GameState
    , GameView
    , Fence
    , validDestinationsFromPosition
    , validPostsInGame
    , validDestinationsInGame
};
