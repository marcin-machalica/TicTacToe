package machalica.marcin.tictactoe.server.server;

import machalica.marcin.tictactoe.communication.ChatMessage;
import machalica.marcin.tictactoe.communication.ExitMessage;
import machalica.marcin.tictactoe.communication.Message;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class);
    private final Socket socket;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private static final Server server = Server.getInstance();
    private String name;
    private int tableId = -1;
    private static int count;

    public ClientHandler(Socket socket, ObjectInputStream in, ObjectOutputStream out) {
        this.socket = socket;
        this.in = in;
        this.out = out;
        this.name = "Client " + ++count;
    }

    public int getTableId() {
        return tableId;
    }

    protected void setTableId(int tableId) {
        this.tableId = tableId;
    }

    @Override
    public void run() {
        try {
            chat();
        } catch (Exception ex) {
            logger.error(ex);
        } finally {
            try {
                server.removePlayer(tableId, this);
                if (in != null) in.close();
                if (out != null) out.close();
                if (socket != null) socket.close();
            } catch (Exception ex) {
                logger.error(ex);
            }
            logger.info("Connection closed with " + this.name);
        }
    }

    private void chat() throws IOException {
        Message msg = null;
        out.writeObject(new ChatMessage(this.name));
        out.flush();

        while (true) {
            try {
                msg = (Message) in.readObject();
            } catch (ClassNotFoundException ex) {
                logger.error(ex);
            }

            if (msg == null || (msg instanceof ExitMessage)) {
                out.writeObject(msg);
                out.flush();
                break;
            }
        }
    }

    public String getName() {
        return name;
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
                "name='" + name + '\'' +
                ", tableId=" + tableId +
                '}';
    }
}