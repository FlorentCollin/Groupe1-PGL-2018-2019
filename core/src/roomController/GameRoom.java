package roomController;

import communication.Messages.*;
import gui.utils.Map;
import logic.board.Board;
import logic.board.cell.Cell;
import logic.item.Item;
import logic.naturalDisasters.naturalDisasterscontroller.NaturalDisastersController;
import logic.player.ai.strategy.RandomStrategy;
import org.pmw.tinylog.Logger;
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
    private HashMap<Client, ArrayList<Integer>> playersNumber = new HashMap<>();

    /**
     * Constructeur d'une partie hors-ligne
     * @param worldName le nom du monde a charger
     * @param naturalDisasters Boolean : true = l'extension Natural Disasters est activée, false = désactivée
     * @param aiStrats Nom des stratégies des AI
     * @param playersName Le nom des différents joueurs
     * @param messagesFrom Référence vers la file des messages reçu par la GameRoom
     */
    public GameRoom(String worldName, boolean naturalDisasters, ArrayList<String> aiStrats, ArrayList<String> playersName,
                    LinkedBlockingQueue<Message> messagesFrom) {
        Map map = new Map(worldName);
        board = map.loadBoard(naturalDisasters, playersName);
        this.messagesFrom = messagesFrom;
        changeToAI(board, aiStrats);
    }

    /**
     * Constructeur d'une partie en ligne
     * @param board Le board représentant le monde
     * @param messagesFrom Référence vers la file des messages reçu par la GameRoom
     * @param messagesToSend Référence vers la file des messages à envoyer par le serveur
     */
    public GameRoom(Board board, LinkedBlockingQueue<Message> messagesFrom, LinkedBlockingQueue<Message> messagesToSend) {
        this.board = board;
        this.messagesFrom = messagesFrom;
        this.messagesToSend = messagesToSend;
    }

    /**
     * Méthode qui permet d'arrêter le thread
     */
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
//                    Logger.info(String.format("Message Executed : %s", message.getClass().getSimpleName()));
                    if (messagesToSend != null) { //On vérifie qu'il faut envoyer des messages d'update
                        //Si le board à changé alors il faut notifier les clients des changements.
                        sendUpdateMessage();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Logger.info("Closing Room");
    }

    /**
     * Méthode qui va exécuter un message d'un client
     * @param message Le message à exécuter
     */
    private void executeMessage(Message message) { //TODO REFACTOR
        if(message instanceof PlayMessage && (message.getClient() == null || isActivePlayer(message.getClient()))) {
            PlayMessage playMessage = (PlayMessage) message;
            Cell cell = board.getCell(playMessage.getX(), playMessage.getY());
            board.play(cell);
        } else if(message instanceof ShopMessage && (message.getClient() == null || isActivePlayer(message.getClient()))) {
            Item item = ((ShopMessage) message).getItem();
            //On refixe le type qui n'a pas survécu au transfert
            //Ce qui permet de renvoyer l'item au client par la suite
            item.setType(item.getClass().getName());
            board.setShopItem(item);
        } else if(message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            if(textMessage.getMessage().equals("nextPlayer") && (message.getClient() == null || isActivePlayer(message.getClient()))) {
                board.nextPlayer();
            }
            else if(textMessage.getMessage().equals("close")) {
                stopRunning();
            }
        } else if(message instanceof NaturalDisasterMessage) {
            executeNDMessage((NaturalDisasterMessage) message);
        }
    }

    /**
     * Méthode qui permet d'exécuter un naturalDisasterMessage sur le board
     * @param message le message à exécuter
     */
    private void executeNDMessage(NaturalDisasterMessage message) {
        NaturalDisastersController controller = board.getNaturalDisastersController();
        if (controller != null) {
            switch (message.getNaturalName()) {
                case "blizzard":
                    controller.getBlizzard().setProba(message.getPourcent());
                    break;
                case "drought":
                    controller.getDrought().setProba(message.getPourcent());
                    break;
                case "forestFire":
                    controller.getForestFire().setProba(message.getPourcent());
                    break;
                case "landErosion":
                    controller.getLandErosion().setProba(message.getPourcent());
                    break;
                case "tsunami":
                    controller.getTsunami().setProba(message.getPourcent());
                    break;
                case "volcanicEruption":
                    controller.getVolcanicEruption().setProba(message.getPourcent());
                    break;
            }
        }
    }

    private void sendUpdateMessage() {
        if (board.hasChanged()) {
            GameUpdateMessage updateMessage;
            if (board.getSelectedCell() != null) { //Création d'un GameUpdateMessage avec selectedCell
                Cell selectedCell = board.getSelectedCell();
                updateMessage = new GameUpdateMessage(board.getDistricts(), board.getShop().getSelectedItem(), board.getPlayers(),
                        board.getWinner(), board.getActivePlayerNumber(), selectedCell.getX(), selectedCell.getY());
            } else { //Création d'un GameUpdateMessage sans selectedCell
                updateMessage = new GameUpdateMessage(board.getDistricts(), board.getShop().getSelectedItem(),
                        board.getPlayers(), board.getWinner(), board.getActivePlayerNumber());
            }
            updateMessage.setClients(clients); //Ajout des clients au message
            try {
                messagesToSend.put(updateMessage); //Envoie du message
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(board.getWinner() != null) {
            stopRunning();
        }
    }

    /**
     * Ajout d'un client et notification à ce client de l'état actuel du Board
     * @param client Le client à ajouter
     */
    public void addClient(Client client) {
        clients.add(client);
        ArrayList<Integer> numbers;
        for(int i = 0; i < client.getNumberOfPlayer(); i++) {
            if(playersNumber.get(client) == null)
                numbers = new ArrayList<>();
            else
                numbers = playersNumber.get(client);
            numbers.add(sizeOfClients++);
            playersNumber.put(client, numbers);
        }
        try {
            InitMessage initMessage = new InitMessage(board, playersNumber.get(client));
            initMessage.setClients(Arrays.asList(client));
            if(messagesToSend != null) {
                //Envoie d'un message d'initialisation au client qui vient d'être ajouté
                //Ce message permet au client de synchroniser son board avec le board actuel du serveur
                messagesToSend.put(initMessage);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean remove(Client client) {
        //On change le joueur par une AI pour que les autres joueurs puissent continuer de jouer
        playersNumber.get(client).forEach((i) -> board.changeToAI(i, new RandomStrategy()));
        if(isActivePlayer(client)) {
            board.nextPlayer();
            sendUpdateMessage();
        }
        return super.remove(client);
    }

    private boolean isActivePlayer(Client client) {
        return playersNumber.get(client).indexOf(board.getActivePlayerNumber()) != -1;
    }

    public Board getBoard() {
        return board;
    }
}