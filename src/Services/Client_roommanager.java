package Services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Client_roommanager {
    String insertQuery = "INSERT INTO CLIENT_ROOM (username, room_name, administrador) VALUES (?, ?, ?);";
    String deletQuery;

    private void Entrada_sala(String username, String room_name, Boolean administrador){
        try {
            Server server;
            Connection conn = server.conectWithDatabase();
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, room_name);
            preparedStatement.setBoolean(3,administrador);// Aqui estou assumindo que o método toString() retorna o valor desejado
            preparedStatement.executeUpdate();
            preparedStatement.close();
    } catch (
    SQLException e) {
        e.printStackTrace(); // ou trate o erro de acordo com sua lógica de aplicação
    }

    private void Saida_sala(String username, String room_name, Boolean administrador){
        try {
            if (!administrador) {
                deletQuery = "DELETE FROM CLIENT_ROOM WHERE USERNAME = '" + username + "' AND ROOM_NAME = " + room_name + "';";
            }else{
                deletQuery = "DELETE CASCADE FROM CHAT WHERE ROOM_NAME = '" + room_name + "';";
            }
            Server server;
            Connection conn = server.conectWithDatabase(deletQuery);
            PreparedStatement preparedStatement = conn.prepareStatement(deletQuery);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (
                SQLException e) {
            e.printStackTrace(); // ou trate o erro de acordo com sua lógica de aplicação
        }

    }
}
