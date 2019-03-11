package communication;

import communication.Messages.GameUpdateMessage;
import communication.Messages.InitMessage;
import communication.Messages.Message;
import communication.Messages.RoomUpdateMessage;
import logic.board.Board;
import logic.player.Player;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Classe abstraite qui sert de base aux classes OfflineMessageListener et OnlineMessageListener
 * Cette classe permet d'exécuter les messages reçus soit par la room directement (partie hors-ligne)
 * ou par le serveur (partie en-ligne)
 */
public abstract class MessageListener extends Thread {
    protected Board board;
    protected String mapName;
    protected int playerNumber;
    protected ArrayList<Player> players = new ArrayList<>();
    protected ArrayList<Boolean> playersReady = new ArrayList<>();
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
            playerNumber = ((InitMessage) message).getPlayerNumber();
        } else if(message instanceof GameUpdateMessage) { //Update du board
            GameUpdateMessage updateMessage = (GameUpdateMessage) message;
            board.updateBoard(updateMessage.getDistricts(), updateMessage.getPlayers(), updateMessage.getActivePlayer());
            //Si le message possède un x et un y c'est qu'il faut update aussi la variable selectedCell du board
            //Sinon c'est qu'il faut la mettre à null
            if(updateMessage.getX() == null && updateMessage.getY() == null) {
                board.setSelectedCell(null);
            } else {
                System.out.println("i'm settings selectedCell");
                board.setSelectedCell(board.getCell(updateMessage.getX(), updateMessage.getY()));
                System.out.println(board.getSelectedCell());
            }
        } else if (message instanceof RoomUpdateMessage) {
            RoomUpdateMessage roomUpdateMessage = (RoomUpdateMessage) message;
            players = roomUpdateMessage.getPlayers();
            playersReady = roomUpdateMessage.getPlayersReady();
            mapName = roomUpdateMessage.getMapName();
        }
    }

    public Board getBoard() {
        return board;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public ArrayList<Boolean> getPlayersReady() {
        return playersReady;
    }

    public String getMapName() {
        return mapName;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }
}
