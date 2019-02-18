package settings;

/**
 * Classe qui gère les paramètres utilisateurs. Si l'utilisateur lance lance l'application pour la première fois
 * des paramètres par défaut lui son attribués
 */
public class UserSettings {

    private boolean fullScreen;
    private int width;
    private int height;
    private int musicLevel;
    private int soundLevel;

    public UserSettings() {
        fullScreen = false;
        width = 1280;
        height = 720;
        musicLevel = 100;
        soundLevel = 100;
    }
}
