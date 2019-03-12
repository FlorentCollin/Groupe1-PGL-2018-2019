package roomController;

import communication.Messages.*;
import gui.utils.Map;
import logic.board.Board;
import logic.board.cell.Cell;
import logic.item.Item;
import logic.player.ai.strategy.*;
import server.Client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread représentant une partie que ce soit en ligne ou hors-ligne.
 */
public class GameRoom extends Room {

    private final Board board;
    private HashMap<Client, Integer> playersNumber = new HashMap<>();

    public GameRoom(String worldName, boolean naturalDisasters, ArrayList<String> aiStrats, ArrayList<String> playersName,
                    LinkedBlockingQueue<Message> messagesFrom) {

        Map map = new Map(worldName);
        board = map.loadBoard(naturalDisasters, playersName);
        this.messagesFrom = messagesFrom;
        for (int i = 0; i < aiStrats.size(); i++) {
            System.out.println(aiStrats.get(i));
            Strategy strat = null;
            switch (aiStrats.get(i)) {
                case "Random":
                    strat = new RandomStrategy();
                    break;
                case "Easy":
                    strat = new AttackStrategy();
                    break;
                case "Medium":
                    strat = new DefenseStrategy();
                    break;
                case "Hard":
                    strat = new AdaptativeStrategy();
                    break;
            }
            board.changeToAI(board.getPlayers().size() - i - 1, strat);
        }
    }

    public GameRoom(Board board, LinkedBlockingQueue<Message> messagesFrom, LinkedBlockingQueue<Message> messagesToSend) {
        this.board = board;
        this.messagesFrom = messagesFrom;
        this.messagesToSend = messagesToSend;
    }


    public void stopRunning() {
        running.set(false);
    }

    @Override
    public void run() {
        Thread.currentThread().setName("GameRoom");
        running.set(true);
        while(running.get()) {
            try {
                //Récupération du message du client
                Message message = messagesFrom.take();
                synchronized (board) {
                    executeMessage(message);
                    System.out.println("Game Room - Message Executed : " + message.getClass().getSimpleName());
                    if (messagesToSend != null) { //On vérifie qu'il faut envoyer des messages d'update
                        //Si le board à changé alors il faut notifier les clients des changements.
                        if (board.hasChanged()) {
                            GameUpdateMessage updateMessage;
                            System.out.println(board.getShop().getSelectedItem());
                            if (board.getSelectedCell() != null) { //Création d'un GameUpdateMessage avec selectedCell
                                Cell selectedCell = board.getSelectedCell();
                                updateMessage = new GameUpdateMessage(board.getDistricts(), board.getShop().getSelectedItem(), board.getPlayers(),
                                        board.getActivePlayerNumber(), selectedCell.getX(), selectedCell.getY());
                            } else { //Création d'un GameUpdateMessage sans selectedCell
                                updateMessage = new GameUpdateMessage(board.getDistricts(), board.getShop().getSelectedItem(),
                                        board.getPlayers(), board.getActivePlayerNumber());
                            }
                            updateMessage.setClients(clients); //Ajout des clients au message
                            System.out.println("Game Room - Sending Update Message");
                            messagesToSend.put(updateMessage); //Envoie du message
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Closing Room");
    }

    /**
     * Méthode qui va exécuter un message d'un client
     * @param message Le message à exécuter
     */
    private void executeMessage(Message message) {
        if(message instanceof PlayMessage && (message.getClient() == null || playersNumber.get(message.getClient()) == board.getActivePlayerNumber())) {
            PlayMessage playMessage = (PlayMessage) message;
            Cell cell = board.getCell(playMessage.getX(), playMessage.getY());
            board.play(cell);
        } else if(message instanceof ShopMessage && (message.getClient() == null || playersNumber.get(message.getClient()) == board.getActivePlayerNumber() || message.getClient() == null)) {
            Item item = ((ShopMessage) message).getItem();
            //On refixe le type qui n'a pas survécu au transfert
            //Ce qui permet de renvoyer l'item au client par la suite
            item.setType(item.getClass().getName());
            board.setShopItem(item);
        } else if(message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            if(textMessage.getMessage().equals("nextPlayer")) {
                board.nextPlayer();
            }
            else if(textMessage.getMessage().equals("close")) {
                stopRunning();
            }
        }
    }

    /**
     * Ajout d'un client et notification à ce client de l'état actuel du Board
     * @param client Le client à ajouter
     */
    public void addClient(Client client) {
        clients.add(client);
        playersNumber.put(client, clients.size() - 1);
        try {
            InitMessage initMessage = new InitMessage(board, clients.size() - 1);
            initMessage.setClients(Arrays.asList(client));
            if(messagesToSend != null) {
                messagesToSend.put(initMessage);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean remove(Client client) {
        board.changeToAI(clients.indexOf(client), new RandomStrategy());
        if(board.getActivePlayerNumber() == clients.indexOf(client)) {
            board.nextPlayer();
        }
        return super.remove(client);
    }

    public Board getBoard() {
        return board;
    }
}