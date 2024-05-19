package Util;

import java.io.*;
import java.net.*;

public class SocketSerialize {
    public static String serialize(Socket socket) {
        InetAddress address = socket.getInetAddress();
        int port = socket.getPort();
        return address.getHostAddress() + ":" + port;
    }

    // Deserialize String and recreate Socket
    public static Socket deserializeSocket(String serializedSocket) throws IOException {
        String[] parts = serializedSocket.split(":");
        String ipAddress = parts[0];
        int port = Integer.parseInt(parts[1]);
        InetAddress address = InetAddress.getByName(ipAddress);
        return new Socket(address, port);
    }
}
