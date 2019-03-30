package machalica.marcin.tictactoe.server.server;

import machalica.marcin.tictactoe.communication.AuthenticationMessage;
import machalica.marcin.tictactoe.server.game.GameTable;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    private static final Server instance = new Server();
    private static final Logger logger = Logger.getLogger(Server.class);
    private static final int PORT = 9999;
    private ServerSocket serverSocket;

    private List<GameTable> gameTablesList = Collections.synchronizedList(new ArrayList<>());
    private HashSet<String> loggedInUsers = new HashSet<>();
    private HashMap<String, char[]> allUsers = new HashMap<>();

    private Server() { }

    public static Server getInstance() {
        return instance;
    }

    public void runServer() {
        populateUsers();

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
                logger.info("Connection established with: " + socket.getRemoteSocketAddress().toString());

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
                logger.info(socket.getRemoteSocketAddress().toString() + " - Connection closed");
            }
        }
    }

    public boolean assignPlayerToGameTable(ClientHandler clientHandler) {
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
            logger.error(clientHandler.getAddress() + " - Player cannot be null");
        } else if (!clientHandler.isAuthenticated() || clientHandler.getName() == null ||
            clientHandler.getTableId() == -1) {
            logger.error(clientHandler.getAddress() + " - Not assigned player, removal ommited");
            wasSuccessful = true;
        } else if(gameTableId != clientHandler.getTableId()) {
            logger.error(clientHandler.getAddress() + " - Passed game table id and player's game table id don't match");
        } else if(gameTableId < 0 || gameTableId > gameTablesList.size()) {
            logger.error(clientHandler.getAddress() + " - Not existing game table id: " + gameTableId);
        } else {
            wasSuccessful = gameTablesList.get(gameTableId).removePlayer(clientHandler);
            loggedInUsers.remove(clientHandler.getName());
            if(wasSuccessful) {
                clientHandler.setTableId(-1);
                logger.info(String.format(clientHandler.getAddress() + " - Removed player %s from game table id: %d", clientHandler.getName(), gameTableId));
            } else {
                logger.info(String.format(clientHandler.getAddress() + " - Player %s not found at game table id: %d", clientHandler.getName(), gameTableId));
            }
        }
        return wasSuccessful;
    }

    public boolean authenticate (AuthenticationMessage authMsg) {
        String login = authMsg.getLogin();
        char[] password = authMsg.getPassword();

        if (login != null && !loggedInUsers.contains(login)) {
            if (Arrays.equals(password, allUsers.get(login))) {
                for(int i = 0; i < password.length; i++) {
                    password[i] = '\0';
                }

                loggedInUsers.add(login);
                return true;
            }
        }
        return false;
    }

    public ClientHandler[] getPlayers(int gameTableId) {
        if(gameTableId < 0 || gameTableId > gameTablesList.size()) {
            logger.error("Not existing game table id: " + gameTableId);
            return new ClientHandler[] {};
        }
        return gameTablesList.get(gameTableId).getPlayers();
    }

    public GameTable getGameTable(ClientHandler clientHandler) {
        if(clientHandler.getTableId() < 0 || clientHandler.getTableId() > gameTablesList.size()) {
            logger.error("Not existing game table id: " + clientHandler.getTableId());
            return null;
        }
        return gameTablesList.get(clientHandler.getTableId());
    }

    private void populateUsers() {
        allUsers.put("1", new char[] {'1'});
        allUsers.put("2", new char[] {'2'});
        allUsers.put("3", new char[] {'3'});
        allUsers.put("q", new char[] {'q'});
        allUsers.put("w", new char[] {'w'});
        allUsers.put("e", new char[] {'e'});
        allUsers.put("a", new char[] {'a'});
        allUsers.put("s", new char[] {'s'});
        allUsers.put("d", new char[] {'d'});
    }
}