package server;

import com.google.gson.Gson;
import communication.Messages.Message;
import communication.Messages.NetworkMessage;
import gui.utils.GsonInit;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread qui se charge d'envoyer les messages du serveur/room aux différents clients
 */
public class ServerSender extends Thread {
    private LinkedBlockingQueue<Message> messageToSend;
    private Gson gson;

    public ServerSender(LinkedBlockingQueue<Message> messageToSend) {
        //Lien vers la pile des messages à envoyer, c'est sur cette pile que les rooms doivent envoyer leurs messages
        this.messageToSend = messageToSend;
        gson = new Gson();
    }

    @Override
    public void run() {
        while (true) {
            try {
                //Récupération du message dans la file d'attente
                Message message = messageToSend.take(); //Remarque cette méthode est bloquante
                //Si le message est un message qui peut être envoyé pour à des clients
                if (message instanceof NetworkMessage) {
                    NetworkMessage networkMessage = (NetworkMessage) message;
                    for (Client client : networkMessage.getClients()) { //Envoie du message à tous les clients
                        SocketChannel clientChannel = client.getSocketChannel();
                        if (clientChannel.isConnected()) {

                            /* Écriture du message dans le buffer du client
                             * Ici on écrit le nom de la classe du message en plus du message sérialisé
                             * Pour permettre au client de retrouver le type du message */
                            clientChannel.write(ByteBuffer.wrap((message.getClass().getSimpleName() + gson.toJson(message) + "+").getBytes()));
                        }
                    }
                }
            } catch (InterruptedException | IOException e) {
                e.printStackTrace(); //TODO
            }
        }
    }
}
