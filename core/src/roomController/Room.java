package roomController;

import communication.*;
import gui.utils.Map;
import logic.board.Board;
import logic.board.cell.Cell;
import server.Client;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Room extends Thread {

    private LinkedBlockingQueue<Message> messagesFrom;
    private LinkedBlockingQueue<Message> messagesToSend;
    private ArrayList<Client> clients = new ArrayList<>();
    private Board board;

    private AtomicBoolean running = new AtomicBoolean(false);

    public LinkedBlockingQueue<Message> getMessagesFrom() {
        return messagesFrom;
    }

    public Room(String worldName,LinkedBlockingQueue<Message> messagesFrom, LinkedBlockingQueue<Message> messagesToSend) {
        Map map = new Map();
        board = map.load(worldName, true, false);
        this.messagesFrom = messagesFrom;
        this.messagesToSend = messagesToSend;
        try {
            messagesToSend.put(new InitMessage(board));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopRunning() {
        running.set(false);
    }

    @Override
    public void run() {
        running.set(true);
        while(running.get()) {
            try {
                Message message = messagesFrom.take();
                executeMessage(message);
                if(board.hasChanged()) {
                    UpdateMessage updateMessage;
                    if(board.getSelectedCell() != null) {
                        Cell selectedCell = board.getSelectedCell();
                        updateMessage = new UpdateMessage(board.getDistricts(),board.getPlayers(),
                                board.getActivePlayerNumber(), selectedCell.getX(), selectedCell.getY());
                    } else {
                        updateMessage = new UpdateMessage(board.getDistricts(),
                                board.getPlayers(), board.getActivePlayerNumber());
                    }
                    updateMessage.setClients(clients);
                    messagesToSend.put(updateMessage);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeMessage(Message message) {
        if(message instanceof PlayMessage) {
            PlayMessage playMessage = (PlayMessage) message;
            Cell cell = board.getCell(playMessage.getX(), playMessage.getY());
            board.play(cell);
        }  else if(message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            if(textMessage.getMessage().equals("nextPlayer")) {
                board.nextPlayer();
            }
        }
    }

    public boolean addClient(Client client) {
        return clients.add(client);
    }
}