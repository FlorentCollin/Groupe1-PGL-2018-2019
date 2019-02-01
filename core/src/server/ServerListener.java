package server;

import com.google.gson.Gson;
import communication.Message;
import roomController.RoomController;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerListener extends Thread{
    private final Gson gson;
    private final LinkedBlockingQueue<Message> messageToSend;
    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private RoomController roomController;
    private SocketChannel clientChannel;

    public ServerListener(int port, LinkedBlockingQueue<Message> messageToSend) throws IOException {
        gson = new Gson();
        roomController = new RoomController(messageToSend);
        this.messageToSend = messageToSend;
        serverChannel = ServerSocketChannel.open(); //Ouverture du serveur
        serverChannel.configureBlocking(false);

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
                            keyIsAcceptable();
                        } else if(key.isReadable()) {
                            keyIsReadable(key);
                        }
                        keyIterator.remove();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace(); //TODO
            }

        }
    }

    private void keyIsAcceptable() throws IOException {
        clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        Client client = new Client(clientChannel);
        ServerInfo.clients.put(clientChannel, client);
    }

    private void keyIsReadable(SelectionKey key) throws IOException {
        clientChannel = (SocketChannel) key.channel();
        if (!clientChannel.isConnected()) {
            //On retire le client si celui ci n'est plus connecté
            ServerInfo.clients.remove(clientChannel);
        }
        //Boucle qui lit les données envoyées par le client et place ces données dans un string
        String messageStr = "";
        do {
            ByteBuffer buffer = ByteBuffer.allocate(100);
            buffer.clear();
            int len = clientChannel.read(buffer);
            messageStr += new String(buffer.array(), StandardCharsets.UTF_8);
        } while (len != 0 || len != 100);
        Message message = gson.fromJson(messageStr, Message.class);
        //TODO
    }
}
