package gui.settings;

import com.badlogic.gdx.Gdx;

/**
 * Classe qui gère les paramètres utilisateurs. Si l'utilisateur lance lance l'application pour la première fois
 * des paramètres par défaut lui son attribués
 */
public class UserSettings {

    private boolean fullScreen;
    private int musicLevel;
    private int soundLevel;

    public UserSettings() {
        setFullScreen(false);
        setMusicLevel(100);
        setSoundLevel(100);
    }

    /**
     * Initialisation des paramètres dans libgdx
     */
    public void init() {
        setMusicLevel(musicLevel);
        setSoundLevel(soundLevel);
        setFullScreen(fullScreen);
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
        if(fullScreen) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            Gdx.graphics.setWindowedMode(1280, 720);
        }
    }

    public void setMusicLevel(int musicLevel) {
        this.musicLevel = musicLevel;
    }

    public void setSoundLevel(int soundLevel) {
        this.soundLevel = soundLevel;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }
}
