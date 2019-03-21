package communication;

import com.google.gson.Gson;
import communication.Messages.Message;
import gui.utils.GsonInit;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class qui représente un listener, elle s'occupe de récupérer les messages envoyés par le serveur et de les exécuter
 */
public class OnlineMessageListener extends MessageListener{

    private final SocketChannel clientChannel;
    private final Selector selector;
    private Gson gson;

    public OnlineMessageListener(SocketChannel clientChannel, Selector selector) {
        this.clientChannel = clientChannel;
        this.selector = selector;
        gson = GsonInit.initGson();
    }

    @Override
    public void run() {
        Thread.currentThread().setName("OnlineMessageListener");
        running.set(true);
        //Boucle infinie tant que le client est connecté au serveur
        while(running.get() && clientChannel.isConnected()) {
            try {
                readFromServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode qui va lire les messages du serveur et les exécuter.
     * @throws IOException
     */
    private void readFromServer() throws IOException {
        Iterator<SelectionKey> keyIterator;
        if(selector.select() != 0) {
            keyIterator = selector.selectedKeys().iterator();
            while(keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                System.out.println("Get key");
                if(key.isReadable()) { //Si le serveur à envoyé un message
                    ArrayList<Message> messages = Message.readFromKey(key, gson);
                    System.out.println("Messages.size() : " + messages.size());
                    messages.forEach(this::executeMessage);
                }
                keyIterator.remove();
            }
        }
    }
}
