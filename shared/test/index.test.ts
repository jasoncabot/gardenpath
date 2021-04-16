import { validDestinationsFromPosition } from '../src/index';
import { expect, test } from '@jest/globals';

const emptySet: Set<number> = new Set();

test('basic movement without wall or jump', () => {
    const noOpponentsOrFences = [
        // corner cases
        { start: 0, destinations: [1, 9] },
        { start: 72, destinations: [63, 73] },
        { start: 80, destinations: [71, 79] },
        { start: 8, destinations: [7, 17] },
        // middle
        { start: 30, destinations: [21, 29, 31, 39] }
    ]
    noOpponentsOrFences.forEach(item => {
        expect(validDestinationsFromPosition(item.start, emptySet, [])).toEqual(new Set(item.destinations));
    })
});

test('move down through wall is not allowed', () => {
    expect(validDestinationsFromPosition(1, emptySet, [{ start: 10, end: 12 }])).toEqual(new Set([0, 2]));
})

test('move up through wall is not allowed', () => {
    expect(validDestinationsFromPosition(10, emptySet, [{ start: 10, end: 12 }])).toEqual(new Set([9, 11, 19]));
})

test('move left through wall is not allowed', () => {
    expect(validDestinationsFromPosition(10, emptySet, [{ start: 1, end: 21 }])).toEqual(new Set([1, 19, 11]));
})

test('move right through wall is not allowed', () => {
    expect(validDestinationsFromPosition(9, emptySet, [{ start: 1, end: 21 }])).toEqual(new Set([0, 18]));
})

test('may jump left over a player', () => {
    expect(validDestinationsFromPosition(40, new Set([39]), [])).toEqual(new Set([38, 31, 49, 41]));
});

test('may jump right over a player', () => {
    expect(validDestinationsFromPosition(39, new Set([40]), [])).toEqual(new Set([30, 48, 38, 41]));
});

test('may jump up over a player', () => {
    expect(validDestinationsFromPosition(40, new Set([31]), [])).toEqual(new Set([22, 39, 41, 49]));
});

test('may jump down over a player', () => {
    expect(validDestinationsFromPosition(31, new Set([40]), [])).toEqual(new Set([22, 30, 32, 49]));
});

test('may jump diagonally over a player when wall blocks jump', () => {
    expect(validDestinationsFromPosition(40, new Set([39]), [{ start: 63, end: 43 }])).toEqual(new Set([30, 31, 41, 48, 49]));
});

test('may jump diagonally over a player when wall blocks jump but still not if a wall blocks the diagonal jump', () => {
    expect(validDestinationsFromPosition(40, new Set([39]), [{ start: 63, end: 43 }, { start: 43, end: 45 }])).toEqual(new Set([48, 49, 41]));
});

test('may not jump over a player when another player blocks jump', () => {
    expect(validDestinationsFromPosition(40, new Set([39, 38]), [])).toEqual(new Set([31, 49, 41]));
});

test('may not land on another player when jumping diagonally over a player', () => {
    expect(validDestinationsFromPosition(40, new Set([39, 48]), [{ start: 63, end: 43 }])).toEqual(new Set([30, 31, 41, 49]));
});
