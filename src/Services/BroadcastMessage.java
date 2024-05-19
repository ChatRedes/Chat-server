package Services;

import Util.DatabaseConfig;
import Util.SocketSerialize;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.net.Socket;


public class BroadcastMessage {
    private DataSource ds = DatabaseConfig.getDataSource();

    public void BroadcastMessage(String chat, String user, String message) {
        String query = "SELECT * FROM chat_client WHERE chat = ?";
        try (Connection connection = ds.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, chat);
            ResultSet rs = statement.executeQuery();
            Socket socket;
            while (rs.next()) {
                socket = SocketSerialize.deserializeSocket(rs.getString("Socket"));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);

                String request = "MENSAGEM " + chat + " " + user + " " + message + "\n";

                writer.println(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
