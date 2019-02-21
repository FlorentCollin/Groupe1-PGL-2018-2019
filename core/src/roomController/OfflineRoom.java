package roomController;

import communication.Message;
import gui.utils.Map;
import logic.board.Board;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class OfflineRoom extends Thread {

    private LinkedBlockingQueue<Message> messagesFrom;
    private LinkedBlockingQueue<Message> messagesToSend;
    private Board board;

    public LinkedBlockingQueue<Message> getMessagesFrom() {
        return messagesFrom;
    }

    public OfflineRoom(String worldName,LinkedBlockingQueue<Message> messagesFrom, LinkedBlockingQueue<Message> messagesToSend) {
        Map map = new Map();
        board = map.load(worldName, false);
        this.messagesFrom = messagesFrom;
        this.messagesToSend = messagesToSend;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Message message = messagesFrom.take();
                System.out.println("GOT MESSAGE :)");
                invokeMethod(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void invokeMethod(Message message) {
        Method method;
        if(message.object.equals("board")) {
            List<?> parameters = message.parameters;
            List<Class<?>> parametersClass = message.parametersClass;
            try {
                switch(message.parametersClass.size()) {
                    case 0: method = board.getClass().getMethod(message.methodName);
                            method.invoke(board);
                            break;
                    case 1: method = board.getClass().getMethod(message.methodName, parametersClass.get(0));
                            method.invoke(board, parameters.get(0));
                            break;
                    case 2: method = board.getClass().getMethod(message.methodName, parametersClass.get(0), parametersClass.get(1));
                            method.invoke(board, parameters.get(0), parameters.get(1));
                            break;
                    case 3: method = board.getClass().getMethod(message.methodName, parametersClass.get(0), parametersClass.get(1), parametersClass.get(2));
                            method.invoke(board, parameters.get(0), parameters.get(1), parameters.get(2));
                            break;
                }

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        System.out.println(board.getSelectedCell());
    }

}
