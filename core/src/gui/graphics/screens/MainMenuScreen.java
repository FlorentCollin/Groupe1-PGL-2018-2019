package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import gui.app.Slay;
import gui.utils.RectangleActor;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sizeTo;
import static gui.graphics.screens.animations.Animations.*;

public class MainMenuScreen extends MenuScreen {
    private MainMenuScreen thisMenu;
    private VerticalGroup verticalGroup;
    private Group slayLogo;
    private TextButton whiteSlay;
    private TextButton shadowSlay;
    private TextButton playOfflineButton;
    private TextButton playOnlineButton;
    private TextButton shorcutsButton;
    private TextButton settingsButton;
    private TextButton exitButton;

    private RectangleActor underlineActor;

    public MainMenuScreen(Slay parent) {
        super(parent);
        this.thisMenu = this; //Référence vers cette instance
        //Création des différents bouttons disponibles pour l'utilisateur dans le menu principal
        verticalGroup = new VerticalGroup();
        Skin uiSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        TextButton.TextButtonStyle textButtonStyle = uiSkin.get(TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFont;
        playOfflineButton = new TextButton("Play Offline", textButtonStyle);
        playOnlineButton = new TextButton("Play Online", textButtonStyle);
        shorcutsButton = new TextButton("Shortcuts", textButtonStyle);
        settingsButton = new TextButton("Settings", textButtonStyle);
        exitButton = new TextButton("Exit", textButtonStyle);
        underlineActor = new RectangleActor();
        underlineActor.setX(Gdx.graphics.getWidth() / 2);
        underlineActor.setY(Gdx.graphics.getHeight() / 2);
        underlineActor.setSize(0, 10);
        underlineActor.setColor(Color.WHITE);
        stage.addActor(underlineActor);

        verticalGroup.space(20);
        verticalGroup.center();
        verticalGroup.setX(-playOnlineButton.getWidth());
        verticalGroup.setY(Gdx.graphics.getHeight() / 2 - Gdx.graphics.getHeight() / 10);
        verticalGroup.addActor(playOfflineButton);
        verticalGroup.addActor(playOnlineButton);
        verticalGroup.addActor(shorcutsButton);
        verticalGroup.addActor(settingsButton);
        verticalGroup.addActor(exitButton);

        slayLogo = new Group();
        textButtonStyle = uiSkin.get("logo", TextButton.TextButtonStyle.class);
        textButtonStyle.font = logoFont;
        whiteSlay = new TextButton("SLAY", textButtonStyle);
        textButtonStyle = uiSkin.get("logoShadow", TextButton.TextButtonStyle.class);
        textButtonStyle.font = logoFont;
        shadowSlay = new TextButton("SLAY", textButtonStyle);
        shadowSlay.setX(-7);

        shadowSlay.setY(-5);
        slayLogo.setX(-whiteSlay.getWidth());
        slayLogo.setY(Gdx.graphics.getHeight() /2 + Gdx.graphics.getHeight() / 5);

        slayLogo.addActor(shadowSlay);
        slayLogo.addActor(whiteSlay);

        stage.addActor(slayLogo);
        stage.addActor(verticalGroup);

        playOfflineButton.addListener(this.underlineAnimation(playOfflineButton));
        playOnlineButton.addListener(this.underlineAnimation(playOnlineButton));
        shorcutsButton.addListener(this.underlineAnimation(shorcutsButton));
        settingsButton.addListener(this.underlineAnimation(settingsButton));
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.changeScreen(SettingsMenuScreen.class);
            }
        });
        exitButton.addListener(this.underlineAnimation(exitButton));
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.dispose();
            }
        });
    }

    public Stage getStage() {
        return stage;
    }

    @Override

    public void show() {
        verticalGroup.addAction(slideFromLeft(verticalGroup, Gdx.graphics.getWidth() / 2, verticalGroup.getY()));
        slayLogo.addAction(slideFromLeft(slayLogo, Gdx.graphics.getWidth() / 2 - whiteSlay.getWidth() / 2, slayLogo.getY()));
    }
    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        verticalGroup.addAction(slideToLeft(verticalGroup));
        slayLogo.addAction(slideToLeft(slayLogo));
    }

    @Override
    public void dispose() {
    }

    private ClickListener underlineAnimation(Actor actor) {
        return new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(!actor.getParent().hasActions()) {
                underlineActor.clearActions();
                underlineActor.setSize(0, underlineActor.getHeight());
                Vector2 coords = new Vector2(0,0);
                actor.localToStageCoordinates(coords);
                underlineActor.setX(coords.x);
                underlineActor.setY(coords.y);
                underlineActor.addAction(sizeTo(actor.getWidth(), underlineActor.getHeight(), 0.5f, ANIMATION_INTERPOLATION));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                underlineActor.clearActions();
                underlineActor.setSize(0, underlineActor.getHeight());
            }
        };
    }
}
