import { Fence, Player } from "./index";

interface PredictionView {
    player: Player,
    opponents: Player[]
}

interface PredictionResult {
    type: "MOVE" | "FENCE"
    result: Fence | number
}

const predictNextMove: (game: PredictionView) => (PredictionResult) = () => {
    return {
        type: "FENCE",
        result: {
            start: 1,
            end: 1
        }
    }
}

export { PredictionView, PredictionResult, predictNextMove }
