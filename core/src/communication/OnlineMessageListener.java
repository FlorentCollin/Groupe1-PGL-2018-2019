package communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import logic.item.Capital;
import logic.item.Item;
import logic.item.Soldier;
import logic.item.Tree;
import logic.item.level.Level;
import logic.item.level.SoldierLevel;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
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
        //TODO NEED SEE APACHE LICENSE 2.0
        //TODO THIS RUNTIMETYPEADAPTERFACTORY NEED TO BE ON A SIDE METHOD TO BE REUSE
        //Création du Gson modifié pour pouvoir, désérializer des items selon leur classe respective
        RuntimeTypeAdapterFactory<Item> itemTypeAdapter = RuntimeTypeAdapterFactory
                .of(Item.class, "type")
                .registerSubtype(Capital.class, Capital.class.getName())
                .registerSubtype(Soldier.class, Soldier.class.getName())
                .registerSubtype(Tree.class, Tree.class.getName());
        RuntimeTypeAdapterFactory<Level> levelTypeAdapter = RuntimeTypeAdapterFactory
                .of(Level.class, "type")
                .registerSubtype(SoldierLevel.class, SoldierLevel.class.getName());
        gson = new GsonBuilder().registerTypeAdapterFactory(itemTypeAdapter).registerTypeAdapterFactory(levelTypeAdapter).create();
    }

    @Override
    public void run() {
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
                if(key.isReadable()) { //Si le serveur à envoyé un message
                    //Récupération du string correspondant au message
                    String messageStr = Message.getStringFromBuffer(clientChannel);
                    //Désérialization du string en un message
                    Message message = Message.getMessage(messageStr, gson);
                    executeMessage(message); //Exécution
                }
                keyIterator.remove();
            }
        }
    }
}
