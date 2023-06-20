import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<ClientHandler> clients;
    private int nextClientId;

    public Server() {
        clients = new ArrayList<>();
        nextClientId = 1;
    }

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Chat server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();

                ClientHandler client = new ClientHandler(clientSocket, nextClientId);
                clients.add(client);
                nextClientId++;

                Thread clientThread = new Thread(client);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private int clientId;
        private Socket clientSocket;
        private PrintWriter writer;
        private BufferedReader reader;

        public ClientHandler(Socket clientSocket, int clientId) {
            this.clientId = clientId;
            this.clientSocket = clientSocket;
            try {
                writer = new PrintWriter(clientSocket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    broadcastMessage(message, clientId);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                clients.remove(this);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendMessage(String message) {
            writer.println(message);
            writer.flush();
        }
    }

    private void broadcastMessage(String message, int senderId) {
        clients.stream()
                .filter(client -> client.clientId != senderId)
                .forEach(client -> client.sendMessage(message));
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(12345);
    }
}