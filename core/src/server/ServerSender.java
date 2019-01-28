package server;

import com.google.gson.Gson;
import communication.Message;

import java.util.concurrent.LinkedBlockingQueue;

public class ServerSender extends Thread{
    private volatile LinkedBlockingQueue<Message> messageToSend;
    private Message message;
    private Gson gson;

    public ServerSender(LinkedBlockingQueue<Message> messageToSend) {
        this.messageToSend = messageToSend;
        gson = new Gson();
    }

    @Override
    public void run() {
        while(true) {
            message = messageToSend.take();
            // TODO
        }
    }

}
