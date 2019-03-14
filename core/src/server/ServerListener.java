package server;

import com.google.gson.Gson;
import communication.Messages.Message;
import communication.Messages.UsernameMessage;
import gui.utils.GsonInit;
import roomController.RoomController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread qui se charge d'accepter les nouveaux clients et de réceptionner
 * les messages des différents clients
 */
public class ServerListener extends Thread{
    private final Gson gson;
    //Lien vers la file des messages du ServerSender.
    private final LinkedBlockingQueue<Message> messageToSend;
    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private RoomController roomController;
    private SocketChannel clientChannel;

    /**
     * Constructeur du ServerListener
     * @param port le port sur lequel les clients peuvent se connecter
     * @param messageToSend Lien vers la file des messages du ServerSender,
     *                      utilisé pour indiquer à chaque nouvelle room à quel endroit elle peut envoyer ses messages
     * @throws IOException
     */
    public ServerListener(int port, LinkedBlockingQueue<Message> messageToSend) throws IOException {
        gson = GsonInit.initGson();
        roomController = new RoomController(messageToSend);
        this.messageToSend = messageToSend;
        serverChannel = ServerSocketChannel.open(); //Ouverture du socket
        serverChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        /* Le serveur n'est pas bloquant cela signifie qu'il ne va pas attendre qu'un client en particulier lui
        * envoie des données. Il va au contraire regarder si un des clients lui a envoyé des données
        * et si il y a des données dans le buffer du client alors il va s'occuper de les récupérer*/
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port)); //On lie le serveur au port donné en paramètre

        //Création du selector qui retiens les différents clients connectés
        selector = Selector.open();
        //On associe le Selector au serveur, et place le selector dans un mode où il peut accepter les connections des clients
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        Iterator<SelectionKey> keyIterator;
        while(serverChannel.isOpen()) { //Boucle infinie tant que le serveur est up
            try {
                //Récupération des différentes clés qui ont envoyés un message au serveur (méthode bloquante)
                if(selector.select() != 0) {
                    keyIterator = selector.selectedKeys().iterator();
                    while(keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        if(key.isAcceptable()) {
                            keyIsAcceptable(); //Signifie qu'un nouveau client se connecte au serveur
                        } else if(key.isReadable()) {
                            keyIsReadable(key); //Signifie qu'un client à envoyé un message
                        }
                        keyIterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(); //TODO
            }

        }
    }

    /**
     * Méthode qui ajoute un client à la liste des clients connectés
     */
    private void keyIsAcceptable() {
        try {
            //Acceptation du client
            clientChannel = serverChannel.accept();
            clientChannel.configureBlocking(false);
            //On permet au client d'écrire
            clientChannel.register(selector, SelectionKey.OP_READ);
            Client client = new Client(clientChannel);
            ServerInfo.clients.put(clientChannel, client);
            System.out.println("Number of player : " + ServerInfo.clients.size());
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        }
    }

    /**
     * Méthode qui lit et envoie le message au roomController qui se charge de répartir le message
     * @param key la clé du client
     */
    private void keyIsReadable(SelectionKey key) {
        clientChannel = (SocketChannel) key.channel();
        try {
            //Récupération du message dans le buffer du client.
            String messageStr = Message.getStringFromBuffer(clientChannel, (String) key.attachment());
            //On définit la fin d'un message par le symbole "+"
            if(!messageStr.endsWith("+")) { //Si le message n'est pas terminé alors on enregistre le message à la clé
                key.attach(messageStr);
            } else {
                //On supprime le symbole de fin (ie : "+")
                String[] split = messageStr.split("\\+");
                if(split.length >= 2) {
                    //Un deuxième message à déjà été lu
                    key.attach(split[1]);
                } else {
                    //Le message est terminé, et on retire le message attaché à la clé
                    key.attach(null);
                }
                messageStr = messageStr.substring(0, messageStr.length()-1);
                //Désérialisation du message
                Message message = Message.getMessage(messageStr, gson);
                message.setClient(ServerInfo.clients.get(clientChannel));
                if(message instanceof UsernameMessage) {
                    ServerInfo.clients.get(clientChannel).setUsername(((UsernameMessage) message).getUsername());
                } else {
                    //Si le message n'est pas un message réservé au serveur alors on l'envoie au roomController
                    roomController.manageMessage(ServerInfo.clients.get(clientChannel), message);
                }
            }

        } catch (IOException e) {
            System.out.println("Client connection lost");
            //Vérifie si la room associé au client n'est pas vide :
            //Si la room est vide alors elle est supprimée
            roomController.checkEmpty(key);
            //Suppression du client de la liste des clients connectés et du selector
            key.cancel();
            ServerInfo.clients.remove(clientChannel);
        }
    }

    public Selector getSelector() {
        return selector;
    }
}
