package ac.umons.slay.g01.server;

import java.nio.channels.SocketChannel;
import java.util.HashMap;

/**
 * Classe donnant des informations sur l'état actuel du server
 */
public class ServerInfo {

    private static HashMap<SocketChannel, Client> clients = new HashMap<>();

    public int getNumberOfPlayers() {
        return clients.size();
    }
    
    public static HashMap<SocketChannel, Client> getClients(){
    	return clients;
    }
}
