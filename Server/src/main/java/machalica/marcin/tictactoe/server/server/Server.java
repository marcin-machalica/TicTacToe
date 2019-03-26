package machalica.marcin.tictactoe.server.server;

import machalica.marcin.tictactoe.server.game.GameTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    private final int PORT = 9999;
    private ServerSocket serverSocket;

    private static final List<GameTable> gameTablesList = Collections.synchronizedList(new ArrayList<>());

    public static void main(String[] args) {
        Server server = new Server();
        server.runServer();
    }

    private void runServer() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
        System.out.println("Waiting for connections...");

        while (true) {
            Socket socket = null;
            BufferedReader in = null;
            PrintWriter out = null;
            ClientHandler clientHandler = null;

            try {
                socket = serverSocket.accept();
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream());
                out.flush();

                clientHandler = new ClientHandler(socket, in, out);
                int tableId = assignPlayerToGameTable(clientHandler);
                clientHandler.setTableId(tableId);
                Thread thread = new Thread(clientHandler);
                thread.start();

                System.out.println("Connection established with: " + clientHandler.getName());
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    if(clientHandler != null && clientHandler.getTableId() != -1) {
                        removePlayer(clientHandler.getTableId() , clientHandler);
                    }
                    if(in != null) in.close();
                    if(out != null) out.close();
                    if(socket != null) socket.close();
                } catch (Exception ex2) {
                    ex2.printStackTrace();
                }
                System.out.println("Connection closed");
            }
        }
    }

    private int assignPlayerToGameTable(ClientHandler clientHandler) {
        int count = 0;
        boolean foundEmpty = false;

        for(GameTable gameTable : gameTablesList) {
            count++;
            if(gameTable.isFree()) {
                foundEmpty = true;
                break;
            }
        }
        if(!foundEmpty) {
            gameTablesList.add(new GameTable(count));
        }
        gameTablesList.get(count).assignPlayer(clientHandler);
        System.out.printf("Assigned player %s to game table id: %d\n", clientHandler.getName(), gameTablesList.get(count).getId());
        return count;
    }

    public static void removePlayer(int gameTableId, ClientHandler clientHandler) {
        gameTablesList.get(gameTableId).removePlayer(clientHandler);
        System.out.printf("Removed player %s from game table id: %d\n", clientHandler.getName(), gameTableId);
    }

    public static ClientHandler[] getPlayers(int gameTableId) {
        return gameTablesList.get(gameTableId).getPlayers();
    }
}