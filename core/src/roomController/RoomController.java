package roomController;

import communication.Messages.CreateRoomMessage;
import communication.Messages.JoinRoomMessage;
import communication.Messages.Message;
import communication.Messages.TextMessage;
import server.Client;
import server.ServerInfo;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Classe qui gère l'ensemble des parties en ligne (rooms) du serveur
 */
public class RoomController {
    private HashMap<Client, Room> rooms;
    // roomQueue : permet de retrouver la file des messages d'une GameRoom particulière
    //Lien vers la pile des messages à envoyer aux clients (utilisé par ServerSender)
    private LinkedBlockingQueue<Message> messagesToSend;

    public RoomController(LinkedBlockingQueue<Message> messageToSend) {
        rooms = new HashMap<>();
        this.messagesToSend = messageToSend;
    }

    /**
     * Méthode utilisé par un client pour ouvrir une nouvelle GameRoom
     * @param creatorClient
     */
    public void createRoom(Client creatorClient, CreateRoomMessage message) {
        //Création d'un lien vers une file de messages pour pouvoir transmettre des messages des clients vers cette room
        LinkedBlockingQueue<Message> messagesFrom = new LinkedBlockingQueue<>();
        WaitingRoom room = new WaitingRoom(message, messagesFrom, messagesToSend);
        //Mise à jour des hashMaps
        rooms.put(creatorClient, room);
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
        System.out.println("Rooms size : " + rooms.size());
        try {
            message.setClient(client);
            clientRoom.getMessagesFrom().put(message);
            System.out.println("I'm Sending message to room");
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
        System.out.println("Room Controller - Managing Message : " + message.getClass().getSimpleName());
        if(message instanceof CreateRoomMessage) {
            System.out.println("Creating GameRoom");
            CreateRoomMessage createRoomMessage = (CreateRoomMessage) message;
            createRoom(client, createRoomMessage);
        } else if(message instanceof JoinRoomMessage) { //TODO Need refactoring
            Client aClient = rooms.keySet().iterator().next();
            Room room = rooms.get(aClient);
            room.addClient(client);
            rooms.put(client, room);
        } else if(message instanceof TextMessage) {
            if(((TextMessage) message).getMessage().equals("launchGame")) {
                WaitingRoom room = (WaitingRoom) rooms.get(client);
                if(room.isReady()) {
                    launchGame(room);
                }
            } else {
                sendMessageToRoom(message, client);
            }
        } else {
            //Si le message n'est pas un message que le roomController peut gérer,
            //c'est qu'il doit l'envoyer à la room correspondante.
            sendMessageToRoom(message, client);
        }
    }

    private void launchGame(WaitingRoom room) {
        try {
            room.getMessagesFrom().put(new TextMessage("close"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        GameRoom gameRoom = new GameRoom(room.getBoard(), room.getMessagesFrom(), messagesToSend);
        gameRoom.start();
        for (Client client : room.getClients()) {
            rooms.replace(client, gameRoom);
            gameRoom.addClient(client);
        }
    }

    public void checkEmpty(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        Client client = ServerInfo.clients.get(clientChannel);
        Room room = rooms.get(client);
        room.remove(client);
        if(room.isEmpty()) {
            try {
                room.getMessagesFrom().put(new TextMessage("close"));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rooms.remove(client);
        }
        System.out.println("Check Empty");
        System.out.println(rooms.size());

    }
}
