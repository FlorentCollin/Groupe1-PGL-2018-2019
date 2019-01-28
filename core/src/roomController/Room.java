package roomController;

import communication.Message;
import server.Client;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class Room extends Thread{

    private UUID id; //Identifiant unique d'une room
    private ArrayList<Client> players;
    private LinkedBlockingQueue<Message> messagesFromServer;
    private LinkedBlockingQueue<Message> messagesToSend;

    public UUID getUUID() {
        return id;
    }
    public LinkedBlockingQueue<Message> getMessagesFromServer() {
        return messagesFromServer;
    }
    public Room(Client creatorClient,LinkedBlockingQueue<Message> messagesFromServer, LinkedBlockingQueue<Message> messagesToSend) {
        id = UUID.randomUUID(); //Création d'un identifiant unique pour la room
        players = new ArrayList<>();
        players.add(creatorClient);
        this.messagesFromServer = messagesFromServer;
        this.messagesToSend = messagesToSend;
    }

    @Override
    public void run() {
        while(true) {
            //TODO
        }
    }

}
