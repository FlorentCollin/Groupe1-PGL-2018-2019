package communication;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import gui.utils.Map;
import logic.board.Board;
import logic.board.cell.Cell;
import roomController.OfflineRoom;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class Message {

    public String object;
    public String methodName;
    public List<?> parameters;
    public List<Class<?>> parametersClass;

    public Message(String object, String methodName, List<?> parameters) {
        this.object = object;
        this.methodName = methodName;
        this.parameters = parameters;
        parametersClass = new ArrayList<>();
        for (int i = 0; i < parameters.size(); i++) {
            if(parameters.get(i).getClass().getSuperclass() == Cell.class) {
                parametersClass.add(parameters.get(i).getClass().getSuperclass());
            } else {
                parametersClass.add(parameters.get(i).getClass());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue<Message> messagesFrom = new LinkedBlockingQueue<>(), messagesTo = new LinkedBlockingQueue<>();
        OfflineRoom offlineRoom = new OfflineRoom("g1_World1", messagesFrom, messagesTo);
        Board board = new Map().load("g1_World1", false);
        Message message = new Message("board", "play", Arrays.asList((Cell)board.getCell(9, 8)));
        messagesFrom.put(message);
        System.out.println("send");
        offlineRoom.run();
    }
}
