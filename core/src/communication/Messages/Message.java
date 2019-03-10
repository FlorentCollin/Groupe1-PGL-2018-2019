package communication.Messages;


import com.google.gson.Gson;
import server.Client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * Classe qui représente un message, cette classe est utilisé par l'ensemble des threads pour communiquer entre eux
 * des informations.
 * Chaque message contient une information bien précise tel que créer une room, update le board, etc,...
 * Ex : un message de classe CreateRoomMessage, est un message envoyé par un client au serveur qui demande au serveur
 * de créer une GameRoom.
 */
public abstract class Message {

    private transient Client client;

    /**
     * Méthode qui retourne un Message correspondant à la classe précisé dans le début du String messageStr
     * Ex : messageStr = InitMessage{...} (à l'intérieur des curly brackets doit se trouver le message sérializé.
     * @param messageStr Le nom de la classe associé au message sérialize + le message sérializé.
     * @param gson le gson permettant de désérializé le message
     * @return Un message du type précisé dans le début du messageStr
     */
    public static Message getMessage(String messageStr, Gson gson) {
        //Récupération de la classe associé au message
        String className = messageStr.substring(0, messageStr.indexOf("{"));
        //Récupération du string représentant le message sérialisé
        messageStr = messageStr.substring(messageStr.indexOf("{"));
        switch(className) {
            case "InitMessage":
                return gson.fromJson(messageStr, InitMessage.class);
            case "PlayMessage":
                return gson.fromJson(messageStr, PlayMessage.class);
            case "TextMessage":
                return gson.fromJson(messageStr, TextMessage.class);
            case "GameUpdateMessage":
                return gson.fromJson(messageStr, GameUpdateMessage.class);
            case "CreateRoomMessage":
                return gson.fromJson(messageStr, CreateRoomMessage.class);
            case "JoinRoomMessage":
                return gson.fromJson(messageStr, JoinRoomMessage.class);
            case "ShopMessage":
                return gson.fromJson(messageStr, ShopMessage.class);
            case "RoomUpdateMessage":
                return gson.fromJson(messageStr, RoomUpdateMessage.class);
            case "UsernameMessage":
                return gson.fromJson(messageStr, UsernameMessage.class);
            default: return null;
        }
    }

    /**
     * Méthode qui va lire les données qui sont contenus dans le buffer d'un SocketChannel
     * et retourné le String correspondant aux bytes contenu dans le buffer.
     * Typiquement, cette méthode est appelé lorsqu'un message est reçu que ce soit côté client ou serveur.
     * @param clientChannel le SocketChannel dont on veux aller lire le buffer
     * @return Le String associé aux bytes qui se trouvaient dans le buffer
     * @throws IOException
     */
    public static String getStringFromBuffer(SocketChannel clientChannel, String previousStr) throws IOException {
        //Boucle qui lit les données envoyées par le client et place ces données dans un string
        StringBuilder messageStr;
        if(previousStr == null) {
            messageStr = new StringBuilder();
        } else {
            messageStr = new StringBuilder(previousStr);
        }
        int bufferSize = 100; //Arbitraire
        ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
        while(clientChannel.read(buffer) > 0) {
            buffer.clear();
            messageStr.append(new String(buffer.array(), StandardCharsets.UTF_8));
            buffer.flip();
        }
        return messageStr.toString().trim(); //Le trim permet d'enlever les espaces avant et après un string
    }


    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
