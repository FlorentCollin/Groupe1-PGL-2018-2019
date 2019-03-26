package gui.settings;

import com.badlogic.gdx.Gdx;

/**
 * Classe qui gère les paramètres utilisateurs. Si l'utilisateur lance lance l'application pour la première fois
 * des paramètres par défaut lui son attribués
 */
public class UserSettings {

    private boolean fullScreen;
    private String username;
    private String language;
    private int numberOfPlayers;

    public UserSettings() {
        setFullScreen(false);
        setUsername("Player");
        setLanguage("en");
        setNumberOfPlayer(1);
    }


    /**
     * Initialisation des paramètres dans libgdx
     */
    public void init() {
        setNumberOfPlayer(numberOfPlayers);
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

    public String getUsername() {
        return username;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setNumberOfPlayer(int numberOfPlayer) {
        this.numberOfPlayers = numberOfPlayer;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
