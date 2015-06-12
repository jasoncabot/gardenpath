"use strict";

function createBoard() {

    var boardView = document.getElementById("board");

    for (var y = 0; y < 9; y++) {
        for (var x = 0; x < 9; x++) {
            var cell = document.createElement('div');
            cell.setAttribute('class', 'cell');
            cell.setAttribute('id', 'cell'+((y*9)+x));
            cell.addEventListener("click", moveHandler, false);
            boardView.appendChild(cell);
        }
    }

    for (var y = 0; y < 10; y++) {
        for (var x = 0; x < 10; x++) {
            var post = document.createElement('div');
            post.setAttribute('class', 'post');
            post.setAttribute('id', 'post'+((y*9)+x));
            post.style.left = ((x * 64)+(x*10)) + 'px';
            post.style.top =((y * 64)+(y*10)) + 'px';
            post.addEventListener("click", postHandler, false);
            boardView.appendChild(post);
        }
    }

    [{id:'player1',name:'jason',pos:{x:50,y:90}}, {id:'player2',name:'cabot',pos:{x:87,y:10}}].forEach(function(data) {
        var player = document.createElement('div');
        player.setAttribute('class', 'player');
        player.setAttribute('id', data.id);
        player.setAttribute('name', data.name);
        player.style.top = data.pos.y + 'px';
        player.style.left = data.pos.x + 'px';
        boardView.appendChild(player);
    });
}

function moveHandler(e) {
    console.log("moved player to " + e.target.id);
}

var prev;

function postHandler(e) {
    if (prev == null) {
        prev = e.target;
        e.target.style.opacity = 1;
    } else {
        prev.style.opacity = 0.1;
        console.log("building fence from " + prev.id + " to " + e.target.id);
        prev = null;
        // submit fence move

    }
}