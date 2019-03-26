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

    public void assignPlayer(ClientHandler player) {
        if(players[0] == null) {
            players[0] = player;
        } else if(players[1] == null) {
            players[1] = player;
        }
    }

    public void removePlayer(ClientHandler player) {
        if(players[0].equals(player)) {
            players[0] = null;
        } else if(players[1].equals(player)) {
            players[1] = null;
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