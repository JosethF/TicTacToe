package com.example.tresenraya;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkService {

    private String serverIP;
    private int serverPort;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public NetworkService(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void connect() throws IOException {
        clientSocket = new Socket(serverIP, serverPort);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMove(int position) throws IOException {
        out.println(position);
        return in.readLine();
    }

    public String getServerMove() throws IOException {
        return in.readLine();
    }

    public void closeConnection() throws IOException {
        clientSocket.close();
    }
}

