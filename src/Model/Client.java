package Model;

public class Client {
    private int id;
    private String username;
    private String socket;

    public Client(int id, String username, String socket) {
        this.id = id;
        this.username = username;
        this.socket = socket;
    }

    public String getUsername() {
        return username;
    }

    public String getSocket() {
        return socket;
    }
}
