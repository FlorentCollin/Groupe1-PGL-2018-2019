package communication;

import communication.Messages.*;
import logic.board.Board;
import logic.player.Player;

import java.util.ArrayList;
import java.util.UUID;
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
    protected String roomName;
    protected int playerNumber;
    protected ArrayList<String> roomNames;
    protected ArrayList<UUID> ids;
    protected ArrayList<Integer> nPlayer, nPlayerIn;
    protected AtomicBoolean needRefresh =  new AtomicBoolean(false);
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
            board.updateBoard(board.getDistricts(), board.getShop().getSelectedItem(), board.getPlayers(), board.getActivePlayerNumber());
            playerNumber = ((InitMessage) message).getPlayerNumber();
        } else if(message instanceof GameUpdateMessage) { //Update du board
            GameUpdateMessage updateMessage = (GameUpdateMessage) message;
            board.updateBoard(updateMessage.getDistricts(), updateMessage.getShopItem(), updateMessage.getPlayers(), updateMessage.getActivePlayer());
            //Si le message possède un x et un y c'est qu'il faut update aussi la variable selectedCell du board
            //Sinon c'est qu'il faut la mettre à null
            if(updateMessage.getX() == null && updateMessage.getY() == null) {
                board.setSelectedCell(null);
            } else {
                board.setSelectedCell(board.getCell(updateMessage.getX(), updateMessage.getY()));
            }
        } else if (message instanceof RoomUpdateMessage) {
            RoomUpdateMessage roomUpdateMessage = (RoomUpdateMessage) message;
            players = roomUpdateMessage.getPlayers();
            playersReady = roomUpdateMessage.getPlayersReady();
            mapName = roomUpdateMessage.getMapName();
            roomName = roomUpdateMessage.getRoomName();
        } else if (message instanceof ListRoomsMessage) {
            ListRoomsMessage listRoomsMessage = (ListRoomsMessage) message;
            roomNames = listRoomsMessage.getRoomsName();
            nPlayer = listRoomsMessage.getnPlayer();
            nPlayerIn = listRoomsMessage.getnPlayerIn();
            ids = listRoomsMessage.getIds();
            needRefresh.set(true);
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

    public String getRoomName() {
        return  roomName;
    }

    public ArrayList<String> getRoomNames() {
        return roomNames;
    }

    public ArrayList<Integer> getnPlayer() {
        return nPlayer;
    }

    public ArrayList<Integer> getnPlayerIn() {
        return nPlayerIn;
    }

    public ArrayList<UUID> getIds() {
        return ids;
    }

    public boolean needRefresh() {
        return needRefresh.getAndSet(false);
    }
}
