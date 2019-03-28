package ac.umons.slay.g01.communication;

import java.util.concurrent.LinkedBlockingQueue;

import ac.umons.slay.g01.communication.Messages.Message;

/**
 * Classe qui permet l'envoit de message de l'interface graphique à la room durant une partie hors-ligne
 */
public class OfflineMessageSender implements MessageSender {

    //Lien vers la file des messages de la room
    private final LinkedBlockingQueue<Message> messagesTo;

    public OfflineMessageSender(LinkedBlockingQueue<Message> messagesTo) {
        this.messagesTo = messagesTo;
    }

    @Override
    public void send(Message message) {
        try {
            messagesTo.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
