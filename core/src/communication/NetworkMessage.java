package communication;

import server.Client;

import java.util.List;

/**
 * Classe qui représente un message qui est envoyable à des clients.
 */
public abstract class NetworkMessage extends Message{

    /* Liste des clients, utilisé par le serveur sender pour savoir à quel client envoyé ce message.
      * Note : cette variable est Transient car :
      * 1) il n'est pas possible de sérializer un SocketChannel
      * 2) Le client n'a pas besoin de savoir à qui le message à été envoyé.
    */
    private transient List<Client> clients;

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }
}
