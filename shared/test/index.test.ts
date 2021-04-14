import { validDestinationsFromPosition } from '../src/index';

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
        expect(validDestinationsFromPosition(item.start, [], []).sort()).toEqual(item.destinations.sort());
    })
});

test('move down through wall is not allowed', () => {
    expect(validDestinationsFromPosition(1, [], [{ start: 10, end: 12 }]).sort()).toEqual([0, 2].sort());
})

test('move up through wall is not allowed', () => {
    expect(validDestinationsFromPosition(10, [], [{ start: 10, end: 12 }]).sort()).toEqual([9, 11, 19].sort());
})

test('move left through wall is not allowed', () => {
    expect(validDestinationsFromPosition(10, [], [{ start: 1, end: 21 }]).sort()).toEqual([1, 19, 11].sort());
})

test('move right through wall is not allowed', () => {
    expect(validDestinationsFromPosition(9, [], [{ start: 1, end: 21 }]).sort()).toEqual([0, 18].sort());
})

test('may jump left over a player', () => {
    expect(validDestinationsFromPosition(40, [39], []).sort()).toEqual([38, 31, 49, 41].sort());
});

test('may jump right over a player', () => {
    expect(validDestinationsFromPosition(39, [40], []).sort()).toEqual([30, 48, 38, 41].sort());
});

test('may jump up over a player', () => {
    expect(validDestinationsFromPosition(40, [31], []).sort()).toEqual([22, 39, 41, 49].sort());
});

test('may jump down over a player', () => {
    expect(validDestinationsFromPosition(31, [40], []).sort()).toEqual([22, 30, 32, 49].sort());
});

test('may jump diagonally over a player when wall blocks jump', () => {
    expect(validDestinationsFromPosition(40, [39], [{ start: 63, end: 43 }]).sort()).toEqual([30, 31, 41, 48, 49].sort());
});

test('may jump diagonally over a player when wall blocks jump but still not if a wall blocks the diagonal jump', () => {
    expect(validDestinationsFromPosition(40, [39], [{ start: 63, end: 43 }, { start: 43, end: 45 }]).sort()).toEqual([48, 49, 41].sort());
});

test('may not jump over a player when another player blocks jump', () => {
    expect(validDestinationsFromPosition(40, [39, 38], []).sort()).toEqual([31, 49, 41].sort());
});

test('may not land on another player when jumping diagonally over a player', () => {
    expect(validDestinationsFromPosition(40, [39, 48], [{ start: 63, end: 43 }]).sort()).toEqual([30, 31, 41, 49].sort());
});
