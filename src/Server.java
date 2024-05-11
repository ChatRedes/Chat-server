import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;


public class Server {
    private ServerSocket serverSocket;

    public Server(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Server started. Listening on port: " + port);
        } catch (Exception e) {
            System.err.println("Error: Failed to start the server on port " + port);
            e.printStackTrace();
        }
    }

    public void start() {
        try {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getChannel());
                Client_Handler client_handler = new Client_Handler(clientSocket);
                Thread threadClientHandler = new Thread(client_handler);
                threadClientHandler.start();
                System.out.println("Client connected: " + clientSocket.getChannel());

            }
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
    }

    public void close() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Server server = new Server(1234);
        server.start();
    }
}