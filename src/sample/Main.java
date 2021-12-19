package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleAction;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Main extends Application {
     static javafx.scene.control.Label chatText = new Label("");
    ScrollPane scrollPane = new ScrollPane();
    TextField textField = new TextField();

    Stage window;
    StackPane mainLayout = new StackPane();
    Button sendButton = new Button("Send");
    Scene mainScene = new Scene(mainLayout, 400, 400);
    BufferedReader in;
    @Override
    public void start(Stage primaryStage) throws Exception {
        ChatClient client = new ChatClient();
        client.startConnection("192.168.100.13", 9563);
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        window = primaryStage;
        in = new BufferedReader(new InputStreamReader(client.getClientSocket().getInputStream()));
        new Thread(() -> chat()).start();
        sendButton.setOnAction(event -> {
            String text = textField.getText();
            try {
                System.out.println(client.sendMessage(text));
            } catch (Exception e) {
                e.printStackTrace();
            }


        });

        window.setScene(mainScene);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(chatText);
        scrollPane.setPannable(true);
        mainLayout.getChildren().add(scrollPane);
        mainLayout.getChildren().add(chatText);

        mainLayout.getChildren().add(sendButton);
        sendButton.setTranslateY(50);
        mainLayout.getChildren().add(textField);
        window.show();
    }

    public void chat(){

            try {
                while(true){
                    String input  = in.readLine();


                    Platform.runLater(() -> {
                            chatText.setText(chatText.getText() + "\n" +input );


                    });


                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
