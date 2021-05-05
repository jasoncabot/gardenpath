import { validPathToDestinations } from '../src/index';
import { expect, test } from '@jest/globals';

const emptySet: Set<number> = new Set();

test('validPathToDestinations performance', () => {
    const destinations = new Set([4]);
    const from = 76;
    const blockedPositions = new Set([67, 49]);
    const fences = [
        { start: 84, end: 86 },
        { start: 86, end: 66 },
        { start: 76, end: 78 },
        { start: 78, end: 98 },
        { start: 10, end: 12 },
        { start: 12, end: 14 },
        { start: 14, end: 34 },
        { start: 34, end: 36 },
        { start: 5, end: 25 },
        { start: 25, end: 27 },
        { start: 27, end: 29 }
    ];

    for (let index = 0; index < 50000; index++) {
        validPathToDestinations(destinations, from, blockedPositions, fences);
    }
});
