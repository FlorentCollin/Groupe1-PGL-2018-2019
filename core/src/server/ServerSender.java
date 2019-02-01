package server;

import com.google.gson.Gson;
import communication.Message;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerSender extends Thread{
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
                for (SocketChannel receiver : message.receivers) {
                    if (receiver.isConnected()) {
                        //Écriture du message dans le buffer du destinataire
                        receiver.write(ByteBuffer.wrap(gson.toJson(message).getBytes()));
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace(); //TODO
            }
        }
    }
}
