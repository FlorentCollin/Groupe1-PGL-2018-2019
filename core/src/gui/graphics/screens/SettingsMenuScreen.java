package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import gui.app.Slay;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static gui.graphics.screens.animations.Animations.*;

public class SettingsMenuScreen extends MenuScreen {
    private TextButton textButton;

    public SettingsMenuScreen(Slay parent, Stage stage) {
        super(parent, stage);
        Skin uiSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        TextButton.TextButtonStyle textButtonStyle = uiSkin.get(TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFont;
        textButton = new TextButton("This is the settings screen", textButtonStyle);
        textButton.setX(Gdx.graphics.getWidth());
        textButton.setY(Gdx.graphics.getHeight() / 2);

        textButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.changeScreen(MainMenuScreen.class);
            }
        });
        stage.addActor(textButton);
    }

    @Override
    public void show() {
        textButton.addAction(slideFromRight(textButton, Gdx.graphics.getWidth() / 2 - textButton.getWidth() / 2 , textButton.getY(), ANIMATION_DURATION / 2));
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        textButton.addAction(slideToRight(textButton));
    }

    @Override
    public void dispose() {

    }
}
