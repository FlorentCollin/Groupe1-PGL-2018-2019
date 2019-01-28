package roomController;

import communication.Message;
import server.Client;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class RoomController {
    private HashMap<UUID, Room> rooms;
    //Lien vers la pile des messages à envoyer aux clients (utilisé par ServerSender)
    private LinkedBlockingQueue<Message> messagesToSend;

    public HashMap<UUID, Room> getRooms() {
        return rooms;
    }
    public RoomController(LinkedBlockingQueue<Message> messagesToSend) {
        rooms = new HashMap<>();
        this.messagesToSend = messagesToSend;
    }

    public UUID createRoom(Client creatorClient) {
        Room newRoom = new Room(creatorClient, new LinkedBlockingQueue<>(), messagesToSend);
        rooms.put(newRoom.getUUID(), newRoom);
        return newRoom.getUUID();
    }

    /**
     * Méthode permettant d'envoyer un message à une room spécifique
     * @param idRoom l'id de la room
     * @param message le message à envoyer
     * @throws InterruptedException
     */
    public void sendMessageToRoom(UUID idRoom, Message message) throws InterruptedException {
        rooms.get(idRoom).getMessagesFromServer().put(message); //todo transform message
    }
}
