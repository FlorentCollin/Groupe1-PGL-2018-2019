package roomController;

import communication.Message;
import server.Client;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class RoomController {
    private HashMap<Client, Room> rooms;
    //Permet de retrouver la file des messages d'une Room particulière
    private HashMap<Room, LinkedBlockingQueue<Message>> roomQueue;
    //Lien vers la pile des messages à envoyer aux clients (utilisé par ServerSender)
    private LinkedBlockingQueue<Message> messagesToSend;

    public RoomController(LinkedBlockingQueue<Message> messageToSend) {
        rooms = new HashMap<>();
        roomQueue = new HashMap<>();
        this.messagesToSend = messageToSend;
    }

    /**
     * Méthode utilisé par un client pour ouvrir une nouvelle Room
     * @param creatorClient
     * @return
     */
    public void createRoom(Client creatorClient, String worldName) {
        LinkedBlockingQueue<Message> messagesFrom = new LinkedBlockingQueue<>();
        Room room = new Room(worldName, messagesFrom, messagesToSend);
        //Mise à jour des hashMap
        rooms.put(creatorClient, room);
        roomQueue.put(room, messagesFrom);
    }

    /**
     * Méthode permettant d'envoyer un message à une room spécifique
     * @param message le message à envoyer
     * @param client le client qui à envoyé le message
     */
    public void sendMessageToRoom(Message message, Client client) {
        Room clientRoom = rooms.get(client);
        LinkedBlockingQueue<Message> clientRoomQueue = roomQueue.get(clientRoom);
        try {
            clientRoomQueue.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace(); //Si la Room n'existe plus TODO
        }
    }
}
