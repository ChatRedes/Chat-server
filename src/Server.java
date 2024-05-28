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
            while (username.equals("")) {
                String clientName = reader.readLine();
                handleRegister(clientName);
            }

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                System.out.println("Chegou aqui");
                handleMessage(clientMessage);
            }
            System.out.println("Client disconnected");

        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        } finally {
            try {
                System.out.println("Closing connection");
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
        System.out.printf("Sending message to %s: %s\n", username, message);
    }

    private void handleRegister(String message) {
        String[] parsedMessage = message.split(" ");
        String result;

        if (parsedMessage.length != 2) {
            sendMessage("ERRO Numero de parametros enviados incorretos");
            return;
        }

        if (parsedMessage[0].equals("REGISTRO")) {
            try
            {
                result = Register_client.insertClient(parsedMessage[1], this.clientSocket);
                sendMessage(result);
                if (result.equals("REGISTRO_OK")) {
                    username = parsedMessage[1];
                }
                return;
            } catch (Exception e)
            {
                sendMessage("ERRO Ocorreu um erro ao registrar o cliente");
            }
        }

        result = "ERRO mensagem não reconhecida ou permissão não concedida";
        sendMessage(result);
    }

    private void handleMessage(String message) {
        System.out.println("Mensagem recebida: " + message);
        String[] parsedMessage = message.split(" ", 2);

        if (parsedMessage[0].equals("LISTAR_SALAS")) {
            try {
                Client_roommanager.Listar_salas();
            } catch (Exception e) {
                sendMessage("ERRO: não listou");
            }
            return;
        }

        if (parsedMessage[0].equals("ENTRAR_SALA")) {
            String[] params = parsedMessage[1].split(" ");
            String roomname = params[0];
            String password = null;
            if (params.length == 2)
            {
                password = params[1];
            }
            Client_roommanager.Entrada_sala(username, roomname, password); // função de entrar sala deve tratar os possiveis erros no corpo dos parametro bem como outros possiveis erros
            sendMessage("ENTRAR_SALA_OK");
            return;
        }

        if (parsedMessage[0].equals("ENVIAR_MENSAGEM")) {
            sendMessage("MENSAGEM " + parsedMessage[1]);
            // função de enviar mensagem deve tratar os possiveis erros no corpo do parametro bem como outros possiveis erros
            return;
        }

        if (parsedMessage[0].equals("CRIAR_SALA")) {
            try {
                String[] paramethers = parsedMessage[1].split(" ", 2);
                String roomName = paramethers[1];
                System.out.println("Criando sala " + roomName);
                String username = this.username;
                String privacidade = paramethers[0];
                String password = null;
                boolean isPrivate = false;

                if (privacidade.equals("PRIVADA"))
                {
                    isPrivate = true;
                }

                if (paramethers.length > 2) {
                    password = paramethers[2];
                }

                Client_roommanager.Criar_sala(username, roomName, password, isPrivate);

                sendMessage("CRIAR_SALA_OK");
            } catch (Exception e) {
                System.err.println(e);
                sendMessage("já é 22:32 e nem sei mais oq eu to fazendo");
            }
            return;
        }

        if (parsedMessage[0].equals("SAIR_SALA")) {
            try {
                String roomName = parsedMessage[1];
                String username = this.username;
                Client_roommanager.Saida_sala(username, roomName);
                sendMessage("SAIR_SALA_OK");
            } catch (Exception e) {
                sendMessage("deu ruim");
            }
            return;
        }

        if (parsedMessage[0].equals("BANIR_USUARIO")) {
            try {
                String roomName = parsedMessage[1];
                String usernameToBan = parsedMessage[2];
                String usernameAdmin = this.username;
                Client_roommanager.bane_user(usernameAdmin, roomName, usernameToBan);
                sendMessage("BANIR_USUARIO_OK");
            } catch (Exception e) {
                sendMessage("deu ruim");
            }
            return;
        }

        String result = "ERRO mensagem não reconhecida";
        sendMessage(result);
    }
}
