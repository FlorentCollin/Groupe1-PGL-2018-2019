package com.gl1.slay.server;

import communication.Messages.Message;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;
import server.ServerInfo;
import server.ServerListener;
import server.ServerSender;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import static gui.utils.Constants.PORT;

/**
 * Classe qui démarre les différents threads liés au serveur
 */
public class ServerLauncher {

    static ServerListener serverListener;
    static ServerSender serverSender;

    public static void main(String[] args) throws IOException {
        //Configuration de tinylog car Libgdx n'est pas chargé par le server et dong l'utilisation
        //de Gdx.app.log est impossible sans lancer une fausse application libgdx.
        Configurator.defaultConfig()
                .writer(new FileWriter(String.format("settings%sserver_log.txt", File.separator), false, true))
                .addWriter(new ConsoleWriter())
                .formatPattern("{date} : {class_name} \n {{level}:|min-size=8} {message}")
                .level(Level.INFO)
                .activate();
        Logger.info("Starting server...");
        Logger.info("Local address : " + Inet4Address.getLocalHost().getHostAddress());
        LinkedBlockingQueue<Message> messageToSend = new LinkedBlockingQueue<>();
        serverListener = new ServerListener(PORT, messageToSend);
        serverSender = new ServerSender(serverListener.getServerChannel(), serverListener.getSelector(), messageToSend);
        //Démarrage des Threads
        Logger.info("Starting threads...");
        serverSender.start();
        serverListener.start();
        Logger.info("SLAY - Server is online");
        commandLine();
        serverListener.getServerChannel().close();
        serverListener.getSelector().close();
        serverSender.stopRunning();
        Logger.info("Server is close");
    }

    private static void commandLine() {
        Scanner scan = new Scanner(System.in);
        boolean running = true;
        while(running) {
            String command = scan.nextLine();
            System.out.println("get line");
            switch(command) {
                case "/size":
                    Logger.info("Number of clients : " + ServerInfo.clients.size()); break;
                case "/games":
                    Logger.info("Number of rooms :" + serverListener.getRoomController().numberRooms()); break;
                case "/close":
                    Logger.info("Closing Server...");
                    running = false;
            }
        }
    }
}
