package machalica.marcin.tictactoe.server.server;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ServerTest {
    private Server server;
    private static Method method;
    private ClientHandler player1;
    private ClientHandler player2;
    private ClientHandler player3;
    private ClientHandler player4;

    @BeforeClass
    public static void setupBeforeClass() throws NoSuchMethodException {
        method = Server.class.getDeclaredMethod("assignPlayerToGameTable", ClientHandler.class);
        method.setAccessible(true);
    }

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        server = Server.getInstance();

        player1 = new ClientHandler(mock(Socket.class), mock(BufferedReader.class), mock(PrintWriter.class));
        player2 = new ClientHandler(mock(Socket.class), mock(BufferedReader.class), mock(PrintWriter.class));
        player3 = new ClientHandler(mock(Socket.class), mock(BufferedReader.class), mock(PrintWriter.class));
        player4 = new ClientHandler(mock(Socket.class), mock(BufferedReader.class), mock(PrintWriter.class));

        Field instance = Server.class.getDeclaredField("gameTablesList");
        instance.setAccessible(true);
        instance.set(server, Collections.synchronizedList(new ArrayList<>()));
    }

    @Test
    public void assignPlayerToGameTable() throws InvocationTargetException, IllegalAccessException {
        method.invoke(server, player1);
        assertEquals(0, player1.getTableId());

        method.invoke(server, player2);
        assertEquals(0, player2.getTableId());

        method.invoke(server, player3);
        assertEquals(1, player3.getTableId());

        server.removePlayer(0, player2);
        method.invoke(server, player4);
        assertEquals(0, player4.getTableId());

        method.invoke(server, player2);
        assertEquals(1, player2.getTableId());
    }

    @Test
    public void removePlayer() throws InvocationTargetException, IllegalAccessException {
        method.invoke(server, player1);
        method.invoke(server, player2);
        method.invoke(server, player3);
        method.invoke(server, player4);

        server.removePlayer(1, player3);
        server.removePlayer(0, player2);

        ClientHandler[] players0 = server.getPlayers(0);
        ClientHandler[] players1 = server.getPlayers(1);

        assertEquals(player1, players0[0]);
        assertEquals(null, players0[1]);
        assertEquals(null, players1[0]);
        assertEquals(player4, players1[1]);

        assertEquals(0, player1.getTableId());
        assertEquals(-1, player2.getTableId());
        assertEquals(-1, player3.getTableId());
        assertEquals(1, player4.getTableId());

        assertFalse(server.removePlayer(1, player3));
        assertEquals(null, players1[0]);
        assertEquals(player4, players1[1]);

        assertFalse(server.removePlayer(1, null));
        assertFalse(server.removePlayer(-1, player1));
        assertFalse(server.removePlayer(100, player1));
        assertFalse(server.removePlayer(1, player1));
    }

    @Test
    public void getPlayers() throws InvocationTargetException, IllegalAccessException {
        method.invoke(server, player1);
        method.invoke(server, player2);
        method.invoke(server, player3);
        method.invoke(server, player4);

        assertTrue(server.getPlayers(0)[0].equals(player1));
        assertTrue(server.getPlayers(0)[1].equals(player2));
        assertTrue(server.getPlayers(1)[0].equals(player3));
        assertTrue(server.getPlayers(1)[1].equals(player4));

        assertTrue(server.getPlayers(-1).length == 0);
        assertTrue(server.getPlayers(100).length == 0);
    }
}