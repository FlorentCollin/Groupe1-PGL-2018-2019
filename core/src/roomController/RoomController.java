package roomController;

import communication.Messages.*;
import org.pmw.tinylog.Logger;
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
     * @param creatorClient Le client qui crée la room
     * @param message Le message de création
     */
    private void createRoom(Client creatorClient, CreateRoomMessage message) {
        //Création d'un lien vers une file de messages pour pouvoir transmettre des messages des clients vers cette room
        LinkedBlockingQueue<Message> messagesFrom = new LinkedBlockingQueue<>();
        WaitingRoom room = new WaitingRoom(message, messagesFrom, messagesToSend);
        //Mise à jour du hashMap et démarrage de la room
        rooms.put(creatorClient, room);
        room.addClient(creatorClient);
        room.start();
    }

    /**
     * Méthode permettant d'envoyer un message à une room spécifique
     * @param message le message à envoyer
     * @param client le client qui à envoyé le message
     */
    public void sendMessageToRoom(Message message, Client client) {
        Room clientRoom = rooms.get(client);
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
        Logger.info(String.format("Managing Message : %s", message.getClass().getSimpleName()));
        if(message instanceof CreateRoomMessage) {
            CreateRoomMessage createRoomMessage = (CreateRoomMessage) message;
            createRoom(client, createRoomMessage);
            Logger.info(String.format("Creating GameRoom : %s", createRoomMessage.getRoomName()));
        } else if(message instanceof JoinRoomMessage) {
            Logger.info("A client has just joined a room");
            JoinRoomMessage joinRoomMessage = (JoinRoomMessage) message;
            for (Room room: rooms.values()) {
                if(room.getUUID().equals(joinRoomMessage.getId()) && ! room.isFull()) {
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

    /**
     * Méthode qui se charge de lancer la partie et de fermer la WaitingRoom
     * @param room la room à fermer
     */
    private void launchGame(WaitingRoom room) {
        try {
            //Envoie d'un message pour fermer la WaitingRoom
            room.getMessagesFrom().put(new TextMessage("close"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Création de la nouvelle room de jeu
        GameRoom gameRoom = new GameRoom(room.getBoard(), room.getMessagesFrom(), messagesToSend);
        gameRoom.start(); //Démarrage du thread
        for (Client client : room.getClients()) {
            //Mise à jour du HashMap des rooms
            rooms.replace(client, gameRoom);
            gameRoom.addClient(client); //Ajout du client dans la nouvelle room
        }
    }

    /**
     * Méthode qui vérifie si une room est vide.
     * Si c'est le cas alors on ferme la room.
     * Cette méthode est appelée lorsqu'un client se déconnecte
     * @param key la clé du client qui s'est déconnecté
     */
    public void checkEmpty(SelectionKey key) {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        Client client = ServerInfo.clients.get(clientChannel);
        Room room = rooms.get(client);
        if (room != null) {
            //On retire le client déconnecté de la room où il se trouvait et on le retire du hashmap des différentes rooms
            room.remove(client);
            rooms.remove(client);
            if (room.isEmpty()) {
                try {
                    //On envoie un message de fermeture à la room car celle-ci est vide
                    room.getMessagesFrom().put(new TextMessage("close"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Méthode qui envoie à un client la liste des WaitingRooms
     * Cette méthode est utilisé par le client lorsqu'il appuie sur le bouton refresh du OnlineMenuScreen
     * @param client le client qui a envoyé la requête
     */
    private void sendWaitingRooms(Client client) {
        ArrayList<String> waitingRooms = new ArrayList<>();
        ArrayList<Integer> nPlayer = new ArrayList<>();
        ArrayList<Integer> nPlayerIn = new ArrayList<>();
        ArrayList<UUID> ids = new ArrayList<>();
        for (Room room: rooms.values()) {
            if (room instanceof WaitingRoom) {
                WaitingRoom wr = (WaitingRoom) room;
                if (ids.indexOf(wr.getUUID()) == -1) {
                    //Ajout des caractéristiques de la room
                    waitingRooms.add(wr.getRoomName());
                    ids.add(wr.getUUID());
                    nPlayer.add(wr.getMaxClients());
                    nPlayerIn.add(wr.getNumberOfClients());
                }
            }
        }
        ListRoomsMessage message = new ListRoomsMessage(waitingRooms, ids, nPlayer, nPlayerIn);
        message.setClients(Collections.singletonList(client));
        try {
            messagesToSend.put(message); //Envoie du message
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int numberRooms() {
        ArrayList<Room> temp = new ArrayList<>();
        for (Room room : rooms.values()) {
            if(temp.indexOf(room) == -1)
                temp.add(room);
        }
        return temp.size();
    }
}
