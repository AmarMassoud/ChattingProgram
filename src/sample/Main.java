package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.datatransfer.Clipboard;
import java.io.IOException;

public class Main extends Application {
    TextField textField = new TextField();
    Stage window;
    StackPane mainLayout = new StackPane();
    Button sendButton = new Button("Send");
    Scene mainScene = new Scene(mainLayout, 400, 400);


    @Override
    public void start(Stage primaryStage) throws Exception{
        ChatClient client = new ChatClient();
        client.startConnection("127.0.0.1", 9563);
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        window = primaryStage;

        sendButton.setOnAction(event -> {
            String text = textField.getText();
            try {
                client.sendMessage(text);
            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        window.setScene(mainScene);
        mainLayout.getChildren().add(sendButton);
        sendButton.setTranslateY(50);
        mainLayout.getChildren().add(textField);
        window.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
