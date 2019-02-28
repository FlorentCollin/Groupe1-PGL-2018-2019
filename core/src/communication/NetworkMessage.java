package communication;

import server.Client;

import java.util.List;

public abstract class NetworkMessage extends Message{

    private transient List<Client> clients;

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }
}
