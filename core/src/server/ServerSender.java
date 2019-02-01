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
            try {
                message = messageToSend.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
