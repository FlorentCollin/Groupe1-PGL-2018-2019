package communication;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

public class OnlineMessageListener extends MessageListener{

    private final SocketChannel clientChannel;
    private final Selector selector;
    private final Gson gson;

    public OnlineMessageListener(LinkedBlockingQueue<Message> messagesFrom, SocketChannel clientChannel, Selector selector) {
        super(messagesFrom);
        this.clientChannel = clientChannel;
        this.selector = selector;
        gson = new Gson();
    }

    @Override
    public void run() {
        running.set(true);
        while(running.get()) {
            try {
                readFromServer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readFromServer() throws IOException {
        Iterator<SelectionKey> keyIterator;
        if(selector.select() != 0) {
            keyIterator = selector.selectedKeys().iterator();
            while(keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if(key.isReadable()) {
                    String messageStr = Message.getStringFromBuffer(clientChannel);
                    Message message = Message.getMessage(messageStr, gson);
                    executeMessage(message);
                }
            }
        }
    }
}
