package Services;
import Util.DatabaseConfig;
import Util.SocketSerialize;

import java.io.IOException;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Messages_handler {

//    public static void pegar_os_sockets() //ver se vai precisar
//    {
//        try {
//            Connection adminConn = DatabaseConfig.getConnection();
//            String query = "SELECT socket FROM CHAT WHERE username = '" + username + "';";
//            Statement stmt = adminConn.createStatement();
//            ResultSet rs = stmt.executeQuery(query);
//
//            while (rs.next()) {
//                Socket socket = SocketSerialize.deserializeSocket(rs.getString("socket"));
//            }
//        } catch (SQLException | IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static List<String> pegar_os_participantes(String roomName) {
//        List<String> participants = new ArrayList<>();
//        try {
//            Connection adminConn = DatabaseConfig.getConnection();
//            String usernamesQuery = "SELECT username FROM CHAT WHERE ROOM_NAME = '" + roomName + "';";
//            PreparedStatement stmt = adminConn.prepareStatement(usernamesQuery);
//            stmt.setString(1, roomName);
//            ResultSet rs = stmt.executeQuery();
//
//            while (rs.next()) {
//                participants.add(rs.getString("username"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return participants;
//    }
}