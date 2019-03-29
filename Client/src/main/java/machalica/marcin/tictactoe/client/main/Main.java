package machalica.marcin.tictactoe.client.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import machalica.marcin.tictactoe.client.client.Client;
import org.apache.log4j.Logger;

public class Main extends Application {
    private static final Logger logger = Logger.getLogger(Main.class);
    private static final Client client = Client.getInstance();
    private static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
        primaryStage.setTitle("Tic Tac Toe - client");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setOnCloseRequest(e -> client.requestClose());
        primaryStage.show();
    }

    public static void setLobbyScene() {
        if (client.isAuthenticated()) {
            try {
                Parent root = FXMLLoader.load(Main.class.getResource("/Lobby.fxml"));
                mainStage.getScene().setRoot(root);
            } catch (Exception ex) {
                logger.error(ex);
                client.requestClose();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}