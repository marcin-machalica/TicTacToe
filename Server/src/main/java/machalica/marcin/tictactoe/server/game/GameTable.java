package machalica.marcin.tictactoe.server.game;

import machalica.marcin.tictactoe.server.server.ClientHandler;

import java.util.Arrays;
import java.util.Random;

public class GameTable {
    private int id;
    private volatile ClientHandler[] players = new ClientHandler[2];
    private ClientHandler playerTurn;
    private Boolean isPlayer1Turn;
    private Boolean isPlayer2Turn;

    public GameTable(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isFree() {
        if(players[0] == null || players[1] == null) {
            return true;
        } else {
            return false;
        }
    }

    public ClientHandler getPlayer1() {
        return players[0];
    }

    public ClientHandler getPlayer2() {
        return players[1];
    }

    public ClientHandler[] getPlayers() {
        return players;
    }

    public boolean assignPlayer(ClientHandler player) {
        if(players[0] == null) {
            players[0] = player;
            isPlayer1Turn = isPlayer2Turn == null ? new Random().nextBoolean() : !isPlayer2Turn;

            return true;
        } else if(players[1] == null) {
            players[1] = player;
            isPlayer2Turn = isPlayer1Turn == null ? new Random().nextBoolean() : !isPlayer1Turn;

            return true;
        } else {
            return false;
        }
    }

    public boolean removePlayer(ClientHandler player) {
        if(players[0] == player) {
            players[0] = null;
            isPlayer1Turn = null;
            return true;
        } else if(players[1] == player) {
            players[1] = null;
            isPlayer2Turn = null;
            return true;
        } else {
            return false;
        }
    }

    public ClientHandler getOpponent (ClientHandler player) {
        if (getPlayer1() == player) {
            return getPlayer2();
        } else if (getPlayer2() == player) {
            return getPlayer1();
        } else {
            return null;
        }
    }

    public Boolean isItMyTurn(ClientHandler player) {
        if(players[0] == player) {
            return isPlayer1Turn;
        } else if(players[1] == player) {
            return isPlayer2Turn;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "GameTable{" +
                "id=" + id +
                ", players=" + Arrays.toString(players) +
                '}';
    }
}