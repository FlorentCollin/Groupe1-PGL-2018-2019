package ac.umons.slay.g01.roomController;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import ac.umons.slay.g01.communication.Messages.CreateRoomMessage;
import ac.umons.slay.g01.communication.Messages.Message;
import ac.umons.slay.g01.communication.Messages.RoomUpdateMessage;
import ac.umons.slay.g01.communication.Messages.TextMessage;
import ac.umons.slay.g01.gui.utils.Map;
import ac.umons.slay.g01.logic.board.Board;
import ac.umons.slay.g01.logic.player.Player;
import ac.umons.slay.g01.logic.player.ai.AI;
import ac.umons.slay.g01.server.Client;

/**
 * Thread qui représente une salle d'attente pour les joueurs avant que la partie ne soit réellement lancée.
 */
public class WaitingRoom extends Room {

    private final Board board;
    private final String mapName;
    private final String roomName;
    private ArrayList<Boolean> clientsReady = new ArrayList<>();

    public WaitingRoom(CreateRoomMessage message, LinkedBlockingQueue<Message> messagesFrom, LinkedBlockingQueue<Message> messageToSend) {
        mapName = message.getWorldName();
        roomName = message.getRoomName();
        Map map = new Map(mapName);
        board = map.loadBoard(false, null);
        changeToAI(board, message.getAiStrats());
        //Création d'une liste qui indique si le joueur est prêt à lancer la partie ou non
        for (int i = 0; i < board.getPlayers().size(); i++) {
            //Les AI sont toujours prêtes et les joueurs ne le sont pas au début de la Waiting Room
            clientsReady.add(i, board.getPlayers().get(i) instanceof AI);
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode qui va exécuter le message en fonction de sa classe
     * @param message le message a exécuter
     */
    private void executeMessage(Message message) {
        if (message instanceof TextMessage) {
            if (((TextMessage) message).getMessage().equals("ready")) {
                int index = 0;
                for(int i = 0; i < clients.indexOf(message.getClient()); i++) {
                    index += clients.get(i).getNumberOfPlayer();
                }
                for(int i = 0; i < message.getClient().getNumberOfPlayer(); i++) {
                    clientsReady.set(index + i, !clientsReady.get(index + i));
                }
                sendUpdateMessage();
            } else if (((TextMessage) message).getMessage().equals("close")) {
                running.set(false);
            }
        }

    }

    /**
     * Méthode qui permet d'envoyer un message d'update pour la waiting room aux différents clients
     * Le message envoyé est un message contenant le nom de chaque joueur ainsi que son état (ready / not ready)
     */
    private void sendUpdateMessage() {
        RoomUpdateMessage roomUpdateMessage = new RoomUpdateMessage(board.getPlayers(), clientsReady, mapName, roomName);
        roomUpdateMessage.setClients(clients);
        try {
            messagesToSend.put(roomUpdateMessage);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Méthode qui va ajouter un client à la WaitingRoom
     * @param client le client a ajouter
     */
    @Override
    public void addClient(Client client){
        super.addClient(client);
        if(client.getNumberOfPlayer() == 1) {
            board.getPlayers().get(sizeOfClients++).setName(client.getUsername());
        } else {
            for(int i = 0; i < client.getNumberOfPlayer(); i++) {
                board.getPlayers().get(sizeOfClients++).setName(client.getUsername() + "#" + (i + 1));
            }
        }
        sendUpdateMessage();
    }

    @Override
    public boolean remove(Client client) {
        int index = -1;
        for(int i = 0; i <= clients.indexOf(client); i++) {
            index += clients.get(i).getNumberOfPlayer();
        }
        if (index != -1) {
            for(int i = index; i < client.getNumberOfPlayer(); i++) {
                board.getPlayers().get(i).setName(null);
                clientsReady.set(i, false);
            }
        }
        sendUpdateMessage();
        return super.remove(client);
    }


    public boolean isReady() {
        return  !clientsReady.contains(false);
    }

    public Board getBoard() {
        return board;
    }

    public String getRoomName() {
        return roomName;
    }

    public int getMaxClients() {
        return clientsReady.size();
    }

    public int getNumberOfClients() {
        int i = sizeOfClients;
        //On ajoute les AI aux clients car elles sont aussi des joueurs
        for(Player player : board.getPlayers()) {
            if(player instanceof AI)
                i++;
        }
        return i;
    }

    public int waitingPlayer() {
        int i = 0;
        for (Client client : clients) {
            i += client.getNumberOfPlayer();
        }
        int numberOfAi = 0;
        for(Player player : board.getPlayers()) {
            if(player instanceof AI)
                numberOfAi += 1;
        }
        return board.getPlayers().size() - numberOfAi - i;
    }
}





