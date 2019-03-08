package roomController;

import communication.CreateRoomMessage;
import communication.JoinRoomMessage;
import communication.Message;
import server.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Classe qui gère l'ensemble des parties en ligne (rooms) du serveur
 */
public class RoomController {
    private HashMap<Client, Room> rooms;
    // roomQueue : permet de retrouver la file des messages d'une Room particulière
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
     */
    public void createRoom(Client creatorClient, String worldName, boolean isNaturalDisastersOn) {
        //Création d'un lien vers une file de messages pour pouvoir transmettre des messages des clients vers cette room
        LinkedBlockingQueue<Message> messagesFrom = new LinkedBlockingQueue<>();
        Room room = new Room(worldName, isNaturalDisastersOn, new ArrayList<>(), messagesFrom, messagesToSend);
        //Mise à jour des hashMaps
        rooms.put(creatorClient, room);
        roomQueue.put(room, messagesFrom);
        room.addClient(creatorClient);
        room.start(); //Démarrage de la room.
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

    /**
     * Méthode qui va s'occuper d'effectuer l'action d'un message en fonction du type de message
     * @param client Le client qui à envoyé le message
     * @param message Le message envoyé par le client
     */
    public void manageMessage(Client client, Message message) {
        if(message instanceof CreateRoomMessage) {
            System.out.println("Creating Room");
            CreateRoomMessage createRoomMessage = (CreateRoomMessage) message;
            createRoom(client, createRoomMessage.getWorldName(), createRoomMessage.isNaturalDisastersOn());
        } else if(message instanceof JoinRoomMessage) { //TODO Need refactoring
            Client aClient = rooms.keySet().iterator().next();
            Room room = rooms.get(aClient);
            room.addClient(client);
            rooms.put(client, room);
        } else {
            //Si le message n'est pas un message que le roomController peut gérer,
            //c'est qu'il doit l'envoyer à la room correspondante.
            sendMessageToRoom(message, client);
        }
    }
}
