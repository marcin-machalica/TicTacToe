package machalica.marcin.tictactoe.client.client;

import javafx.application.Platform;
import machalica.marcin.tictactoe.client.main.Main;
import machalica.marcin.tictactoe.communication.AuthenticationMessage;
import machalica.marcin.tictactoe.communication.ChatMessage;
import machalica.marcin.tictactoe.communication.ExitMessage;
import machalica.marcin.tictactoe.communication.Message;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {
    private static final Client instance = new Client();
    private static final Logger logger = Logger.getLogger(Client.class);
    private final String IP_ADDRESS = "localhost";
    private final int PORT = 9999;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String name;
    private volatile boolean isAuthenticated;

    private Client() { }

    public static Client getInstance() {
        return instance;
    }

    @Override
    public void run() {
        runClient();
    }

    private void runClient() {
        try {
            connectToServer();
            waitForAuthentication();
            if (isAuthenticated) {
                Main.setGameScene();
                chat();
            }
        } catch (Exception ex) {
            logger.error(ex);
        } finally {
            closeEverything();
        }
    }

    private void connectToServer() throws IOException {
        logger.info("Attempting to connect...");
        socket = new Socket(IP_ADDRESS, PORT);
        setupStreams();
        logger.info("Connected to: " + socket.getInetAddress().getHostName());
    }

    private void setupStreams() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    private void waitForAuthentication() {
        logger.info("Waiting for authentication");
        while (!isAuthenticated);
        logger.info("Authenticated");
    }

    private void chat() throws IOException {
        Message msg = null;

        while (true) {
            try {
                msg = (Message) in.readObject();
            } catch (ClassNotFoundException ex) {
                logger.error(ex);
            }

            if (!(msg instanceof ChatMessage)) {
                break;
            } else {
                logger.info(((ChatMessage) msg).getMessage());
            }
        }
    }

    private void closeEverything() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException ex) {
            logger.error(ex);
        } finally {
            logger.info("Connection closed\n");
            Platform.exit();
            System.exit(0);
        }
    }

    public void sendMessage(Message msg) {
        try {
            if (msg != null) {
                out.writeObject(msg);
                out.flush();
            }
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    public boolean authenticate(String login, char[] password) {
        logger.info("Authenticating");
        AuthenticationMessage authMsg = new AuthenticationMessage(login, password);
        sendMessage(authMsg);

        for(int i = 0; i < password.length; i++) {
            password[i] = '\0';
        }

        try {
            Object obj = in.readObject();

            if (!(obj instanceof Boolean)) {
                closeEverything();
                return false;
            } else {
                isAuthenticated = (Boolean) obj;

                if (isAuthenticated) {
                    setName(login);
                }

                return isAuthenticated;
            }
        } catch (IOException | ClassNotFoundException ex) {
            logger.error(ex);
            closeEverything();
            return false;
        }
    }

    public void requestClose() {
        sendMessage(new ExitMessage());
        if (!isAuthenticated) {
            closeEverything();
        }
    }

    private void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }
}