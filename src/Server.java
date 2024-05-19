import Util.DatabaseConfig;

import Services.*;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import io.github.cdimascio.dotenv.Dotenv;

public class Server {
    private static final int PORT = 8080;
    private static List<ClientHandler> clients = new ArrayList<>();
    private Dotenv envVariables;

    public static void main(String[] args) {
        Server server = new Server();
        DatabaseConfig.StartDatabase();

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
    private String username = "";

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            while (username == "") {
                String clientName = reader.readLine();
                handleRegister(clientName);
            }

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                System.out.println(clientMessage);

//                Server.broadcastMessage(clientMessage); // Broadcast message to all clients
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

    private void handleRegister(String message) {
        String[] parsedMessage = message.split(" ", 2);
        String result = "REGISTRO_OK";

        if (parsedMessage[0].equals("REGISTRO")) {
//            if (Register_client.registerClient(parsedMessage[1], clientSocket)) { // registerClient booleano para retornar o sucesso ou falha
//                sendMessage(result);
//                username = parsedMessage[1]; // salva o nome do usuario no handler caso consiga salvar o nome do usuario
//            }
            sendMessage(result);
            return;
        }

        result = "ERRO mensagem não reconhecida ou permissão não concedida";
        sendMessage(result);
    }

    private void handleMessage(String message) {
        String[] parsedMessage = message.split(" ", 2);

        if (parsedMessage[0].equals("LISTAR_SALAS")) {
            // verificar se veio sem nenhum parametro adicional na mensagem
        }

        if (parsedMessage[0].equals("ENTRAR_SALA")) {
            System.out.println();
//            Client_roommanager.Entrar_sala(parsedMessage[1]); // função de entrar sala deve tratar os possiveis erros no corpo dos parametro bem como outros possiveis erros
            return;
        }

        if (parsedMessage[0].equals("ENVIAR_MENSAGEM")) {
            // função de enviar mensagem deve tratar os possiveis erros no corpo do parametro bem como outros possiveis erros
        }

        if (parsedMessage[0].equals("CRIAR_SALA")) {
            // função de criar salas deve tratar os possiveis erros no corpo do parametro bem como outros possiveis erros
        }

        if (parsedMessage[0].equals("SAIR_SALA")) {
            // função de sair da sala deve tratar os deve tratar os possiveis erros no corpo do parametro bem como outros possiveis erros
        }

        if (parsedMessage[0].equals("BANIR_USUARIO")) {
            // função de banir usuario deve tratar os deve tratar os possiveis erros no corpo do parametro bem como outros possiveis erros
        }

        String result = "ERRO mensagem não reconhecida";
        sendMessage(result);
    }
}
