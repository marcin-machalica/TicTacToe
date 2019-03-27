package machalica.marcin.tictactoe.server.server;

import machalica.marcin.tictactoe.server.game.GameTable;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    private static final Server instance = new Server();
    private static final Logger logger = Logger.getLogger(Server.class);
    private final int PORT = 9999;
    private ServerSocket serverSocket;

    private List<GameTable> gameTablesList = Collections.synchronizedList(new ArrayList<>());

    private Server() { }

    public static Server getInstance() {
        return instance;
    }

    public void runServer() {
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException ex) {
            logger.error(ex);
            System.exit(0);
        }
        logger.info("Waiting for connections...");

        while (true) {
            Socket socket = null;
            ObjectInputStream in = null;
            ObjectOutputStream out = null;
            ClientHandler clientHandler = null;

            try {
                socket = serverSocket.accept();
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                clientHandler = new ClientHandler(socket, in, out);
                logger.info("Connection established with: " + clientHandler.getName());
                assignPlayerToGameTable(clientHandler);

                Thread thread = new Thread(clientHandler);
                thread.start();
            } catch (Exception ex) {
                logger.error(ex);
                try {
                    if(clientHandler != null && clientHandler.getTableId() != -1) {
                        removePlayer(clientHandler.getTableId() , clientHandler);
                    }
                    if(in != null) in.close();
                    if(out != null) out.close();
                    if(socket != null) socket.close();
                } catch (Exception ex2) {
                    logger.error(ex2);
                }
                logger.info("Connection closed");
            }
        }
    }

    private boolean assignPlayerToGameTable(ClientHandler clientHandler) {
        int count = 0;
        boolean foundEmpty = false;

        for(GameTable gameTable : gameTablesList) {
            if(gameTable.isFree()) {
                foundEmpty = true;
                break;
            }
            count++;
        }
        if(!foundEmpty) {
            gameTablesList.add(new GameTable(count));
        }
        boolean wasSuccessful = gameTablesList.get(count).assignPlayer(clientHandler);
        if(wasSuccessful) {
            clientHandler.setTableId(count);
            logger.info(String.format("Assigned player %s to game table id: %d", clientHandler.getName(), gameTablesList.get(count).getId()));
        } else {
            logger.error(String.format("Error while assigning player %s to game table id: %d", clientHandler.getName(), gameTablesList.get(count).getId()));
        }
        return wasSuccessful;
    }

    public boolean removePlayer(int gameTableId, ClientHandler clientHandler) {
        boolean wasSuccessful = false;

        if(clientHandler == null) {
            logger.error("Player cannot be null");
        } else if(gameTableId != clientHandler.getTableId()) {
            logger.error("Passed game table id and player's game table id don't match");
        } else if(gameTableId < 0 || gameTableId > gameTablesList.size()) {
            logger.error("Not existing game table id: " + gameTableId);
        } else {
            wasSuccessful = gameTablesList.get(gameTableId).removePlayer(clientHandler);
            if(wasSuccessful) {
                clientHandler.setTableId(-1);
                logger.info(String.format("Removed player %s from game table id: %d", clientHandler.getName(), gameTableId));
            } else {
                logger.info(String.format("Player %s not found at game table id: %d", clientHandler.getName(), gameTableId));
            }
        }
        return wasSuccessful;
    }

    public ClientHandler[] getPlayers(int gameTableId) {
        if(gameTableId < 0 || gameTableId > gameTablesList.size()) {
            logger.error("Not existing game table id: " + gameTableId);
            return new ClientHandler[] {};
        }
        return gameTablesList.get(gameTableId).getPlayers();
    }
}