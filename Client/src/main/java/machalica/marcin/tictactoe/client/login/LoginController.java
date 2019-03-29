package machalica.marcin.tictactoe.client.login;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import machalica.marcin.tictactoe.client.client.Client;

public class LoginController {
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button signInButton;

    private static final Client client = Client.getInstance();

    @FXML
    private void initialize() {

        signInButton.setOnAction(e -> {
            if (!client.isAuthenticated()) {
                String login = loginField.getText();
                char[] password = passwordField.getText().toCharArray();
                loginField.setText("");
                passwordField.setText("");

                new Thread(client).start();
                while (!client.isRunning());
                client.authenticate(login, password);
            }
        });
    }
}