package communication;

import logic.board.Board;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Classe abstraite qui sert de base aux classes OfflineMessageListener et OnlineMessageListener
 * Cette classe permet d'exécuter les messages reçus soit par la room directement (partie hors-ligne)
 * ou par le serveur (partie en-ligne)
 */
public abstract class MessageListener extends Thread {
    protected volatile Board board;
    //Variable permettant de stopper le thread quand il n'est plus nécessaire de le faire tourner
    protected AtomicBoolean running = new AtomicBoolean(false);
    protected LinkedBlockingQueue<Message> messagesFrom; //File des messages en attente

    public MessageListener( LinkedBlockingQueue<Message> messagesFrom) {
        this.messagesFrom = messagesFrom;
    }

    protected MessageListener() {}

    /**
     * Permet de stopper le thread
     */
    public void stopRunning() {
        running.set(false);
    }

    /**
     * Méthode qui permet d'exécuter un message
     * @param message Le message à exécuter
     */
    protected void executeMessage(Message message) {
        if(message instanceof InitMessage) { //Initialisation du board
            board = ((InitMessage) message).getBoard();
        } else if(message instanceof UpdateMessage) { //Update du board
            UpdateMessage updateMessage = (UpdateMessage) message;
            board.updateBoard(updateMessage.getDistricts(), updateMessage.getPlayers(), updateMessage.getActivePlayer());
            //Si le message possède un x et un y c'est qu'il faut update aussi la variable selectedCell du board
            //Sinon c'est qu'il faut la mettre à null
            if(updateMessage.getX() == null && updateMessage.getY() == null) {
                board.setSelectedCell(null);
            } else {
                board.setSelectedCell(board.getCell(updateMessage.getX(), updateMessage.getY()));
            }
        }
    }

    public Board getBoard() {
        return board;
    }

}
