package Services;

import Util.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class Client_roommanager {

    public static String Listar_salas() {
        StringBuilder salasString = new StringBuilder("SALAS");
        try {
            Connection adminConn = DatabaseConfig.getConnection();
            String salas = "SELECT room_name FROM CHAT;";
            Statement stmt = adminConn.createStatement();
            ResultSet rs = stmt.executeQuery(salas);

            while (rs.next()) {
                String roomName = rs.getString("room_name");
                salasString.append(" ").append(roomName);
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return salasString.toString();
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
            String SenhaEqual = "SELECT senha, isprivate FROM CHAT WHERE ROOM_NAME = '" + room_name + "';";
            try (Statement stmt = adminConn.createStatement();
                 ResultSet rs = stmt.executeQuery(SenhaEqual)) {

                if (rs.next()) {
                    boolean isPrivate = rs.getBoolean("isPrivate");
                    List<String> Listusuarios = new ArrayList<>();
                    if (!isPrivate) {
                        stmt.executeUpdate(insertQuery);
                        String usuarios = "SELECT username FROM CLIENT_ROOM WHERE ROOM_NAME = '" + room_name + "';";

                        try (Statement stmtt = adminConn.createStatement();
                             ResultSet rss = stmtt.executeQuery(usuarios)) {
                            String user = rss.getString("room_name");
                            Listusuarios.add(user);
                            return "ENTRAR_SALA_OK" + Listusuarios;
                        }catch (SQLException e) {
                            e.printStackTrace();
                            return "ERRO Ocorreu um erro na entrada de sala";
                        }
                    }

                    String equal = rs.getString("senha");
                    if (senha == null) {
                        return "ERRO Senha para esta sala é obrigatoria";
                    }

                    if (!equal.equals(senha)) {
                        return"ERRO Senha incorreta";
                    }

                    stmt.executeUpdate(insertQuery);
                    String usuarios = "SELECT username FROM CLIENT_ROOM WHERE ROOM_NAME = '" + room_name + "';";
                    Listusuarios = new ArrayList<>();
                    try (Statement stmtt = adminConn.createStatement();
                         ResultSet rss = stmtt.executeQuery(usuarios)) {
                        String user = rss.getString("room_name");
                        Listusuarios.add(user);
                        return "ENTRAR_SALA_OK" + Listusuarios;
                    }
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

    public static String Saida_sala (String username, String room_name){
        try {
            Connection adminConn = DatabaseConfig.getConnection();
            String salaexist = "SELECT room_name FROM CHAT WHERE ROOM_NAME = '" + room_name + "';";
            String isadmin = "SELECT administrador FROM CHAT WHERE USERNAME = '" + username + "' AND ROOM_NAME = '" + room_name + "';";
            try (Statement stmt = adminConn.createStatement();
                 ResultSet rs = stmt.executeQuery(salaexist)) {

                if (rs.next()) {
                    try (Statement stmtt= adminConn.createStatement();
                         ResultSet rss = stmtt.executeQuery(isadmin)){
                        if (rss.next()) {
                            String admin = rs.getString("administrador");
                            if (!admin.equals(username)) {
                                String deletQuery = "DELETE FROM CLIENT_ROOM WHERE USERNAME = '" + username + "' AND ROOM_NAME = " + room_name + "';";
                                stmt.executeUpdate(deletQuery);
                                return "SAIR_SALA_OK";
                            } else {
                                String deletQuery = "DELETE CASCADE FROM CHAT WHERE ROOM_NAME = '" + room_name + "';";
                                stmt.executeUpdate(deletQuery);
                                return "FECHAR_SALA_OK";
                            }
                        }
                    }
                }
            } catch (SQLException e) {
                return "ERRO Sala não existe";
            }
        } catch (SQLException e) {
            return "ERRO Conexão não estabelicida";
        }
        return "ERRO";
    }

    public static String bane_user(String username_admin, String room_name, String username){
        String deletQuery;
        try {
            Connection adminConn = DatabaseConfig.getConnection();
            String isIn = "SELECT username FROM CLIENT_ROOM WHERE USERNAME = '" + username_admin + "' AND ROOM_NAME = '" + room_name + "';";
            try (Statement stmt = adminConn.createStatement();
                 ResultSet rs = stmt.executeQuery(isIn)) {

                if(rs.next()){

                    String isadmin = "SELECT administrador FROM CHAT WHERE USERNAME = '" + username_admin + "' AND ROOM_NAME = '" + room_name + "';";


                    try (Statement stmtt = adminConn.createStatement();
                         ResultSet rss = stmtt.executeQuery(isadmin)) {

                        if (rss.next()) {
                            String admin = rss.getString("administrador");
                            if (!admin.equals(username_admin)) {
                                return "ERRO Usuário não tem permissão para realizar essa ação";
                            } else {
                                deletQuery = "DELETE FROM CLIENT_ROOM WHERE USERNAME = '" + username + "' AND ROOM_NAME = " + room_name + "';";
                                return "BANIMENTO_OK " + username;
                            }
                        }
                    }
                } else{
                    return "ERRO USUARIO NÃO ESTÁ NA SALA";
                }}}catch (SQLException e) {
            e.printStackTrace();
        }
        return "ERRO Desconhecido";
    }}