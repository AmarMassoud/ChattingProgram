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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class Main extends Application {
    TextField textField = new TextField();
    Stage window;
    StackPane mainLayout = new StackPane();
    Button sendButton = new Button("Send");
    Scene mainScene = new Scene(mainLayout, 400, 400);
    BufferedReader in;
    @Override
    public void start(Stage primaryStage) throws Exception{
        ChatClient client = new ChatClient();
        client.startConnection("127.0.0.1", 9563);
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        window = primaryStage;
        in = new BufferedReader(new InputStreamReader(client.getClientSocket().getInputStream()));
        chat();
        sendButton.setOnAction(event -> {
            String text = textField.getText();
            try {
                System.out.println(client.sendMessage(text));
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

    public void chat(){
        setTimeout(() -> {
            try {
                while(in.ready()){
                    System.out.println(in.readLine());
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            chat();
        }, 500);
    }
    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
