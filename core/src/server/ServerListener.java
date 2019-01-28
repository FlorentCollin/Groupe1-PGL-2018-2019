package server;

import com.google.gson.Gson;
import communication.Message;
import roomController.RoomController;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerListener extends Thread{
    private final Gson gson;
    private final LinkedBlockingQueue<Message> messageToSend;
    private final ServerSocketChannel serverChannel;
    private final Selector selector;
    private RoomController roomController;

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

    }
}
