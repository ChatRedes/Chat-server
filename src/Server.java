
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import io.github.cdimascio.dotenv.Dotenv;

public class Server {
    private static final int PORT = 8080;
    private static List<ClientHandler> clients = new ArrayList<>();
    private Connection conexao;
    private Dotenv envVariables;

    public static void main(String[] args) {
        Server server = new Server();
        server.getDotEnv();
        server.conexao = server.conectWithDatabase();
        if (server.conexao == null){
            return;
        }
        server.start();
    }

    private void getDotEnv() {
        Dotenv dotenv = Dotenv.load();
        String env = dotenv.get("testing");
        System.out.println(env);

        envVariables = dotenv;
    }

    private Connection conectWithDatabase() {
        try {
            String host = envVariables.get("HOST");
            String port = envVariables.get("PORT");
            String database = envVariables.get("DATABASE");
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            System.out.println(url);

            Properties props = new Properties();
            props.setProperty("user", envVariables.get("USER"));
            props.setProperty("password", envVariables.get("PASSWORD"));
            Connection conn = DriverManager.getConnection(url, props);

            return conn;
        } catch (SQLException e) {
            // Erro caso haja problemas para se conectar ao banco de dados
            e.printStackTrace();
        }
        return null;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    public static void broadcastMessage(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final BufferedReader reader;
    private final PrintWriter writer;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            writer.println("Welcome to the server!");

            String clientName = reader.readLine();
            System.out.println("Client name: " + clientName);

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                System.out.println(clientMessage);
                Server.broadcastMessage(clientMessage); // Broadcast message to all clients
            }

        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        } finally {
            try {
                reader.close();
                writer.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client connection: " + e.getMessage());
            }
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}
