import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Client_Handler implements Runnable {

    private final Socket clientSocket;

    public Client_Handler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);

            writer.println("Welcome to the server!");

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                System.out.println("Message from client: " + clientMessage);
                writer.println("Server received: " + clientMessage);
                System.out.println("Client connected: " + clientSocket.getChannel());

            }

            reader.close();
            writer.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        }
    }
}