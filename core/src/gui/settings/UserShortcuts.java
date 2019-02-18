package gui.settings;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.HashMap;

/**
 * Classe qui gère les paramètres utilisateurs. Si l'utilisateur lance lance l'application pour la première fois
 * des paramètres par défaut lui son attribués
 */
public class UserShortcuts {

    private HashMap<String, Integer[]> shortcuts;

    public UserShortcuts() {
        shortcuts = new HashMap<>();
        shortcuts.put("MoveCamUp", new Integer[]{Input.Keys.W, Input.Keys.Z});
        shortcuts.put("MoveCamDown", new Integer[]{Input.Keys.S, null});
        shortcuts.put("MoveCamLeft", new Integer[]{Input.Keys.Q, Input.Keys.A});
        shortcuts.put("MoveCamRight", new Integer[]{Input.Keys.D, null});
        shortcuts.put("Menu", new Integer[]{Input.Keys.ESCAPE, null});
        shortcuts.put("EndTurn", new Integer[]{Input.Keys.ENTER, null});
    }


    public HashMap<String, Integer[]> getShortcuts() {
        return shortcuts;
    }

    public boolean changeShortcut(String key, Integer[] values) {
        if (shortcuts.get(values) != null) {
            return false; //Signifie que le raccourci est déjà utilisé

        } else {
            shortcuts.replace(key, values);
            return true;
        }
    }
}