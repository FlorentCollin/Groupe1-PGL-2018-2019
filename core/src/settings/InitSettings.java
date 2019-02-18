package settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;

public class InitSettings {
    private static Gson gson;

    private static <T> T init(String fileName, Class<T> returnType) throws IllegalAccessException, InstantiationException {
        gson = new Gson();
        FileHandle userSettingsFile = Gdx.files.internal(fileName);
        if(userSettingsFile.exists()) {
            return gson.fromJson(fileName, returnType);
        } else {
            return returnType.newInstance();
        }
    }
}
