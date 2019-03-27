package machalica.marcin.tictactoe.client.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import machalica.marcin.tictactoe.client.client.Client;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
        primaryStage.setTitle("Tic Tac Toe - client");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setOnCloseRequest(e -> Client.getInstance().sendMessage("ENDCONNECTION"));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}