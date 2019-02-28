package communication;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import logic.board.Board;
import logic.item.Capital;
import logic.item.Item;
import logic.item.Soldier;
import logic.item.Tree;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class OnlineMessageListener extends MessageListener{

    private final SocketChannel clientChannel;
    private final Selector selector;
    private Gson gson;

    public OnlineMessageListener(SocketChannel clientChannel, Selector selector) {
        this.clientChannel = clientChannel;
        this.selector = selector;
        RuntimeTypeAdapterFactory<Item> runtimeTypeAdapterFactory = RuntimeTypeAdapterFactory
                .of(Item.class, "type")
                .registerSubtype(Capital.class, Capital.class.getName())
                .registerSubtype(Soldier.class, Soldier.class.getName())
                .registerSubtype(Tree.class, Tree.class.getName());

        gson = new GsonBuilder().registerTypeAdapterFactory(runtimeTypeAdapterFactory).create();
//        gson = new Gson();
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
                keyIterator.remove();
            }
        }
    }
}
