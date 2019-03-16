package com.gl1.slay.server;

import communication.Messages.Message;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import org.pmw.tinylog.writers.ConsoleWriter;
import org.pmw.tinylog.writers.FileWriter;
import server.ServerListener;
import server.ServerSender;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import static gui.utils.Constants.PORT;

/**
 * Classe qui démarre les différents threads liés au serveur
 */
public class ServerLauncher {

    public static void main(String[] args) throws IOException {
        //Configuration de tinylog car Libgdx n'est pas chargé par le server et dong l'utilisation
        //de Gdx.app.log est impossible sans lancer une fausse application libgdx.
        Configurator.defaultConfig()
                .writer(new FileWriter(String.format("settings%slog.txt", File.separator), false, true))
                .addWriter(new ConsoleWriter())
                .formatPattern("{date} : {class_name} \n {{level}:|min-size=8} {message}")
                .level(Level.INFO)
                .activate();
        Logger.info("Starting server...");
        LinkedBlockingQueue<Message> messageToSend = new LinkedBlockingQueue<>();
        ServerListener serverListener = new ServerListener(PORT, messageToSend);
        ServerSender serverSender = new ServerSender(serverListener.getSelector(), messageToSend);
        //Démarrage des Threads
        Logger.info("Starting threads...");
        serverSender.start();
        serverListener.start();
        Logger.info("SLAY - Server is online");
    }
}
