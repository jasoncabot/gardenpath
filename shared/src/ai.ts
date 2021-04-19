import { allFences, Fence, nextPositionOnShortestPath, Player, validDestinationsFromPosition, validPostsForPlayers } from "./index";

interface GameState {
    currentPlayerIndex: number
    players: Player[]
}

interface PlayCommand {
    type: "MOVE" | "FENCE"
    result: Fence | number
}

interface PRNG {
    next: () => (number);
}

interface TreeNode {
    parent: TreeNode | undefined
    children: TreeNode[]
    play: PlayCommand | undefined
    state: GameState
    winCount: number
    simulationCount: number
    validCommands: PlayCommand[]
}

const opponentPositions: (game: GameState) => (Set<number>) = (game: GameState) => {
    let blocked: Set<number> = new Set();
    game.players.forEach(player => {
        if (player.position === game.players[game.currentPlayerIndex].position) return;
        blocked.add(player.position);
    });
    return blocked;
}

const validFencesForNextMove: (game: GameState) => (Fence[]) = (game: GameState) => {
    let fences: Fence[] = [];

    const toFence: (start: number, end: number) => Fence = (start, end) => {
        return { start, end }
    }

    for (let startPostId = 0; startPostId < 99; startPostId++) {
        const more = validPostsForPlayers(startPostId, game.players).map(endPostId => toFence(startPostId, endPostId));
        fences = fences.concat(more);
    }

    return fences;
}

const buildMovesForState: (game: GameState) => (PlayCommand[]) = (game: GameState) => {
    const player = game.players[game.currentPlayerIndex];

    // generate all possible next moves
    const possibleMoves = validDestinationsFromPosition(player.position, opponentPositions(game), allFences(game.players));
    const possibleFences: Fence[] = player.fences.length < 10 ? validFencesForNextMove(game) : [];

    const moves: PlayCommand[] = [];
    possibleMoves.forEach(position => {
        moves.push({ type: "MOVE", result: position });
    });
    possibleFences.forEach(fence => {
        moves.push({ type: "FENCE", result: fence });
    });

    return moves;
}

const predictNextMove: (generator: PRNG, game: GameState) => (PlayCommand) = (generator: PRNG, game: GameState) => {

    const calculateScore: (node: TreeNode, explorationConstant: number) => number = (node: TreeNode, explorationConstant: number) => {
        if (node.simulationCount === 0) return -Infinity;
        return (node.winCount / node.simulationCount) + Math.sqrt((explorationConstant * Math.log(node.parent?.simulationCount ?? 0)) / node.simulationCount);
    }

    const select: (toSearch: TreeNode) => TreeNode = (toSearch: TreeNode) => {
        interface ScoredNode { nodes: TreeNode[], score: number };

        let node = toSearch;
        while (isFullyExpanded(node) && !isLeaf(node)) {

            const bestChildren = node.validCommands.reduce((best: ScoredNode, command: PlayCommand) => {
                const childState = applyCommand(node.state, command);
                const child: TreeNode = buildNode(node, command, childState);
                const childScore = calculateScore(child, Math.SQRT2);

                if (childScore > best.score) {
                    return {
                        nodes: [child],
                        score: childScore
                    };
                } else if (childScore === best.score) {
                    best.nodes.push(child);
                }

                return best;
            }, { nodes: [], score: -Infinity }).nodes;

            node = randomSelection(generator, bestChildren);
        }

        return node;
    };

    const selectCommandUsingHeuristics: (state: GameState, commands: PlayCommand[]) => PlayCommand = (state: GameState, commands: PlayCommand[]) => {
        const { position, target } = state.players[state.currentPlayerIndex];
        const blocked = opponentPositions(state)
        const fences = allFences(state.players);
        const moveOnShortestPath = nextPositionOnShortestPath(position, new Set(target), blocked, fences);

        // TODO: this isn't right - we shouldn't just move :)
        // TODO: should we also check that this is a valid move?
        return {
            type: "MOVE",
            result: moveOnShortestPath
        };
    }

    const expand: (node: TreeNode) => TreeNode = (node: TreeNode) => {
        const randomExpansion = selectCommandUsingHeuristics(node.state, node.validCommands);

        const state = applyCommand(node.state, randomExpansion);
        const child: TreeNode = buildNode(node, randomExpansion, state);
        node.children.push(child);

        return child;
    };

    const simulate: (node: TreeNode) => number = (node: TreeNode) => {
        let { state, validCommands } = node;
        let winner = findWinner(state);
        while (winner === undefined) {
            const command = selectCommandUsingHeuristics(state, validCommands);
            state = applyCommand(state, command);
            validCommands = buildMovesForState(state);
            winner = findWinner(state);
        }
        return winner;
    }

    const backPropagate: (node: TreeNode, winner: number) => (void) = (node: TreeNode, winner: number) => {
        let current: TreeNode | undefined = node;

        while (current !== undefined) {
            current.simulationCount += 1;
            if (current.state.currentPlayerIndex === winner) {
                current.winCount += 1;
            }
            current = current.parent;
        }
    }

    const isFullyExpanded: (node: TreeNode) => boolean = (node: TreeNode) => {
        return node.children.length === node.validCommands.length;
    }

    const isLeaf: (node: TreeNode) => boolean = (node: TreeNode) => {
        return node.children.length === 0;
    }

    // start at the root of the tree
    const root = buildRootNode(game);

    // main loop for finding the best move in a given time
    let count = 1;
    while (count-- > 0) {
        // Start from root R and select successive child nodes until a leaf node L is reached
        let node = select(root);
        let winner = findWinner(node.state);

        // Unless L ends the game decisively (e.g. win/loss/draw) for either player, create one (or more)
        // child nodes and choose node C from one of them
        if (winner === undefined) {
            node = expand(node);
            // Complete one random playout from node C
            winner = simulate(node);
        }
        // Use the result of the playout to update information in the nodes on the path from C to R
        backPropagate(node, winner!);
    }

    const findPlayByMaximumWinRate: (node: TreeNode) => PlayCommand = (node: TreeNode) => {
        let maximumNode: TreeNode | undefined
        let maximum = -Infinity;
        for (let index = 0; index < node.children.length; index++) {
            const child = node.children[index];
            const winRate = child.winCount / child.simulationCount;
            if (winRate > maximum) {
                maximumNode = child;
                maximum = winRate;
            }
        }
        if (!maximumNode?.play) throw new Error("No play found. This can only happen if there are no valid plays from current state");
        return maximumNode.play;
    }

    return findPlayByMaximumWinRate(root);
}

const findWinner: (game: GameState) => number | undefined = (game: GameState) => {
    for (let index = 0; index < game.players.length; index++) {
        const player = game.players[index];
        if (player.target.indexOf(player.position) >= 0) return index;
    }
    return undefined;
}

const applyCommand: (game: GameState, command: PlayCommand) => (GameState) = (game: GameState, command: PlayCommand) => {
    // clone the current game state and apply some changes
    let nextPlayerIndex = game.currentPlayerIndex + 1;
    if (nextPlayerIndex >= game.players.length) nextPlayerIndex = 0;

    if (command.type === 'FENCE') {
        return {
            currentPlayerIndex: nextPlayerIndex,
            players: game.players.map((player: Player, index: number) => {
                // leave all unaffected players as they were
                if (index !== game.currentPlayerIndex) return player;
                // clone the current player
                const fence = <Fence>command.result;
                return {
                    colour: player.colour,
                    name: player.name,
                    position: player.position,
                    target: player.target,
                    // but update their fences
                    fences: player.fences.concat(fence),
                }
            })
        }

    } else if (command.type === 'MOVE') {
        return {
            currentPlayerIndex: nextPlayerIndex,
            players: game.players.map((player: Player, index: number) => {
                // leave all unaffected players as they were
                if (index !== game.currentPlayerIndex) return player;
                // clone the current player
                const destination = <number>command.result;
                return {
                    colour: player.colour,
                    fences: player.fences,
                    name: player.name,
                    target: player.target,
                    // but update their position
                    position: destination
                }
            })
        }
    }

    throw new Error("Invalid command applied");
}

const randomSelection: <Type>(generator: PRNG, from: Type[]) => Type = <Type>(generator: PRNG, from: Type[]) => {
    const randomIndex = Math.floor(generator.next() * from.length);
    return from[randomIndex];
}

const mulberry32: (seed: number) => PRNG = (seed: number) => {
    return {
        next: () => {
            var t = seed += 0x6D2B79F5;
            t = Math.imul(t ^ t >>> 15, t | 1);
            t ^= t + Math.imul(t ^ t >>> 7, t | 61);
            return ((t ^ t >>> 14) >>> 0) / 4294967296;
        }
    }
}

const buildRootNode: (game: GameState) => TreeNode = (game: GameState) => {
    return buildNode(undefined, undefined, game);
}

const buildNode: (parent: TreeNode | undefined, play: PlayCommand | undefined, state: GameState) => TreeNode = (parent: TreeNode | undefined, play: PlayCommand | undefined, state: GameState) => {
    return {
        parent: parent,
        children: [],
        play: play,
        state: state,
        winCount: 0,
        simulationCount: 0,
        validCommands: buildMovesForState(state)
    };
}

export { GameState, PlayCommand, predictNextMove, mulberry32 }
