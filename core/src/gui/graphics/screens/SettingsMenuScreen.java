package gui.graphics.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import gui.app.Slay;
import gui.utils.Constants;
import gui.utils.RectangleActor;

import static gui.graphics.screens.animations.Animations.*;

public class SettingsMenuScreen extends MenuScreen {
    private Label settingsText;
    private RectangleActor rectangleSettings;
    private ImageButton arrowButton;
    private HorizontalGroup settingsGroup;
    private float ratio = Constants.getRatio(stage.getWidth());


    public SettingsMenuScreen(Slay parent, Stage stage) {
        super(parent, stage);
        Label.LabelStyle settingsTextStyle = uiSkin.get(Label.LabelStyle.class);
        settingsTextStyle.font = defaultFont;
        settingsText = new Label("Settings", settingsTextStyle);

        rectangleSettings = new RectangleActor();
        rectangleSettings.setColor(Color.WHITE);
        rectangleSettings.setSize(25 * ratio, 25 * ratio);

        settingsGroup = new HorizontalGroup();
        settingsGroup.space(25 * ratio);
        settingsGroup.setY(stage.getHeight() - 100 * ratio);
        settingsGroup.addActor(rectangleSettings);
        settingsGroup.addActor(settingsText);


        arrowButton = new ImageButton(uiSkin, "arrow");
        arrowButton.setTransform(true);
        arrowButton.setScale(0.5f * ratio);
        arrowButton.setY(stage.getHeight() - 75 * ratio);
        stage.addActor(arrowButton);

        arrowButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.changeScreen(MainMenuScreen.class);
            }
        });
        stage.addActor(settingsGroup);
    }

    @Override
    public void show() {
        settingsGroup.addAction(slideFromRight(settingsGroup, 100 * ratio, settingsGroup.getY(), ANIMATION_DURATION / 2));
        arrowButton.addAction(slideFromRight(arrowButton, 25 * ratio, arrowButton.getY(), ANIMATION_DURATION / 2));
    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        settingsGroup.addAction(slideToRight(settingsGroup));
        arrowButton.addAction(slideToRight(arrowButton));
    }

}
