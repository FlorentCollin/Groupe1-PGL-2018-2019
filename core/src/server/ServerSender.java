package server;

import com.google.gson.Gson;
import communication.Message;
import communication.NetworkMessage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerSender extends Thread {
    private LinkedBlockingQueue<Message> messageToSend;
    private Message message;
    private Gson gson;

    public ServerSender(LinkedBlockingQueue<Message> messageToSend) {
        this.messageToSend = messageToSend;
        gson = new Gson();
    }

    @Override
    public void run() {
        while (true) {
            try {
                message = messageToSend.take(); //Remarque cette méthode est bloquante
                if (message instanceof NetworkMessage) {
                    NetworkMessage networkMessage = (NetworkMessage) message;
                    for (Client client : networkMessage.getClients()) {
                        SocketChannel clientChannel = client.getSocketChannel();
                        if (clientChannel.isConnected()) {
                            //Écriture du message dans le buffer du destinataire
                            clientChannel.write(ByteBuffer.wrap((message.getClass().getSimpleName() + gson.toJson(message)).getBytes()));
                        }
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace(); //TODO
            }
        }
    }
}
