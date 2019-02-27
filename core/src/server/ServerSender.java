package server;

import com.google.gson.Gson;
import communication.Message;
import communication.UpdateMessage;

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
                if (message instanceof UpdateMessage) {
                    UpdateMessage updateMessage = (UpdateMessage) message;
                    for (Client client : updateMessage.getClients()) {
                        SocketChannel clientChannel = client.getSocketChannel();
                        if (clientChannel.isConnected()) {
                            //Écriture du message dans le buffer du destinataire
                            clientChannel.write(ByteBuffer.wrap((message.getClass() + gson.toJson(updateMessage)).getBytes()));
                        }
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace(); //TODO
            }
        }
    }
}
