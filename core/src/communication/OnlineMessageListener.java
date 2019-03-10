package communication;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import communication.Messages.Message;
import gui.utils.GsonInit;

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
                if(key.isReadable()) { //Si le serveur à envoyé un message
                    //Récupération du string correspondant au message
                    String messageStr = Message.getStringFromBuffer(clientChannel, (String) key.attachment());
                    if(!messageStr.endsWith("+")) {
                        key.attach(messageStr);
                    } else {
                        key.attach(null);
                        messageStr = messageStr.substring(0, messageStr.length()-1);
                        FileHandle file = new FileHandle("core.json");
                        file.writeString(messageStr, false);
                        //Désérialization du string en un message
                        Message message = Message.getMessage(messageStr, gson);
                        executeMessage(message); //Exécution
                    }
                        keyIterator.remove();
                }
            }
        }
    }
}
