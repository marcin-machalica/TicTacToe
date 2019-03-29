package machalica.marcin.tictactoe.client.game;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import machalica.marcin.tictactoe.client.client.Client;

public class LobbyController {
    @FXML
    private Label playerNameLabel;
    private static final String playerNameLabelTemplate = "Player: %s";
    @FXML
    private Label gameTableLabel;
    private static final String gameTableLabelTemplate = "Game Table: %d";

    private static final Client client = Client.getInstance();

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            playerNameLabel.setText(String.format(playerNameLabelTemplate, client.getName()));
            gameTableLabel.setText(String.format(gameTableLabelTemplate, client.getGameTableId()));
        });
    }
}