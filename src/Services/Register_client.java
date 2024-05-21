package Services;

import Util.DatabaseConfig;

import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import Util.*;


public class Register_client {

    private static void insert_query(String username, Socket socket) {
        try {
            Connection adminConn = DatabaseConfig.getConnection();
            String CreateQueryClient = "INSERT INTO CLIENT (username, socket) VALUES ('" + username + "', '" + socket + "');";
            Statement stmt = adminConn.createStatement();
            stmt.executeQuery(CreateQueryClient);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
