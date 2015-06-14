"use strict";

/**
 * Generates a GUID string.
 * @returns {String} The generated GUID.
 * @example af8a8416-6e18-a307-bd9c-f2c947bbb3aa
 * @author Slavik Meltser (slavik@meltser.info).
 * @link http://slavik.meltser.info/?p=142
 */
function guid() {
    function _p8(s) {
        var p = (Math.random().toString(16)+"000000000").substr(2,8);
        return s ? "-" + p.substr(0,4) + "-" + p.substr(4,4) : p ;
    }
    return _p8() + _p8(true) + _p8(true) + _p8();
}

var createCookie = function(name, value, days) {
    var expires;
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toGMTString();
    }
    else {
        expires = "";
    }
    document.cookie = name + "=" + value + expires + "; path=/";
}

function getCookie(c_name) {
    if (document.cookie.length > 0) {
        var c_start = document.cookie.indexOf(c_name + "=");
        if (c_start != -1) {
            c_start = c_start + c_name.length + 1;
            var c_end = document.cookie.indexOf(";", c_start);
            if (c_end == -1) {
                c_end = document.cookie.length;
            }
            return unescape(document.cookie.substring(c_start, c_end));
        }
    }
    return "";
}

var HttpClient = function() {
    this.get = function(aUrl, aCallback) {
        return this.request("GET", aUrl, aCallback);
    }

    this.post = function(aUrl, aCallback) {
        return this.request("POST", aUrl, aCallback);
    }

    this.put = function(aUrl, aCallback) {
        return this.request("PUT", aUrl, aCallback);
    }

    this.request = function(method, aUrl, aCallback) {
        var anHttpRequest = new XMLHttpRequest();
        anHttpRequest.onreadystatechange = function() {
        if (anHttpRequest.readyState == 4)
            aCallback(anHttpRequest.status, JSON.parse(anHttpRequest.responseText));
        }

        anHttpRequest.open( method, aUrl, true );
        anHttpRequest.send( null );
    };
}

var cellWidth = 64;
var cellSpacing = 10;
var apiUrl = 'api/games';
var previousFencePost = null;

function player() {
    return {'id':getCookie('player_id'), 'name':getCookie('player_name')};
}

function currentGame() {
    return {"id":getCookie('game_id')};
}

function setup() {
    var p = player();
    var gid = currentGame().id;
    if (p.id.length == 0 || p.name.length == 0) {
        document.getElementById('playerInfo').style.display = 'block';
    } else if (gid.length > 0) {

        var http = new HttpClient();
        http.get(apiUrl + '/' + gid + '?id=' + p.id, function(status, game) {
            if (status != 200) {
                alert(game.message);
                return;
            }

            startGame(game);
        });
    } else {
        showGames(p);
    }
}

function login() {
    var nameView = document.getElementById('player_name');
    if (nameView.value.length > 0) {
        createCookie('player_id', guid(), 30);
        createCookie('player_name', nameView.value, 30);
        showGames(player());
    } else {
        alert('You must enter a name');
    }
    return false;
}

function showGames(player) {
    document.getElementById('welcomeName').innerHTML = 'Welcome ' + player.name + '!';
    document.getElementById('playerInfo').style.display = 'none';
    loadGames();
}

function formatTime(time) {
    var lastMoveAt = new Date(time);
    return lastMoveAt.toLocaleTimeString() + ' on ' + lastMoveAt.toLocaleDateString();
}

function loadGames() {

    var p = player();

    var http = new HttpClient();
    http.get(apiUrl, function(status, games) {
        if (status != 200) {
            alert(games.message);
            return;
        }

        var table = document.getElementById('publicGames');
        games.forEach(function(game) {
            var row = table.insertRow();

            var idCell = row.insertCell();
            idCell.appendChild(document.createTextNode(game.id));
            var age = row.insertCell();
            age.appendChild(document.createTextNode(formatTime(game.lastMoveAt)));
            var join = row.insertCell();
            var a = document.createElement('a');
            var linkText = document.createTextNode("Join");
            a.appendChild(linkText);
            a.title = "Join game " + game.id;
            var joinUrl = apiUrl + '?id='+p.id+'&name='+p.name+'&gameId='+game.id;
            a.href = joinUrl;
            a.onclick = function(e) {
                e.preventDefault();
                http.put(joinUrl, function(postStatus, joined) {
                    if (postStatus != 200) {
                        alert(joined.message);
                        return;
                    }

                    startGame(joined);
                });
            }
            join.appendChild(a);

        });
        document.getElementById('gameList').style.display = 'block';
    });
}

function createGame() {
    var p = player();

    var http = new HttpClient();
    http.post(apiUrl+'?name='+p.name+'&id='+p.id  , function(status, game) {
        if (status != 200) {
            alert(game.message);
            return;
        }

        startGame(game);
    });
}

function startGame(game) {
    document.getElementById('gameList').style.display = 'none';
    document.getElementById('board').style.display = 'block';
    createCookie('game_id', game.id, 30);
    renderGame(game);
}

function renderGame(game) {
    console.log('rendering game');

    var boardView = document.getElementById("board");
    while (boardView.firstChild) {
        boardView.removeChild(boardView.firstChild);
    }

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
            post.style.left = ((cellSpacing + ((cellWidth * x) + (cellSpacing * x)))-((26/2)+6)) + 'px';
            post.style.top = ((cellSpacing + ((cellWidth * y) + (cellSpacing * y)))-((26/2)+6)) + 'px';
            post.addEventListener("click", postHandler, false);
            boardView.appendChild(post);
        }
    }

    if (game.me != null) {
        boardView.appendChild(playerView(game.me, 'me'));
    }
    if (game.you != null) {
        boardView.appendChild(playerView(game.you, 'you'));
    }
}

function playerView(player, id) {
    var view = document.createElement('div');
    view.setAttribute('class', 'player');
    view.setAttribute('id', id);
    var pos = positionForCell(player.position);
    view.style.top = pos.y + 'px';
    view.style.left = pos.x + 'px';
    view.innerHTML = player.name;
    return view;
}

function positionForCell(index) {
    return {'y':(10 + (Math.floor(index / 9))*(64+10)),'x':(((index % 9) * 64) + ((index % 9) * 10) + 10)};
}

function moveHandler(e) {
    var end = e.target.id.replace('cell','');
    console.log("moving player to " + end);
    var http = new HttpClient();
    http.post(apiUrl + '/' + currentGame().id + '/move?id=' + player().id + '&end=' + end, function(status, game) {
        if (status != 200) {
            alert(game.message);
            return;
        }

        renderGame(game);
    });
}

function postHandler(e) {
    if (previousFencePost == null) {
        previousFencePost = e.target;
        e.target.style.opacity = 1;
    } else {
        previousFencePost.style.opacity = 0.1;
        var start = previousFencePost.id.replace('post','');
        var end = e.target.id.replace('post','');
        previousFencePost = null;
        console.log("building fence from " + start + " to " + end);
        var http = new HttpClient();
        http.post(apiUrl + '/' + currentGame().id + '/fence?id=' + player().id + '&start=' + start + '&end=' + end, function(status, game) {
            if (status != 200) {
                alert(game.message);
                return;
            }

            renderGame(game);
        });
    }
}