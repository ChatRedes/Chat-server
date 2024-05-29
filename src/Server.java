import Util.Authenticator;
import Util.DatabaseConfig;
import Util.ServerCripto;

import Services.*;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import io.github.cdimascio.dotenv.Dotenv;

public class Server {
    private static final int PORT = 8080;
    static List<ClientHandler> clients = new ArrayList<>();
    private Dotenv envVariables;
    private ServerCripto keyGenerator;

    public static void main(String[] args) {
        Server server = new Server();
        DatabaseConfig.StartDatabase();
        server.keyGenerator = new ServerCripto();

        server.start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                Authenticator auth = new Authenticator(keyGenerator.privateKey, keyGenerator.publicKey);
                ClientHandler clientHandler = new ClientHandler(clientSocket, auth);
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
    private boolean authenticated = false;
    private Authenticator authenticator;


    public ClientHandler(Socket clientSocket, Authenticator authenticator) throws IOException {
        this.clientSocket = clientSocket;
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        this.authenticator = authenticator;
    }

    @Override
    public void run() {
        try {
            while (username.equals("")) {
                String clientName = reader.readLine();
                handleRegister(clientName);
            }

            authenticateClient();

            String clientMessage;
            while ((clientMessage = reader.readLine()) != null) {
                handleMessage(clientMessage);
            }
            System.out.println("Client disconnected");

        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        } finally {
            closeClient();
        }
    }

    public void closeClient() {
        try {
            System.out.println("Closing connection");
            reader.close();
            writer.close();
            clientSocket.close();
            Client_roommanager.remove_user_database(username);
        } catch (IOException e) {
            System.err.println("Error closing client connection: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        String encryptedMessage = authenticator.EncryptMessage(message);
        writer.println(encryptedMessage);
        System.out.printf("Sending message to %s: %s\n", username, message);
    }

    public void unsafeMessage(String message) {
        writer.println(message);
        System.out.printf("Sending message to %s: %s\n", username, message);
    }

    private void handleRegister(String message) {
        String[] parsedMessage = message.split(" ");
        String result;

        if (parsedMessage.length != 2) {
            unsafeMessage("ERRO Numero de parametros enviados incorretos");
            return;
        }

        if (parsedMessage[0].equals("REGISTRO")) {
            try
            {
                result = Register_client.insertClient(parsedMessage[1], this.clientSocket);
                unsafeMessage(result);
                if (result.equals("REGISTRO_OK")) {
                    username = parsedMessage[1];
                }
                return;
            } catch (Exception e)
            {
                unsafeMessage("ERRO Ocorreu um erro ao registrar o cliente");
            }
        }

        result = "ERRO mensagem não reconhecida ou permissão não concedida";
        unsafeMessage(result);
    }

    private void authenticateClient() {
        try {
            String request = reader.readLine();
            String[] parsedRequest = request.split(" ");

            if (parsedRequest.length != 2) {
                System.err.println("Invalid request size: " + request);
                closeClient();
                return;
            }

            if (!parsedRequest[0].equals("AUTENTICACAO")) {
                System.err.println("Invalid request: " + request);
                closeClient();
                return;
            }

            if (!parsedRequest[1].equals(username)) {
                System.err.println("Invalid username: " + request);
                closeClient();
                return;
            }

            unsafeMessage(authenticator.SendPublicKey());

            String encryptedSKey = reader.readLine();
            if (!authenticator.DecryptSimetricKey(encryptedSKey)) {
                closeClient();
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    private void handleMessage(String encryptedMessage) {
        String message = authenticator.DecryptMessage(encryptedMessage);
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
            try {
                String roomName = parsedMessage[1];
                String messageContent = parsedMessage[2];
                List<String> participants = Messages_handler.pegar_os_participantes(roomName);
                for (ClientHandler client : Server.clients) {
                    if (participants.contains(username)) {
                        client.sendMessage(messageContent);
                    }
                }
            } catch (Exception e) {
                sendMessage("erro");
            }
            return;
        }

        if (parsedMessage[0].equals("CRIAR_SALA")) {
            try {
                String[] paramethers = parsedMessage[1].split(" ");
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
                sendMessage("ERRO Problema ao criar sala");
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
