"use strict";

var svgNS = "http://www.w3.org/2000/svg";



function createBoard() {
    var board = new Board();
    board.move("player1", 4);
    board.move("player2", 76);

    console.log(board.pathFrom(4,76));
    console.log(board.pathFrom(76,4));



    var boardView = document.getElementById("board");


    for (var y = 0; y < 9; y++) {
        for (var x = 0; x < 9; x++) {
            var cell = document.createElementNS(svgNS,'rect');
            cell.setAttribute('x', (x*50) + (x*10));
            cell.setAttribute('y', (y*50) + (y*10));
            cell.setAttribute('width', 50);
            cell.setAttribute('height', 50);
            cell.setAttribute('class', 'cell');
            cell.addEventListener('click', onCellSelected);
            boardView.appendChild(cell);
        }
    }



}

function boardView

function onCellSelected(e) {
    console.log('selected cell '+e.relatedTarget);
}
