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
    private static final String IP_ADDRESS = "localhost";
    private static final int PORT = 9999;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String name;
    private volatile boolean isAuthenticated;
    private volatile boolean isRunning;

    private Client() { }

    public static Client getInstance() { return instance; }

    @Override
    public void run() {
        runClient();
    }

    private void runClient() {
        try {
            connectToServer();
            waitForAuthentication();
            if (isRunning && isAuthenticated) {
                Main.setGameScene();
                chat();
            }
        } catch (EOFException ex) {
          closeEverything(true);
        } catch (Exception ex) {
            logger.error(ex);
        } finally {
            closeEverything(false);
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
        isRunning = true;
        logger.info("Waiting for authentication");
        while (!isAuthenticated && isRunning);
        if (isRunning && isAuthenticated) {
            logger.info("Authenticated");
        }
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
                closeEverything(true);
            } else {
                logger.info(((ChatMessage) msg).getMessage());
            }
        }
    }

    private void closeEverything(boolean shouldExit) {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException ex) {
            logger.error(ex);
        } finally {
            isRunning = false;
            logger.info("Connection closed\n");
            if (shouldExit) {
                Platform.exit();
                System.exit(0);
            }
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
                logger.info("Authentication failed");
                isRunning = false;
                return false;
            } else {
                isAuthenticated = (Boolean) obj;

                if (isAuthenticated) {
                    setName(login);
                    return isAuthenticated;
                } else {
                    logger.info("Authentication failed");
                    isRunning = false;
                    return false;
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            logger.error("Authentication failed - " + ex);
            isRunning = false;
            return false;
        }
    }

    public void requestClose() {
        if (isAuthenticated) {
            sendMessage(new ExitMessage());
        }
        else {
            closeEverything(true);
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

    public boolean isRunning() {
        return isRunning;
    }
}