package Services;

import Util.DatabaseConfig;

import Model.Client;

import java.net.Socket;
import java.sql.*;

import Util.*;

public class Register_client {

    private static String insert_query(String username, String socket) throws Exception {
        Connection conn = DatabaseConfig.getConnection();
        String CreateQueryClient = "INSERT INTO CLIENT (username, socket) VALUES ('" + username + "', '" + socket + "');";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(CreateQueryClient);
        return "REGISTRO_OK";
    }

    private static Client select_query(String username) {
        try {
            Connection conn = DatabaseConfig.getConnection();
            String SelectQueryClient = "SELECT * FROM client WHERE username = '" + username + "';";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SelectQueryClient);
            if (rs.next()) {
                int id = rs.getInt("id_client");
                String user = rs.getString("username");
                String socket = rs.getString("socket");

                Client client = new Client(id, user, socket);
                return client;
            }

            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String insertClient(String username, Socket socket) throws Exception {
        Client client = select_query(username);
        String result;
        System.out.println("client: " + client);
        if (client == null) {
            String serializedSocket = SocketSerialize.serialize(socket);
            result = insert_query(username, serializedSocket);
            return result;
        }
        result = "ERRO usuario com este nome já regitrado";
        return result;
    }
}