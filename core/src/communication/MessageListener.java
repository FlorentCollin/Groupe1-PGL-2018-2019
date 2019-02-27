package communication;

import logic.board.Board;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MessageListener extends Thread {
    protected volatile Board board;
    protected AtomicBoolean running = new AtomicBoolean(false);
    protected LinkedBlockingQueue<Message> messagesFrom;

    public MessageListener( LinkedBlockingQueue<Message> messagesFrom) {
        this.messagesFrom = messagesFrom;
    }

    public void stopRunning() {
        running.set(false);
    }
    protected void executeMessage(Message message) {
        if(message instanceof InitMessage) {
            board = ((InitMessage) message).getBoard();
        } else if(message instanceof UpdateMessage) {
            UpdateMessage updateMessage = (UpdateMessage) message;
            if(updateMessage.getX() == null && updateMessage.getY() == null) {
                board.setSelectedCell(null);
            } else {
                board.setSelectedCell(board.getCell(updateMessage.getX(), updateMessage.getY()));
            }
            board.updateBoard(updateMessage.getDistricts(), updateMessage.getPlayers(), updateMessage.getActivePlayer());
        }
    }

    public Board getBoard() {
        return board;
    }

}
