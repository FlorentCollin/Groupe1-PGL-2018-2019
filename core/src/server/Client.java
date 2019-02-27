package server;

import java.nio.channels.SocketChannel;
import java.util.UUID;

public class Client {
    /*
        Channel de communication utilisé par le serveur pour savoir à quel client envoyé le message
     */
    private SocketChannel socketChannel;
    private String username; //Nom de l'utilisateur pour pouvoir l'indentifié lors de partie en ligne

    public Client(SocketChannel socketChannel) {
        this.username = "Player";
        this.socketChannel = socketChannel;
    }
    public Client(String username, SocketChannel socketChannel) {
        this.username = username;
        this.socketChannel = socketChannel;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }
}
