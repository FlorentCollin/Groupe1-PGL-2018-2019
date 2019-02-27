package communication;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingDeque;

public class OnlineMessageSender implements MessageSender {
    private SocketChannel clientChannel;
    private Selector selector;
    private Gson gson;

    public OnlineMessageSender() {
        gson = new Gson();
        try {
            clientChannel = SocketChannel.open(new InetSocketAddress("localhost", 8888));
            clientChannel.configureBlocking(false);
            selector = Selector.open();
            //On associe le channel à un selector
            clientChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void send(Message message) {
        try {
            clientChannel.write(ByteBuffer.wrap((message.getClass() + gson.toJson(message)).getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
