package server;

import com.google.gson.Gson;
import communication.Messages.Message;
import communication.Messages.UsernameMessage;
import gui.utils.GsonInit;
import roomController.RoomController;

import java.io.IOException;
import java.net.InetSocketAddress;
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
        serverChannel = ServerSocketChannel.open(); //Ouverture du serveur
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(port)); //On lie le serveur au port donné en paramètre

        //Création du selector qui retiens les différents clients connectés
        selector = Selector.open();
        //On associe le Selector au serveur, et on met le selector en mode accept
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        Iterator<SelectionKey> keyIterator;
        while(serverChannel.isOpen()) { //Boucle infinie tant que le serveur est up
            try {
                if(selector.select() != 0) { //Récupération des différentes clés qui ont envoyés un message au serveur
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
     * Méthode qui ajoute un client à la liste des clients du serveur
     * @throws IOException
     */
    private void keyIsAcceptable() {
        try {
            clientChannel = serverChannel.accept();
            clientChannel.configureBlocking(false);
            //On permet au client de lire et d'écrire des messages
            clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            Client client = new Client(clientChannel);
            ServerInfo.clients.put(clientChannel, client);
            System.out.println("Number of player : " + ServerInfo.clients.size());
        } catch (IOException e) {
            e.printStackTrace(); //TODO
        }
    }

    /**
     * Méthode qui lit et envoie le message au roomController qui se charge de répartir le message
     * @param key
     * @throws IOException
     */
    private void keyIsReadable(SelectionKey key) {
        clientChannel = (SocketChannel) key.channel();
        //TODO create a MessageControlCenter to manage et distribute message to server/room depending on the class's message
        //Récupération du message dans le buffer du client.
        try {
            String messageStr = Message.getStringFromBuffer(clientChannel, (String) key.attachment());
            if(!messageStr.endsWith("+")) {
                key.attach(messageStr);
            } else {
                key.attach(null);
                messageStr = messageStr.substring(0, messageStr.length()-1);
                Message message = Message.getMessage(messageStr, gson);
                message.setClient(ServerInfo.clients.get(clientChannel));
                if(message instanceof UsernameMessage) {
                    ServerInfo.clients.get(clientChannel).setUsername(((UsernameMessage) message).getUsername());
                } else {
                    roomController.manageMessage(ServerInfo.clients.get(clientChannel), message);
                }
            }

        } catch (IOException e) {
            System.out.println("Client connection lost");
            key.cancel();
            ServerInfo.clients.remove(clientChannel);
        }
    }
}
