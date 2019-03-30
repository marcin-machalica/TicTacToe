package machalica.marcin.tictactoe.client.game;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import machalica.marcin.tictactoe.client.client.Client;

public class GameController {
    @FXML
    private Label playerNameLabel;
    private final String playerNameLabelTemplate = "Player: %s";
    @FXML
    private Label playerScoreLabel;
    private final String playerScoreLabelTemplate = "Score: %d";
    @FXML
    private Label playerSymbolLabel;
    @FXML
    private Label opponentNameLabel;
    private final String opponentNameLabelTemplate = "Opponent: %s";
    @FXML
    private Label opponentScoreLabel;
    private final String opponentScoreLabelTemplate = "Score: %s";
    @FXML
    private Label opponentSymbolLabel;

    private static final Client client = Client.getInstance();

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            playerSymbolLabel.setTextFill(Game.getPlayerColor());
            opponentSymbolLabel.setTextFill(Game.getOpponentColor());

            playerNameLabel.setText(String.format(playerNameLabelTemplate, client.getGame().getPlayerName()));
            playerScoreLabel.setText(String.format(playerScoreLabelTemplate, client.getGame().getPlayerScore()));
            playerSymbolLabel.setText(Character.toString(client.getGame().getPlayerSymbol()));

            opponentNameLabel.setText(String.format(opponentNameLabelTemplate, client.getGame().getOpponentName()));
            opponentScoreLabel.setText(String.format(opponentScoreLabelTemplate, client.getGame().getOpponentScore()));
            opponentSymbolLabel.setText(Character.toString(client.getGame().getOpponentSymbol()));
        });
    }
}
