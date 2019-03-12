package com.gl1.slay.server;

import communication.Messages.Message;
import server.ServerListener;
import server.ServerSender;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import static gui.utils.Constants.PORT;

/**
 * Classe qui démarre les différents threads liés au serveur
 */
public class ServerLauncher {

    public static void main(String[] args) throws IOException {
        System.out.println("Starting server...");
        LinkedBlockingQueue<Message> messageToSend = new LinkedBlockingQueue<>();
        ServerListener serverListener = new ServerListener(PORT, messageToSend);
        ServerSender serverSender = new ServerSender(serverListener.getSelector(), messageToSend);
        //Démarrage des Threads
        serverSender.start();
        serverListener.start();
        System.out.println("Server is online");
    }
}
