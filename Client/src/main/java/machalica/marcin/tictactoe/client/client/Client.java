package machalica.marcin.tictactoe.client.client;

import javafx.application.Platform;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {
    private static final Client instance = new Client();
    private static final Logger logger = Logger.getLogger(Client.class);
    private final String IP_ADDRESS = "localhost";
    private final int PORT = 9999;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String name;

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
            chat();
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
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());
        out.flush();
    }

    private void chat() throws IOException {
        String msg;
        do {
            msg = in.readLine();
            if(msg == null) {
                logger.error("null");
                break;
            }
            if(this.name == null) {
                assignName(msg);
                continue;
            }
            logger.info(msg);
        } while(!msg.equals("ENDCONNECTION"));
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

    public void sendMessage(String msg) {
        if(msg == null) return;
        if(this.name != null || msg.equals("ENDCONNECTION")) {
            out.println(msg);
            out.flush();
        }
    }

    private void assignName(String msg) {
        String[] nameMsg = msg.split("SERVER:CLIENTNAME#");
        if(nameMsg.length == 2) {
            this.name = nameMsg[1];
        }
    }

    public String getName() {
        return this.name;
    }
}