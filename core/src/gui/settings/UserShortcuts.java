package gui.settings;

import com.badlogic.gdx.Input;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe qui gère les raccourcis utilisateurs. Si l'utilisateur lance lance l'application pour la première fois
 * des paramètres par défaut lui son attribués
 */
public class UserShortcuts {

    private HashMap<String, Integer[]> shortcuts;
    private String[] shortcutsName = new String[] {"Move camera up", "Move camera down", "Move camera left", "Move camera right",
                                                    "Menu", "End turn"};

    public UserShortcuts() {
        shortcuts = new HashMap<>();
        //Raccourcis par défaut
        shortcuts.put(shortcutsName[0], new Integer[]{Input.Keys.W, Input.Keys.Z});
        shortcuts.put(shortcutsName[1], new Integer[]{Input.Keys.S, null});
        shortcuts.put(shortcutsName[2], new Integer[]{Input.Keys.Q, Input.Keys.A});
        shortcuts.put(shortcutsName[3], new Integer[]{Input.Keys.D, null});
        shortcuts.put(shortcutsName[4], new Integer[]{Input.Keys.ESCAPE, null});
        shortcuts.put(shortcutsName[5], new Integer[]{Input.Keys.ENTER, null});
    }


    public HashMap<String, Integer[]> getShortcuts() {
        return shortcuts;
    }

    public void changeShortcut(String key, Integer[] values) {
        shortcuts.replace(key, values);
    }

    public String[] getShortcutsName() {
        return shortcutsName;
    }

    public boolean isShortcut(String key, int keycode) {
        Integer[] values = shortcuts.get(key);
        return (values[0] != null && values[0] == keycode) || (values[1] != null && values[1] == keycode);
    }
}