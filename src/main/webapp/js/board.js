"use strict";

function Cell(id) {
    this.id = id;
}

function Board() {
    this.spaces = []

    for (var i = 0; i < 81; i++) {
        this.spaces.push(new Cell(i));
    }
}

Board.prototype.cell = function(index) {
    return this.spaces[index];
}

Board.prototype.pathFrom = function(a, b) {
    return false;
}

Board.prototype.move = function(player, to) {
    console.log("moving player " + player + " from " + "???" + " to " + to);
}

Board.prototype.wall = function(id) {
    console.log("Building wall with id " + id);
}