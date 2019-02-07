package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import gui.app.Slay;

import static gui.graphics.screens.animations.Animations.*;

public class SettingsMenuScreen extends MenuScreen {
    private TextButton settingsText;

    public SettingsMenuScreen(Slay parent, Stage stage) {
        super(parent, stage);
        TextButton.TextButtonStyle settingsTextStyle = uiSkin.get(TextButton.TextButtonStyle.class);
        settingsTextStyle.font = defaultFont;
        settingsText = new TextButton("Settings", settingsTextStyle);
        settingsText.setX(Gdx.graphics.getWidth());
        settingsText.setY(Gdx.graphics.getHeight() / 2);

        settingsText.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.changeScreen(MainMenuScreen.class);
            }
        });
        stage.addActor(settingsText);
    }

    @Override
    public void show() {
        settingsText.addAction(slideFromRight(settingsText, Gdx.graphics.getWidth() / 2 - settingsText.getWidth() / 2 , settingsText.getY(), ANIMATION_DURATION / 2));
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        settingsText.addAction(slideToRight(settingsText));
    }

    @Override
    public void dispose() {

    }
}
