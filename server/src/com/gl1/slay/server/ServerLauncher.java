package src.com.gl1.slay.server;

import static ac.umons.slay.g01.gui.utils.Constants.PORT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.System.Logger;
import java.net.Inet4Address;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.writers.ConsoleWriter;

import ac.umons.slay.g01.communication.Messages.Message;
import ac.umons.slay.g01.logic.item.level.Level;
import ac.umons.slay.g01.server.ServerInfo;
import ac.umons.slay.g01.server.ServerListener;
import ac.umons.slay.g01.server.ServerSender;

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
