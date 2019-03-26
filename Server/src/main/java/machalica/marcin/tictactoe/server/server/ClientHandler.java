package machalica.marcin.tictactoe.server.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private String name;
    private int tableId = -1;
    private static int count;

    public ClientHandler(Socket socket, BufferedReader in, PrintWriter out)
    {
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
    public void run()
    {
        try {
            chat();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try
            {
                Server.removePlayer(tableId,this);
                if(in != null) in.close();
                if(out != null) out.close();
                if(socket != null) socket.close();
            } catch(Exception ex){
                ex.printStackTrace();
            }
            System.out.println("Connection closed");
        }
    }

    private void chat() throws IOException {
        String msg;
        out.println(this.name + "#SERVER:CLIENTNAME");
        out.flush();

        do {
            msg = in.readLine();
            if(msg == null) break;
            if(msg.equals("")) continue;
            for (ClientHandler player : Server.getPlayers(tableId))
            {
                if(msg.equals("ENDCONNECTION")) player.out.println(this.name + " has disconnected");
                else player.out.println(this.name + ": " + msg);
                player.out.flush();
            }
        } while(!msg.equals("ENDCONNECTION"));
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