package gui.utils;

import com.badlogic.gdx.Gdx;

/**
 * Classe contenant toutes les constantes utiles à l'interface graphique
 */
public class Constants {
    /** Résolution maximum **/
    public static final int MAX_RES = 1920;
    public static final int PAD = 20;

    public static final int N_TILES = 3; //Nombre de tiles présent dans le tileset hex.
    // Ce nombre est utilisé pour sélectionner et désélectionner des cellules

    public static final String USER_SETTINGS_FILE = "settings/userSettings.json";

    public static float getRatio(float width) {
        return width / MAX_RES;
    }
}
