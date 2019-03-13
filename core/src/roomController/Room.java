package roomController;

import communication.Messages.Message;
import server.Client;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Room extends Thread {

    private UUID id = UUID.randomUUID();
    LinkedBlockingQueue<Message> messagesFrom;
    LinkedBlockingQueue<Message> messagesToSend;
    ArrayList<Client> clients = new ArrayList<>();
    AtomicBoolean running = new AtomicBoolean(false);

    Room() {}

    public void addClient(Client client) {
        clients.add(client);
    }
    public LinkedBlockingQueue<Message> getMessagesFrom() {
        return messagesFrom;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public boolean remove(Client client) {
        return clients.remove(client);
    }

    public boolean isEmpty() {
        return clients.size() == 0;
    }

    public UUID getUUID() {
        return id;
    }

    public boolean isFull() {
        return true;
    }
}
