package communication;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * TODO DELETE THIS CLASS IF NOT USED
 */
public class OfflineMessageListener extends MessageListener{

    public OfflineMessageListener(LinkedBlockingQueue<Message> messagesFrom) {
        super(messagesFrom);
    }

    @Override
    public void run() {
        running.set(true);
        while(running.get()) {
            try {
                Message message = messagesFrom.take();
                executeMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
