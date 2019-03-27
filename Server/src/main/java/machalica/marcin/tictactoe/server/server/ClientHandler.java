package machalica.marcin.tictactoe.server.server;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ClientHandler.class);
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private static final Server server = Server.getInstance();
    private String name;
    private int tableId = -1;
    private static int count;

    public ClientHandler(Socket socket, BufferedReader in, PrintWriter out) {
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
            logger.info("Connection closed");
        }
    }

    private void chat() throws IOException {
        String msg;
        out.println("SERVER:CLIENTNAME#" + this.name);
        out.flush();

        do {
            msg = in.readLine();
            if (msg != null && msg.trim().equals("")) continue;
            for (ClientHandler player : server.getPlayers(tableId)) {
                if (msg == null || msg.equals("ENDCONNECTION")) {
                    if (player != this) {
                        player.out.println("Opponent has disconnected");
                    } else {
                        player.out.println("ENDCONNECTION");
                    }
                } else if (this.name != null) {
                    player.out.println(this.name + ": " + msg.trim());
                }
                player.out.flush();
            }
        } while (msg != null || !msg.equals("ENDCONNECTION"));
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