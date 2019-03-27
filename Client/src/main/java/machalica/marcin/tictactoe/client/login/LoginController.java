package machalica.marcin.tictactoe.client.login;

import javafx.fxml.FXML;
import machalica.marcin.tictactoe.client.client.Client;

public class LoginController {
    private static final Client client = Client.getInstance();

    @FXML
    private void initialize() {
        new Thread(client).start();
    }
}