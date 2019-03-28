package ac.umons.slay.g01.gui.utils;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.UUID;

public class JoinButton extends TextButton {

    private UUID id;

    public JoinButton(String text, TextButtonStyle style, UUID id) {
        super(text, style);
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
