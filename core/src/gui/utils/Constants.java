package gui.utils;

import com.badlogic.gdx.Gdx;

/**
 * Classe contenant toutes les constantes utiles à l'interface graphique
 */
public class Constants {
    /** Résolution maximum **/
    public static int MAX_RES = 1920;

    public static float getRatio(float width) {
        return width / MAX_RES;
    }
}
