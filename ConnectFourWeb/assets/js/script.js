/**
 * Created by Tom Dobbelaere on 22/06/2016.
 */

var socket;
var joinIdentifier = null;
var username = null;
var selectedtype = null;
var host = "digaly.ddns.net";

$(document).ready(function () {
    $("#connecting").text("Connecting with " + host + "...");

    socket = io(host + ":3001");

    socket.on("connect", function(){
        $("#connecting").hide();
    });

    socket.on("other dropped piece", function(data) {
        var dropInfo = JSON.parse(data);

        console.log(dropInfo.x[0]);
        console.log(dropInfo.y[0]);

        dropPieceAt(dropInfo.type[0], dropInfo.x[0], dropInfo.y[0]);
    });

    socket.on("current player changed", function(data) {
        if (data == username)
        {
            $("#selection").show();
        }
        else
        {
            $("#selection").hide();
        }

        $("#gamestatus").text(data + "'s turn!");
    });

    socket.on("create response", function(id) {
        var link = window.location.origin + "/connectfour/?join=" + id;

        $("#link").html("<b>Game created, give this link to your friend (or enemy): </b>" + "<br/>" + link);
        //console.log(link);
    });

    socket.on("game over", function(data) {
        $("#gamestatus").text(data + " won!");
    });

    loadUsername();
    $("#username").on("keyup", saveUsername);

    joinIdentifier = window.location.search.split("=")[1];

    if (typeof(joinIdentifier) != 'undefined') {
        setSelectedType("sun");
        $("#create-game").find("h1").text("Join game")
        $("#create").text("Join").on("click", joinGame);
    } else {
        setSelectedType("moon");
        $("#create").on("click", createGame);
    }

    $(".selection").hide();
    $("#select-type").find("a").on("click", selectType);
    $(".board").on('mousemove', updateDropLocation).on('click', dropPieceEvent).hide();;
});

var selectType = function(e) {
    e.preventDefault();

    setSelectedType($(this).attr("data-type"));
};

var setSelectedType = function(type) {
    selectedtype = type;
    $("#select-type").find("a.selected").removeClass("selected");
    $("#select-type").find("a." + selectedtype).addClass("selected");
    $("#selection").removeClass().addClass("piece").addClass(selectedtype);
};

var createGame = function(e) {
    e.preventDefault();

    username = $("#username").val();

    $(".board").empty().show();

    socket.emit("create game", JSON.stringify({
        type: selectedtype,
        username: username
    }));

    $("#select-type").hide();
    $("#create-game").hide();
    $(".selection").show();
    $("#start-new").show();
};

var joinGame = function(e) {
    e.preventDefault();

    username = $("#username").val();

    socket.emit("join game", JSON.stringify({
        lobbyid: joinIdentifier,
        type: selectedtype,
        username: username
    }));

    $("#select-type").hide();
    $("#create-game").hide();
    $(".board").show();
    $(".selection").show();
    $("#start-new").show();
};

var saveUsername = function() {
    var username = $("#username").val();

    if (typeof(localStorage) != 'undefined')
    {
        localStorage.username = username;
    }
};

var loadUsername = function() {
    if (typeof(localStorage) != 'undefined')
    {
        if (typeof(localStorage.username) != 'undefined')
        {
            $("#username").val(localStorage.username);
        }
    }
};

var dropPieceAt = function (type, x, y) {
    var newPiece = $("<div class='piece " + type + "'></div>");

    $(".board").append(newPiece);

    newPiece.css("left", (x) * 85 + "px");
    newPiece.css("top");
    newPiece.css("top", (y) * 85 + "px");
};

var updateDropLocation = function (e) {
    var parentOffset = $(this).parent().offset();
    var parentWidth = $(this).parent().width();

    var relX = e.pageX - parentOffset.left;
    var relY = e.pageY - parentOffset.top;


    var selectedX = Math.floor((relX) / (parentWidth / 7));

    $("#selection").css("left", selectedX * 85 + "px");
};

var dropPieceEvent = function (e) {
    var parentOffset = $(this).parent().offset();
    var parentWidth = $(this).parent().width();

    var relX = e.pageX - parentOffset.left;
    var relY = e.pageY - parentOffset.top;

    var selectedX = Math.floor((relX) / (parentWidth / 7));

    socket.emit("drop piece", selectedX);

    /*if (pieceType == "moon") pieceType = "sun";
    else pieceType = "moon";

    $("#selection").removeClass().addClass("piece").addClass(pieceType);*/
};