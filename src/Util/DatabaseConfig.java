package Util;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.*;

public class DatabaseConfig {
    private static String url;
    private static String database;
    private static String user;
    private static String password;

    static {
        Dotenv dotenv = Dotenv.load();

        String host = dotenv.get("HOST");
        String port = dotenv.get("PORT");

        url = "jdbc:postgresql://" + host + ":" + port + "/";
        database = dotenv.get("DATABASE").toLowerCase();
        user = dotenv.get("USER");
        password = dotenv.get("PASSWORD");
    }

    public DatabaseConfig () {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url+database, user, password);
    }

    private static Connection getAdminConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public static void StartDatabase() {
        CreateDatabase();
        CreateTables();
    }

    private static void CreateDatabase() {
        try (Connection adminConn = getAdminConnection()) {
            createDatabaseIfNotExists();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void CreateTables() {
        try {
            createClient();
            createRoom();
            createClient_Room();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createDatabaseIfNotExists() throws SQLException {
        Connection adminConn = getAdminConnection();

        String checkDbExistsQuery = "SELECT 1 FROM pg_database WHERE datname = '" + database + "'" ;
        try (Statement stmt = adminConn.createStatement();
             var rs = stmt.executeQuery(checkDbExistsQuery)) {
            if (!rs.next()) {
                String createDbQuery = "CREATE DATABASE " + database;
                stmt.executeUpdate(createDbQuery);
                System.out.println("Database created successfully.");
            }
        }
    }

    private static void createClient() throws SQLException {
        Connection conn = getConnection();
        String query = "CREATE TABLE IF NOT EXISTS CLIENT (id_client SERIAL PRIMARY KEY, username VARCHAR(255) UNIQUE, socket VARCHAR(255) UNIQUE);";
        PreparedStatement st = conn.prepareStatement(query);
        st.executeUpdate();
    }

    private static void createRoom() throws SQLException {
        Connection conn = getConnection();
        String query = "CREATE TABLE IF NOT EXISTS CHAT (id_chat SERIAL PRIMARY KEY, room_name VARCHAR(255) UNIQUE, administrador VARCHAR(255) REFERENCES CLIENT(username), isPrivate BOOLEAN UNIQUE, senha TEXT);";
        PreparedStatement st = conn.prepareStatement(query);
        st.executeUpdate();
    }

    private static void createClient_Room() throws SQLException {
        Connection conn = getConnection();
        String query = "CREATE TABLE IF NOT EXISTS CLIENT_CHAT (id_cliente_room SERIAL PRIMARY KEY, username VARCHAR(255) REFERENCES CLIENT(username), room_name TEXT REFERENCES CHAT(room_name));";
        PreparedStatement st = conn.prepareStatement(query);
        st.executeUpdate();
    }
}
