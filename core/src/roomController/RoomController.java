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

    public RoomController(LinkedBlockingQueue<Message> messagesToSend) {
        rooms = new HashMap<>();
        this.messagesToSend = messagesToSend;
    }

    public void createRoom(Client creatorClient) {
        Room newRoom = new Room(creatorClient, new LinkedBlockingQueue<>(), messagesToSend);
        rooms.put(newRoom.getUUID(), newRoom);

    }
}
