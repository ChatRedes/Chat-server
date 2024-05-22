package Model;

public class Room {
    private int id;
    private String name;
    private String admin;
    private Boolean isPrivate;
    private String passhash;

    public Room(int id, String name, Boolean isPrivate, String passhash) {
        this.id = id;
        this.name = name;
        this.isPrivate = isPrivate;
        this.passhash = passhash;
    }
}
