package server;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.UUID;

/**
 * Classe donnant des informations sur l'état actuel du server
 */
public class ServerInfo {

    public static HashMap<SocketChannel, Client> clients = new HashMap<>();

    public int getNumberOfPlayers() {
        return clients.size();
    }
}
