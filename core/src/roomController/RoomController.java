package roomController;

import communication.Messages.*;
import server.Client;
import server.ServerInfo;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Classe qui gère l'ensemble des parties en ligne (rooms) du serveur
 */
public class RoomController {
    private HashMap<Client, Room> rooms;
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
            JoinRoomMessage joinRoomMessage = (JoinRoomMessage) message;
            for (Room room: rooms.values()) {
                if(room.getUUID().equals(joinRoomMessage.getId())) {
                    room.addClient(client);
                    rooms.put(client, room);
                    break;
                }
            }
        } else if(message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            switch (textMessage.getMessage()) {
                case "launchGame":
                    WaitingRoom room = (WaitingRoom) rooms.get(client);
                    if(room.isReady()) {
                        launchGame(room);
                    } break;
                case "getWaitingRooms":
                    sendWaitingRooms(client); break;
                default:
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
        if (room != null) {
            room.remove(client);
            if (room.isEmpty()) {
                try {
                    room.getMessagesFrom().put(new TextMessage("close"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rooms.remove(client);
            }
        }
    }

    private void sendWaitingRooms(Client client) {
        ArrayList<String> waitingRooms = new ArrayList<>();
        ArrayList<Integer> nPlayer = new ArrayList<>();
        ArrayList<Integer> nPlayerIn = new ArrayList<>();
        ArrayList<UUID> ids = new ArrayList<>();
        for (Room room: rooms.values()) {
            if(room instanceof WaitingRoom) {
                WaitingRoom wr = (WaitingRoom) room;
                waitingRooms.add(wr.getRoomName());
                ids.add(wr.getUUID());
                nPlayer.add(wr.getMaxClients());
                nPlayerIn.add(wr.getNumberOfClients());
            }
        }
        ListRoomsMessage message = new ListRoomsMessage(waitingRooms, ids, nPlayer, nPlayerIn);
        message.setClients(Collections.singletonList(client));
        try {
            messagesToSend.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
