package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.AccessibleAction;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Main extends Application {
    Label chatText = new Label("");
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


    StackPane connectionErrorLayout = new StackPane();
    Label connectionErrorLabel = new Label("There was an error connecting to the server. Please check your internet connection or try again later.");
    Scene connectionErrorScene = new Scene(connectionErrorLayout, 900, 100);

    @Override
    public void start(Stage primaryStage) throws Exception {

        window = primaryStage;
        ChatClient client = new ChatClient();
        try {
            client.startConnection("185.218.124.167", 8089); // 185.218.124.167
            in = new BufferedReader(new InputStreamReader(client.getClientSocket().getInputStream()));
            new Thread(() -> chat()).start();
            window.setScene(nameScene);
        } catch (SocketException | NullPointerException ignored) {
            window.setScene(connectionErrorScene);
        }
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));


        sendButton.setOnAction(event -> {
            if (!sendMessageField.getText().trim().isEmpty()) {
                String text = sendMessageField.getText();
                try {
                    client.sendMessage(text);
                    sendMessageField.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        });
        okNameButton.setOnAction(event -> {
            if (!nameField.getText().trim().isEmpty()) {
                try {
                    client.sendMessage("/setname " + nameField.getText());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                nameLabel.setText("Invalid name, please do not use an empty name. enter your name.");
            }
        });

        sendMessageField.setOnKeyPressed(event -> {
                    if (event.getCode().equals(KeyCode.ENTER)) {
                        if (!sendMessageField.getText().trim().isEmpty()) {
                            String text = sendMessageField.getText();
                            try {
                                client.sendMessage(text);
                                sendMessageField.clear();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }});
        nameField.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)) {
                if (!nameField.getText().trim().isEmpty()) {
                    try {
                        client.sendMessage("/setname " + nameField.getText());
                        nameField.clear();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    nameLabel.setText("Invalid name, please do not use an empty name. enter your name.");
                }
            }


        });

        connectionErrorLayout.getChildren().add(connectionErrorLabel);
        connectionErrorScene.getStylesheets().add("sample/MainCSS.css");
        connectionErrorLabel.setId("errorLabel");




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

        chatText.setMaxWidth(700);
        chatText.setWrapText(true);


        window.show();


    }

    public void chat() {

        try {
            while (true) {
                String input = in.readLine();
                System.out.println(input);

                Platform.runLater(() -> {
                    if (input.contains("name is wrong")) {
                        nameLabel.setText("The name has already been used. Please use another name.");
                    } else {
                        window.setScene(mainScene);
                        sendMessageField.requestFocus();
                    }
                    chatText.setText(chatText.getText() + "\n" + input);


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
