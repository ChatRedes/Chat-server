package Services;
import Util.DatabaseConfig;
import Util.SocketSerialize;

import java.io.IOException;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Messages_handler {

        public static List<String> pegar_os_participantes(String roomName) {
            List<String> participants = new ArrayList<>();
            try {
                Connection adminConn = DatabaseConfig.getConnection();
                String usernamesQuery = "SELECT username FROM CLIENT_ROOM WHERE ROOM_NAME = ?";
                PreparedStatement stmt = adminConn.prepareStatement(usernamesQuery);
                stmt.setString(1, roomName);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    participants.add(rs.getString("username"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return participants;
        }

    public static String pegar_banidos(String username) {
        String participante = "";
        try {
            Connection adminConn = DatabaseConfig.getConnection();
            String usernamesQuery = "SELECT username FROM CLIENT WHERE username = ?";
            PreparedStatement stmt = adminConn.prepareStatement(usernamesQuery);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                participante=(rs.getString("username"));
            }
        } catch (SQLException e) {
            participante = "";
            e.printStackTrace();
        }
        return participante;
    }
}