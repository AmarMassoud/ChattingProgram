package sample;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;


    public void startConnection(String ip, int port){
        try {
            clientSocket = new Socket(ip, port);
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String sendMessage(String message) throws Exception{
        out.println(message);
        String response = in.readLine();
        return response;


    }
    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public Socket getClientSocket() {
        return clientSocket;
    }
}
