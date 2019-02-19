package gui.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class InitSettings {
    private static Gson gson = new Gson();

    public static <T> T init(String fileName, Class<T> returnType) {
        FileHandle userSettingsFile = Gdx.files.local(fileName);
        if(userSettingsFile.exists()) {
            return gson.fromJson(userSettingsFile.readString(), returnType);
        } else {
            try {
                return returnType.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public static <T> void dispose(String fileName, T classToSerialize) {
        FileHandle fileHandle = Gdx.files.local(fileName);
        fileHandle.writeString(gson.toJson(classToSerialize), false);
    }
}
