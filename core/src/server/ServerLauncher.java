package server;

import communication.Message;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerLauncher {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server...");
        LinkedBlockingQueue<Message> messageToSend = new LinkedBlockingQueue<>();
        ServerListener serverListener = new ServerListener(8888, messageToSend);
        ServerSender serverSender = new ServerSender(messageToSend);
        serverSender.start();
        serverListener.start();
    }
}
