package machalica.marcin.tictactoe.server.game;

import machalica.marcin.tictactoe.server.server.ClientHandler;

import java.util.Arrays;

public class GameTable {
    private int id;
    private ClientHandler[] players = new ClientHandler[2];

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
            return true;
        } else if(players[1] == null) {
            players[1] = player;
            return true;
        } else {
            return false;
        }
    }

    public boolean removePlayer(ClientHandler player) {
        if(players[0] == player) {
            players[0] = null;
            return true;
        } else if(players[1] == player) {
            players[1] = null;
            return true;
        } else {
            return false;
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