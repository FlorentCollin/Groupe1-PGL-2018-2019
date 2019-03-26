package server;

import java.nio.channels.SocketChannel;
import java.util.UUID;

/**
 * Classe qui réprésente un client. Un client possède un SocketChannel (qui permet au server sender
 * de trouver le channel où envoyer) et un username
 */
public class Client {
    /*
        Channel de communication utilisé par le serveur pour savoir à quel client envoyer le message
     */
    private SocketChannel socketChannel;
    private String username; //Nom de l'utilisateur pour pouvoir l'indentifié lors de partie en ligne
    private int numberOfPlayer;

    public Client(int numberOfPlayer, SocketChannel socketChannel) {
        this.username = "Player";
        this.numberOfPlayer = numberOfPlayer;
        this.socketChannel = socketChannel;
    }
    public Client(String username, int numberOfPlayer, SocketChannel socketChannel) {
        this.username = username;
        this.numberOfPlayer = numberOfPlayer;
        this.socketChannel = socketChannel;
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    public String getUsername() {
        return username;
    }

    public int getNumberOfPlayer() {
        return numberOfPlayer;
    }

    public void setInfo(String username, int numberOfPlayer) {
        this.username = username;
        this.numberOfPlayer = numberOfPlayer;
    }
}
