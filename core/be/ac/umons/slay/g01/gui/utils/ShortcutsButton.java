package ac.umons.slay.g01.gui.utils;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class ShortcutsButton extends TextButton {

    private String shortcutName;
    private int number;

    public ShortcutsButton(String shortcutName, int number, String text, TextButtonStyle style) {
        super(text, style);
        this.shortcutName = shortcutName;
        this.number = number;
    }

    public String getShortcutName() {
        return shortcutName;
    }

    public int getNumber() {
        return number;
    }
}
