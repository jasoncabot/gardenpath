
import { mulberry32, predictNextMove } from './ai';
import { Queue } from './priorityqueue';

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
    target: number[],
    isMe: boolean
    isTurn: boolean
}

interface GameView {
    id: string,
    code: string,
    numberOfPlayers: number,
    players: Player[],
    lastMoveAt: number,
    state: GameState
}

interface PathNode {
    visited: boolean,
    children: Set<number>
}

interface StartReference {
    start: number,
    end: number[],
    colour: number
};

const initialConfiguration: StartReference[] = [
    { start: 76, end: [0, 1, 2, 3, 4, 5, 6, 7, 8], colour: 0x00C2FB },
    { start: 4, end: [72, 73, 74, 75, 76, 77, 78, 79, 80], colour: 0x25D3BA },
    { start: 36, end: [8, 17, 26, 35, 44, 53, 62, 71, 80], colour: 0xFF8119 },
    { start: 44, end: [0, 9, 18, 27, 36, 45, 54, 63, 72], colour: 0xAC47C7 },
];

const nextPositionOnShortestPath: (start: number, goal: Set<number>, blocked: Set<number>, fences: Fence[]) => (number) = (start: number, goal: Set<number>, blocked: Set<number>, fences: Fence[]) => {

    let nodes: PathNode[] = buildNodes(blocked, fences);

    const findNextOnPath = (position: number, edges: Record<number, number>) => {
        let current = position;
        while (edges[current] !== start) {
            current = edges[current];
        }
        return current;
    }

    const scores: Record<number, number> = {};
    const cameFrom: Record<number, number> = {};

    const open = new Queue();
    open.enqueue(0, start);

    while (!open.isEmpty()) {
        const item = open.dequeue();
        if (goal.has(item.value)) {
            return findNextOnPath(item.value, cameFrom);
        }

        nodes[item.value].children.forEach((neighbour: number) => {

            const tentative = item.priority + 1;
            let existingScore = scores[neighbour];
            if (!existingScore) {
                existingScore = Infinity;
            }
            if (tentative < existingScore) {
                scores[neighbour] = tentative;
                cameFrom[neighbour] = item.value;
            }
            open.enqueue(tentative, neighbour);
        });
    }

    return 0;
}


const buildNodes = (blockedPositions: Set<number>, fences: Fence[]) => {
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

const allFences: (players: Player[]) => (Fence[]) = (players) => {
    let allFences: Fence[] = [];
    players.forEach(player => allFences = allFences.concat(player.fences));
    return allFences;
}

const validDestinationsInGame = (game: GameView) => {
    let myPosition: number;
    let opponentPositions = new Set<number>();
    game.players.forEach(player => {
        if (player.isMe) {
            myPosition = player.position;
        } else {
            opponentPositions.add(player.position);
        }
    });
    return validDestinationsFromPosition(myPosition!, opponentPositions, allFences(allPlayers(game)));
}

const validDestinationsFromPosition = (from: number, blockedPositions: Set<number>, fences: Fence[]) => {
    // Finds all valid next steps for a current position, taking into account 1..n opponents
    const valid: Set<number> = new Set();
    const blocked: Set<number> = new Set();
    validDestinations(from).forEach(to => {
        if (pathBlockedByFences(from, to, fences)) return;
        const canMoveTo = !blockedPositions.has(to);
        (canMoveTo ? valid : blocked).add(to);
    });

    // Check for being able to jump over the opponent
    blocked.forEach(blockedPosition => {
        const validMovesFromOpponent = validDestinations(blockedPosition);

        // moving in this direction would land us on an opponent, so check if we can jump over them
        const straightJump = blockedPosition - (from - blockedPosition);
        // if straight jump is a valid move and not blocked by a fence, then that's it!
        if (validMovesFromOpponent.has(straightJump) && !pathBlockedByFences(blockedPosition, straightJump, fences)) {
            // if this lands us on another player, just return
            // we don't need to carry on and explore diagonal moves as it's
            // against the rules
            if (blockedPositions.has(straightJump)) return;

            // otherwise, record the fact we can jump straight over the player
            // and don't bother exploring diagonals
            valid.add(straightJump);
            return;
        }

        // otherwise, if we can't straight jump them we are allowed to go diagonally
        // provided we don't land on another player or pass through a fence
        validMovesFromOpponent.forEach(landing => {
            // If this would mean going back to where we started, ignore it
            if (landing === from) return;
            // If we already know about this position, ignore it
            if (valid.has(landing)) return;
            // If a fence is blocking
            if (pathBlockedByFences(blockedPosition, landing, fences)) return;
            // If we are landing on a player
            if (blockedPositions.has(landing)) return;
            // Otherwise it's a valid destination
            valid.add(landing);
        });
    });

    return valid;
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
    let valid: Set<number> = new Set();
    if (id >= 1 && (id % 9) !== 0) {
        valid.add(id - 1);
    }
    if (id <= 79 && ((id + 1) % 9) !== 0) {
        valid.add(id + 1);
    }
    if (id >= 9) {
        valid.add(id - 9);
    }
    if (id <= 71) {
        valid.add(id + 9);
    }
    return valid;
}

const allPlayers: (game: GameView) => Player[] = (game: GameView) => {
    return game.players.filter(p => !!p);
}

const validPostsInGame: (from: number, game: GameView) => (number[]) = (from: number, game: GameView) => {
    return validPostsForPlayers(from, allPlayers(game));
}

const validPostsForPlayers: (from: number, players: Player[]) => number[] = (from: number, players: Player[]) => {
    const currentFences = allFences(players);

    // ensure that this fence doesn't cross another
    let valid: number[] = validPosts(from).filter(to => {

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

        // is any player blocked from reaching their target position?
        for (let index = 0; index < players.length; index++) {
            const player = players[index];
            const otherPlayers: Set<number> = new Set(players.map(p => p.position));
            otherPlayers.delete(player.position);
            // are we blocking the player from reaching their winning positions?
            if (!validPathToDestinations(new Set(player.target), player.position, otherPlayers, fences)) return false;
        }

        // if no blocking fences are found, then this position is
        // ok to place a new one in
        return true;
    });
    return valid;
}

const validPathToDestinations = (destinations: Set<number>, from: number, blockedPositions: Set<number>, fences: Fence[]) => {
    const nodes = buildNodes(blockedPositions, fences);

    // start on this current position
    const toExplore: number[] = [from];
    let pathHasBeenFound = false;
    // breadth-first search of all valid destinations
    while (!pathHasBeenFound && toExplore.length > 0) {
        const index: number = toExplore.shift()!;

        nodes[index].children.forEach(childIndex => {
            // base, if any of this nodes connections are a winning cell, then a path is possible
            if (destinations.has(childIndex)) {
                pathHasBeenFound = true;
                return;
            }

            // otherwise, search all reachable nodes
            if (!nodes[childIndex].visited) toExplore.push(childIndex);
        });

        nodes[index].visited = true;
    }
    return pathHasBeenFound;
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
    allFences
    , Fence
    , GameState
    , GameView
    , initialConfiguration
    , nextPositionOnShortestPath
    , Player
    , StartReference
    , validDestinationsFromPosition
    , validDestinationsInGame
    , validPostsInGame
    , validPostsForPlayers
};
