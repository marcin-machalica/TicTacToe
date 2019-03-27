package machalica.marcin.tictactoe.server.main;

import machalica.marcin.tictactoe.server.server.Server;

public class Main {
    public static void main(String[] args) {
        Server server = Server.getInstance();
        server.runServer();
    }
}