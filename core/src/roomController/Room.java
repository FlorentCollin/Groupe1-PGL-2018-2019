package roomController;

import communication.*;
import gui.utils.Map;
import logic.board.Board;
import logic.board.cell.Cell;
import server.Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread représentant une partie que ce soit en ligne ou hors-ligne.
 */
public class Room extends Thread {

    private LinkedBlockingQueue<Message> messagesFrom;
    private LinkedBlockingQueue<Message> messagesToSend;
    private ArrayList<Client> clients = new ArrayList<>();
    private Board board;

    private AtomicBoolean running = new AtomicBoolean(false);

    public LinkedBlockingQueue<Message> getMessagesFrom() {
        return messagesFrom;
    }

    public Room(String worldName, boolean naturalDisasters, LinkedBlockingQueue<Message> messagesFrom) {
        Map map = new Map();
        board = map.load(worldName, true, false, naturalDisasters);
        this.messagesFrom = messagesFrom;
    }

    public Room(String worldName, boolean naturalDisasters, LinkedBlockingQueue<Message> messagesFrom, LinkedBlockingQueue<Message> messagesToSend) {
        this(worldName, naturalDisasters, messagesFrom);
        this.messagesToSend = messagesToSend;
    }

    public void stopRunning() {
        running.set(false);
    }

    @Override
    public void run() {
        running.set(true);
        while(running.get()) {
            try {
                //Récupération du message du client
                Message message = messagesFrom.take();
                executeMessage(message);
                if(messagesToSend != null) {
                    //Si le board à changé alors il faut notifier les clients des changements.
                    if(board.hasChanged()) { //TODO NEED REFACTORING when offline we don't need to send message
                        UpdateMessage updateMessage;
                        if(board.getSelectedCell() != null) { //Création d'un UpdateMessage avec selectedCell
                            Cell selectedCell = board.getSelectedCell();
                            updateMessage = new UpdateMessage(board.getDistricts(),board.getPlayers(),
                                    board.getActivePlayerNumber(), selectedCell.getX(), selectedCell.getY());
                        } else { //Création d'un UpdateMessage sans selectedCell
                            updateMessage = new UpdateMessage(board.getDistricts(),
                                    board.getPlayers(), board.getActivePlayerNumber());
                        }
                        updateMessage.setClients(clients); //Ajout des clients au message
                        messagesToSend.put(updateMessage); //Envoie du message
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode qui va exécuter un message d'un client
     * @param message Le message à exécuter
     */
    private void executeMessage(Message message) {
        if(message instanceof PlayMessage) {
            PlayMessage playMessage = (PlayMessage) message;
            Cell cell = board.getCell(playMessage.getX(), playMessage.getY());
            board.play(cell);
        } else if(message instanceof ShopMessage) {
            board.setShopItem(((ShopMessage) message).getItem());
        } else if(message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            if(textMessage.getMessage().equals("nextPlayer")) {
                board.nextPlayer();
            }
        }
    }

    /**
     * Ajout d'un client et notification à ce client de l'état actuel du Board
     * @param client Le client à ajouter
     */
    public void addClient(Client client) {
        clients.add(client);
        try {
            InitMessage initMessage = new InitMessage(board);
            initMessage.setClients(Arrays.asList(client));
            if(messagesToSend != null) {
                messagesToSend.put(initMessage);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Board getBoard() {
        return board;
    }
}