package gui.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe qui permet d'initialiser et de sauvegarder les paramètres de l'utilisateur
 * tel que les raccourcis ou les paramètres video/audio
 */
public class InitSettings {
    private static Gson gson = new Gson();

    /**
     * Méthode qui charge un fichier de paramètres et renvoie un objet de la classe associé
     * @param fileName le nom fichier de paramètres
     * @param returnType la classe qui va servir de support aux paramètres
     * @param <T> Le Type de paramètres
     * @return Un objet de la classe de paramètres returnType
     */
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

    /**
     * Enregistre les parmètres dans un fichier
     * @param fileName le fichier dans lequel sauvegarder les paramètres
     * @param parametersToSerialize les paramètres à sérializer
     * @param <T> le type de paramètres à sérializer
     */
    public static <T> void dispose(String fileName, T parametersToSerialize) {
        FileHandle fileHandle = Gdx.files.local(fileName);
        fileHandle.writeString(gson.toJson(parametersToSerialize), false);
    }
}
