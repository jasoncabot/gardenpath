import { removeStaleGames } from '../service/database';

type JobHandles = NodeJS.Timeout[];

const SECOND = 1000;
const MINUTE = 60 * SECOND;
const HOUR = 60 * MINUTE;
const DAY = 24 * HOUR;

const registerJobs: () => (JobHandles) = () => {
    const clearStaleGames = setInterval(removeStaleGames, 4 * HOUR);
    return [clearStaleGames];
}

export { registerJobs };