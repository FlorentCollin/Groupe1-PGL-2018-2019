package communication;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingDeque;

public class OnlineMessageSender extends Thread {
    private LinkedBlockingDeque<Message> messagesToSend;
    private SocketChannel clientChannel;
    private Gson gson;

    public OnlineMessageSender(LinkedBlockingDeque<Message> messagesToSend, SocketChannel clientChannel) {
        this.messagesToSend = messagesToSend;
        this.clientChannel = clientChannel;
        gson = new Gson();
    }

    @Override
    public void run() {
        while(true) {
            try {
                Message message = messagesToSend.take();
                clientChannel.write(ByteBuffer.wrap(gson.toJson(message).getBytes()));
            } catch (InterruptedException | IOException e) {
                e.printStackTrace(); //TODO
            }
        }
    }
}
