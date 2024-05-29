package Services;

import Util.DatabaseConfig;

import java.security.MessageDigest;
import java.sql.*;
import Util.*;

public class Client_roommanager {

    public static String gerarHash(String senha) throws Exception {
        MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
        byte hash[] = algorithm.digest(senha.getBytes("UTF-8"));

        StringBuilder texto = new StringBuilder();
        for (byte b : hash) {
            texto.append(String.format("%02X", 0xFF & b));
        }
        return texto.toString();
    }

    public static void Listar_salas() {
        try {
            Connection adminConn = DatabaseConfig.getConnection();
            String salas = "SELECT room_name FROM chat;";
            Statement stmt = adminConn.createStatement();
            ResultSet rs = stmt.executeQuery(salas);

            while (rs.next()) {
                String roomName = rs.getString("room_name");
                System.out.println("Room Name: " + roomName);
            }
        } catch(SQLException e){
                e.printStackTrace();
            }
    }

    public static void Criar_sala(String username, String room_name, String senha, boolean isPrivate) {
        try {
            Connection adminConn = DatabaseConfig.getConnection();
            Statement stmt = adminConn.createStatement();
            String CreateQueryChat = "INSERT INTO chat (room_name, administrador, senha, isPrivate) VALUES ('" + room_name + "', '" + username + "' ,'"+ senha + "', " + isPrivate + " );";
            stmt.executeUpdate(CreateQueryChat);
            String CreateQueryIntermed = "INSERT INTO client_room (username, room_name) VALUES ('" + username + "', '" + room_name + "');";
            stmt.executeUpdate(CreateQueryIntermed);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String Entrada_sala(String username, String room_name, String senha) {
        String insertQuery = "INSERT INTO CLIENT_ROOM (username, room_name) VALUES ('"+ username + "', '" + room_name + "');";
        try {
            Connection adminConn = DatabaseConfig.getConnection();
            String SenhaEqual = "SELECT senha FROM CHAT WHERE ROOM_NAME = '" + room_name + "';";
            try (Statement stmt = adminConn.createStatement();
                 ResultSet rs = stmt.executeQuery(SenhaEqual)) {

                if (rs.next()) {
                    boolean isPrivate = rs.getBoolean("isPrivate");

                    if (!isPrivate) {
                        stmt.executeUpdate(insertQuery);
                        return "ENTRAR_SALA_OK";
                    }

                    String equal = rs.getString("senha");
                    if (senha == null) {
                        return "ERRO Senha para esta sala é obrigatoria";
                    }

                    if (!equal.equals(senha)) {
                        return"ERRO Senha incorreta";
                    }

                    stmt.executeUpdate(insertQuery);
                    return "ENTRAR_SALA_OK";
                }

                return "ERRO Sala não existe";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERRO Ocorreu um erro na entrada de sala";
        } catch (Exception e) {
            return "ERRO Ocorreu um erro na entrada de sala";
        }
    }

    public static void Saida_sala (String username, String room_name){
        String deletQuery;
        try {
            Connection adminConn = DatabaseConfig.getConnection();
            String isadmin = "SELECT administrador FROM CHAT WHERE USERNAME = '" + username + "' AND ROOM_NAME = '" + room_name + "';";
            try (Statement stmt = adminConn.createStatement();
                 ResultSet rs = stmt.executeQuery(isadmin)) {

                if (rs.next()) {
                    String admin = rs.getString("administrador");
                    if (!admin.equals(username)) {
                        deletQuery = "DELETE FROM CLIENT_ROOM WHERE USERNAME = '" + username + "' AND ROOM_NAME = " + room_name + "';";

                    } else {
                        deletQuery = "DELETE CASCADE FROM CHAT WHERE ROOM_NAME = '" + room_name + "';";
                    }
                    stmt.executeUpdate(deletQuery);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void bane_user(String username_admin, String room_name, String username){
        String deletQuery;
        try {
            Connection adminConn = DatabaseConfig.getConnection();
            String isadmin = "SELECT administrador FROM CHAT WHERE USERNAME = '" + username_admin + "' AND ROOM_NAME = '" + room_name + "';";
            try (Statement stmt = adminConn.createStatement();
                 ResultSet rs = stmt.executeQuery(isadmin)) {

                if (rs.next()) {
                    String admin = rs.getString("administrador");
                    if (!admin.equals(username_admin)) {
                        System.out.println("Usuário não tem permissão para realizar essa ação");
                    } else {
                        deletQuery = "DELETE FROM CLIENT_ROOM WHERE USERNAME = '" + username + "' AND ROOM_NAME = " + room_name + "';";
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    }