package communication;


import com.google.gson.Gson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public abstract class Message {

    public static Message getMessage(String messageStr, Gson gson) {
        String className = messageStr.substring(0, messageStr.indexOf("{"));
        messageStr = messageStr.substring(messageStr.indexOf("{"));
        switch(className) {
            case "InitMessage":
                return gson.fromJson(messageStr, InitMessage.class);
            case "PlayMessage":
                return gson.fromJson(messageStr, PlayMessage.class);
            case "TextMessage":
                return gson.fromJson(messageStr, TextMessage.class);
            case "UpdateMessage":
                return gson.fromJson(messageStr, UpdateMessage.class);
            default: return null;
        }
    }

    public static String getStringFromBuffer(SocketChannel clientChannel) throws IOException {
        //Boucle qui lit les données envoyées par le client et place ces données dans un string
        String messageStr = "";
        int len;
        do {
            ByteBuffer buffer = ByteBuffer.allocate(100);
            buffer.clear();
            len = clientChannel.read(buffer);
            messageStr += new String(buffer.array(), StandardCharsets.UTF_8);
        } while (len == 100); //len == 100 indique que l'entièreté du message n'a pas encore été lu
        return messageStr;
    }
}
