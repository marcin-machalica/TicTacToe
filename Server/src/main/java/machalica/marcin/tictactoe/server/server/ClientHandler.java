package machalica.marcin.tictactoe.server.server;

import machalica.marcin.tictactoe.communication.AuthenticationMessage;
import machalica.marcin.tictactoe.communication.ChatMessage;
import machalica.marcin.tictactoe.communication.ExitMessage;
import machalica.marcin.tictactoe.communication.Message;
import machalica.marcin.tictactoe.communication.game.AssignGameTableMessage;
import machalica.marcin.tictactoe.communication.game.StartGameMessage;
import machalica.marcin.tictactoe.server.game.GameTable;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class);
    private static final Server server = Server.getInstance();
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private final String address;
    private String name;
    private int tableId = -1;
    private volatile boolean isAuthenticated;
    private volatile boolean isRunning;
    private volatile boolean isInGame;
    private GameTable gameTable;

    public ClientHandler(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.address = socket.getRemoteSocketAddress().toString();
    }

    public int getTableId() {
        return tableId;
    }

    protected void setTableId(int tableId) {
        this.tableId = tableId;
    }

    private void handleInput() {
        try {
            while (isRunning) {
                Object msg = in.readObject();

                if (msg instanceof AuthenticationMessage && !isAuthenticated) {
                    authenticate((AuthenticationMessage) msg);
                } else {
                    isRunning = false;
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
            isRunning = false;
        }
    }

    @Override
    public void run() {
        isRunning = true;
        try {
            new Thread(() -> handleInput()).start();
            while (isRunning && !isAuthenticated);
            if (isAuthenticated && server.assignPlayerToGameTable(this)) {
                out.writeObject(new AssignGameTableMessage(tableId));
                out.flush();

                gameTable = server.getGameTable(this);
                if (gameTable != null) {
                    while (isRunning && gameTable.isFree());

                    if (isRunning && !gameTable.isFree()) {
                        ClientHandler player = this;
                        ClientHandler opponent = gameTable.getOpponent(player);
                        if (opponent != null) {
                            out.writeObject(new StartGameMessage(player.getName(), opponent.getName(), gameTable.isItMyTurn(this)));

                            isInGame = true;
                        }
                    }

                    if (isRunning && isInGame) {
                        chat();
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex);
        } finally {
            closeEverything();
        }
    }

    private void authenticate(AuthenticationMessage authMsg) throws IOException {
        isAuthenticated = server.authenticate(authMsg);
        isRunning = true;

        if (isAuthenticated) {
            this.name = authMsg.getLogin();
            logger.info(getAddress() + " Authenticated as " + this.name);
        }

        out.writeObject(isAuthenticated);
        out.flush();
    }

    private void chat() throws IOException {
        Message msg = null;
        out.writeObject(new ChatMessage("Hello " + this.name));
        out.flush();

        while (true) {
            try {
                msg = (Message) in.readObject();
            } catch (ClassNotFoundException ex) {
                logger.error(ex);
            }

            if (!(msg instanceof ChatMessage)) {
                break;
            } else {
                out.writeObject(msg);
                out.flush();
            }
        }
    }

    private void closeEverything() {
        try {
            gameTable = null;
            server.removePlayer(tableId, this);
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (Exception ex) {
            logger.error(ex);
        }
        logger.info("Closed connection with " + getAddress());
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientHandler that = (ClientHandler) o;
        return socket.equals(that.socket) &&
                in.equals(that.in) &&
                out.equals(that.out);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket, in, out);
    }

    @Override
    public String toString() {
        return "ClientHandler{" +
                "address='" + socket.getRemoteSocketAddress().toString() + '\'' +
                ", tableId=" + tableId +
                '}';
    }
}