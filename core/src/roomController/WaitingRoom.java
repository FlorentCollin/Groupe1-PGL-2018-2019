package roomController;

import communication.Messages.CreateRoomMessage;
import communication.Messages.Message;
import communication.Messages.RoomUpdateMessage;
import communication.Messages.TextMessage;
import gui.utils.Map;
import logic.board.Board;
import logic.player.ai.strategy.RandomStrategy;
import server.Client;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Thread qui représente une salle d'attente pour les joueurs avant que la partie ne soit réellement lancée.
 */
public class WaitingRoom extends Room {

    private final Board board;
    private final String mapName;
    private ArrayList<Boolean> clientsReady = new ArrayList<>();

    public WaitingRoom(CreateRoomMessage message, LinkedBlockingQueue<Message> messagesFrom, LinkedBlockingQueue<Message> messageToSend) {
        mapName = message.getWorldName();
        Map map = new Map(mapName);
        board = map.loadBoard(false, message.isNaturalDisastersOn(), null);
        for (int i = 0; i < board.getPlayers().size(); i++) {
            clientsReady.add(i, false);
        }
        for (int i = 0; i < message.getAiStrats().size(); i++) { //TODO Faire en sorte que ce soit vraiment la stratégie sélectionné
            board.changeToAI(board.getPlayers().size() - i - 1, new RandomStrategy());
            clientsReady.set(board.getPlayers().size() - i - 1, true);
        }
        this.messagesFrom = messagesFrom;
        this.messagesToSend = messageToSend;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("WaitingRoom");
        running.set(true);
        while (running.get()) {
            try {
                //Récupération du message du client
                Message message = messagesFrom.take();
                executeMessage(message);
                System.out.print("");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                if (((TextMessage) message).getMessage().equals("ready")) {
                    int index = clients.indexOf(message.getClient());
                    if (clientsReady.get(index))
                        clientsReady.set(index, false);
                    else
                        clientsReady.set(index, true);
                    RoomUpdateMessage roomUpdateMessage = new RoomUpdateMessage(board.getPlayers(), clientsReady, mapName);
                    roomUpdateMessage.setClients(clients);
                    messagesToSend.put(roomUpdateMessage);
                } else if(((TextMessage) message).getMessage().equals("close")) {
                    running.set(false);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void addClient(Client client){
        super.addClient(client);
        board.getPlayers().get(clients.size()-1).setName(client.getUsername());
        try {
            RoomUpdateMessage message = new RoomUpdateMessage(board.getPlayers(), clientsReady, mapName);
            message.setClients(clients);
            messagesToSend.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isReady() {
        for (boolean ready : clientsReady) {
            if (!ready)
                return false;
        }
        return true;
    }

    public Board getBoard() {
        return board;
    }

    public String getMapName() {
        return mapName;
    }
}





