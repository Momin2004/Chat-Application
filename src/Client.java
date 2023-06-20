import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;

    public Client(String host, int port) {
        try {
            socket = new Socket(host, port);
            System.out.println("Connected to chat server");

            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            Thread serverThread = new Thread(new ServerHandler());
            serverThread.start();

            Thread clientThread = new Thread(new ClientHandler());
            clientThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerHandler implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = reader.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ClientHandler implements Runnable {
        @Override
        public void run() {
            Scanner scanner = new Scanner(System.in);
            try {
                while (true) {
                    String message = scanner.nextLine();
                    writer.println(message);
                }
            } finally {
                scanner.close();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 12345);
    }
}