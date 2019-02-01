package server;

import java.nio.channels.SocketChannel;
import java.util.UUID;

public class Client {

    private UUID id;
    private SocketChannel socketChannel;
    private String username; //Nom de l'utilisateur pour pouvoir l'indentifié lors de partie en ligne

    public Client(SocketChannel socketChannel) {
        this.username = "Player";
        this.socketChannel = socketChannel;
        id = UUID.randomUUID();
    }
    public Client(String username, SocketChannel socketChannel) {
        this.username = username;
        this.socketChannel = socketChannel;
        id = UUID.randomUUID();
    }

    public UUID getId() {
        return id;
    }
}
