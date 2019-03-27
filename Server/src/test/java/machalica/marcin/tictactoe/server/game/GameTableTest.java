package machalica.marcin.tictactoe.server.game;

import machalica.marcin.tictactoe.server.server.ClientHandler;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class GameTableTest {
    private ClientHandler player1;
    private ClientHandler player2;
    private ClientHandler player3;
    private GameTable gameTable = new GameTable(1);

    @Before
    public void setup() {
        player1 = new ClientHandler(mock(Socket.class), mock(BufferedReader.class), mock(PrintWriter.class));
        player2 = new ClientHandler(mock(Socket.class), mock(BufferedReader.class), mock(PrintWriter.class));
        player3 = new ClientHandler(mock(Socket.class), mock(BufferedReader.class), mock(PrintWriter.class));
    }

    @Test
    public void isFree() {
        assertTrue(gameTable.isFree());

        gameTable.assignPlayer(player1);
        assertTrue(gameTable.isFree());

        gameTable.assignPlayer(player2);
        assertFalse(gameTable.isFree());

        gameTable.assignPlayer(player3);
        assertFalse(gameTable.isFree());

        gameTable.removePlayer(player1);
        assertTrue(gameTable.isFree());

        gameTable.assignPlayer(player3);
        assertFalse(gameTable.isFree());
    }

    @Test
    public void assignPlayer() {
        assertNull(gameTable.getPlayer1());
        assertNull(gameTable.getPlayer2());

        gameTable.assignPlayer(player1);
        assertTrue(gameTable.getPlayer1() == player1);
        assertNull(gameTable.getPlayer2());

        gameTable.assignPlayer(player2);
        assertTrue(gameTable.getPlayer1() == player1);
        assertTrue(gameTable.getPlayer2() == player2);

        gameTable.assignPlayer(player3);
        assertTrue(gameTable.getPlayer1() == player1);
        assertTrue(gameTable.getPlayer2() == player2);
    }

    @Test
    public void removePlayer() {
        gameTable.assignPlayer(player1);
        gameTable.assignPlayer(player2);

        gameTable.removePlayer(player3);
        assertTrue(gameTable.getPlayer1() == player1);
        assertTrue(gameTable.getPlayer2() == player2);

        gameTable.removePlayer(player2);
        assertTrue(gameTable.getPlayer1() == player1);
        assertTrue(gameTable.getPlayer2() == null);

        gameTable.assignPlayer(player2);

        gameTable.removePlayer(player1);
        assertTrue(gameTable.getPlayer1() == null);
        assertTrue(gameTable.getPlayer2() == player2);

        gameTable.removePlayer(player2);
        assertTrue(gameTable.getPlayer1() == null);
        assertTrue(gameTable.getPlayer2() == null);

        gameTable.removePlayer(player3);
        assertTrue(gameTable.getPlayer1() == null);
        assertTrue(gameTable.getPlayer2() == null);
    }
}