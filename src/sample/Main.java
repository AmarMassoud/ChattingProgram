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
    TextField sendMessageField = new TextField();

    TextField nameField = new TextField();
    Button okNameButton = new Button("OK");
    Label nameLabel = new Label("Please enter your name");
    StackPane nameLayout = new StackPane();
    Scene nameScene = new Scene(nameLayout, 700, 700);



    Stage window;
    StackPane mainLayout = new StackPane();
    Button sendButton = new Button("Send");
    Scene mainScene = new Scene(mainLayout, 700, 700);
    BufferedReader in;
    @Override
    public void start(Stage primaryStage) throws Exception {


        ChatClient client = new ChatClient();
        client.startConnection("127.0.0.1", 8089); // 185.218.124.167
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        window = primaryStage;
        in = new BufferedReader(new InputStreamReader(client.getClientSocket().getInputStream()));
        new Thread(() -> chat()).start();
        sendButton.setOnAction(event -> {
            String text = sendMessageField.getText();
            try {
                System.out.println(client.sendMessage(text));
                sendMessageField.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }


        });
        okNameButton.setOnAction(event -> {
            if(!nameField.getText().trim().isEmpty()) {
                try {
                    client.sendMessage("/setname " + nameField.getText());
                    window.setScene(mainScene);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                nameLabel.setText("Invalid name, please do not use an empty name. enter your name.");
            }


        });


        window.setScene(nameScene);
        nameLayout.getChildren().add(nameField);
        nameLayout.getChildren().add(okNameButton);
        okNameButton.setTranslateY(50);
        nameLayout.getChildren().add(nameLabel);
        nameLabel.setTranslateY(-50);

        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setContent(chatText);
        scrollPane.setPannable(true);
        mainLayout.getChildren().add(scrollPane);
        mainLayout.getChildren().add(chatText);
        scrollPane.setMaxHeight(500);
        scrollPane.vvalueProperty().bind(chatText.heightProperty());

        mainLayout.getChildren().add(sendButton);
        sendButton.setTranslateY(300);
        sendButton.setTranslateX(250);
        scrollPane.setMinViewportHeight(50);
        sendMessageField.setTranslateY(300);
        sendMessageField.setTranslateX(-50);
        sendMessageField.setMaxSize(500, 50);
        mainLayout.getChildren().add(sendMessageField);
        mainScene.getStylesheets().add("sample/MainCSS.css");
        nameScene.getStylesheets().add("sample/MainCSS.css");
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
