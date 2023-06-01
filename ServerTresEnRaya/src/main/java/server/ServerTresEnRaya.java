package server;

import java.net.*;
import java.io.*;

public class ServerTresEnRaya {
    private int port;

    public ServerTresEnRaya(int port) {
        this.port = port;
    }
    public void startServer() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started. Waiting for a client ...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("Client accepted.");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                TresEnRaya game = new TresEnRaya();

                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    try {

                        int move = Integer.parseInt(inputLine);
                        String response = game.makeMove(move);
                        out.println(response);

                        // Ahora la jugada del servidor después de la jugada del cliente
                        if (!game.isGameOver()) {
                            String serverResponse = game.serverMove();
                            out.println(serverResponse);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input: " + inputLine);
                        out.println("Invalid input");
                    }
                }
                System.out.println("Client disconnected.");
            }
        } catch (IOException e) {
            System.out.println("Error starting the server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 9999; // Debes escoger el puerto que más te convenga
        ServerTresEnRaya server = new ServerTresEnRaya(port);

        try {
            server.startServer();
        } catch (IOException e) {
            System.out.println("Error starting the server: " + e.getMessage());
        }
    }
}

