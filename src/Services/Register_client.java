package Services;

import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import Server.java;

public class Register_client {
    Server server;

    private static void insert_query(String username, Socket socket) {
        String insertQuery = "INSERT INTO CLIENT (username, socket) VALUES (?, ?)";
        try {
            Connection conn = Server.conectWithDatabase();
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, socket.toString()); // Aqui estou assumindo que o método toString() retorna o valor desejado
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace(); // ou trate o erro de acordo com sua lógica de aplicação
        }
    }

}
