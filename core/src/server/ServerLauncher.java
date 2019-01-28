package server;

import communication.Message;

import java.util.concurrent.LinkedBlockingQueue;

public class ServerLauncher {

    public static void main(String[] args) {
        System.out.println("Starting server...");
        LinkedBlockingQueue<Message> messageToSend = new LinkedBlockingQueue<>();
        ServerListener serverListener = new ServerListener();
        ServerSender serverSender = new ServerSender(messageToSend);
        serverListener.openServer(8888, messageToSend);
        serverSender.start();
        serverListener.start();
    }
}
