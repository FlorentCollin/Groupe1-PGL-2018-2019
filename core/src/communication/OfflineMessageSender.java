package communication;

import java.util.concurrent.LinkedBlockingQueue;

public class OfflineMessageSender implements MessageSender {

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
