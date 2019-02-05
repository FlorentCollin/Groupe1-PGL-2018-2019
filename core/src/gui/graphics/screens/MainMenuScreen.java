package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import gui.app.Slay;
import gui.utils.RectangleActor;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sizeTo;

public class MainMenuScreen extends MenuScreen {
    private VerticalGroup verticalGroup;
    private Group slayLogo;
    private TextButton whiteSlay;
    private TextButton shadowSlay;
    private TextButton playOfflineButton;
    private TextButton playOnlineButton;
    private TextButton shorcutsButton;
    private TextButton settingsButton;
    private TextButton exitButton;

    private RectangleActor rectangleActor;

    public MainMenuScreen(Slay parent) {
        super(parent);
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
        rectangleActor = new RectangleActor();
        rectangleActor.setX(Gdx.graphics.getWidth() / 2);
        rectangleActor.setY(Gdx.graphics.getHeight() / 2);
        rectangleActor.setSize(0, 10);
        rectangleActor.setColor(Color.WHITE);
        stage.addActor(rectangleActor);
        verticalGroup.space(20);
        verticalGroup.addActor(playOfflineButton);
        verticalGroup.addActor(playOnlineButton);
        verticalGroup.addActor(shorcutsButton);
        verticalGroup.addActor(settingsButton);
        verticalGroup.addActor(exitButton);
        verticalGroup.center();
        verticalGroup.setX(-playOnlineButton.getWidth());
        verticalGroup.setY(Gdx.graphics.getHeight() / 2 - Gdx.graphics.getHeight() / 10);

        slayLogo = new Group();
        textButtonStyle = uiSkin.get("logo", TextButton.TextButtonStyle.class);
        textButtonStyle.font = logoFont;
        whiteSlay = new TextButton("SLAY", textButtonStyle);
        textButtonStyle = uiSkin.get("logoShadow", TextButton.TextButtonStyle.class);
        textButtonStyle.font = logoFont;
        shadowSlay = new TextButton("SLAY", textButtonStyle);
        shadowSlay.setX(-7);
        shadowSlay.setY(-5);
        slayLogo.addActor(shadowSlay);
        slayLogo.addActor(whiteSlay);
        slayLogo.setX(-whiteSlay.getWidth());
        slayLogo.setY(Gdx.graphics.getHeight() /2 + Gdx.graphics.getHeight() / 5);
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
        verticalGroup.addAction(moveTo(Gdx.graphics.getWidth() / 2, verticalGroup.getY(), ANIMATION_DURATION, ANIMATION_INTERPOLATION));
        slayLogo.addAction(moveTo(Gdx.graphics.getWidth() / 2 - whiteSlay.getWidth() / 2, slayLogo.getY(), ANIMATION_DURATION, ANIMATION_INTERPOLATION));
    }
    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        verticalGroup.addAction(moveTo(-playOfflineButton.getWidth(), verticalGroup.getY(), ANIMATION_DURATION, ANIMATION_INTERPOLATION));
        slayLogo.addAction(moveTo( - whiteSlay.getWidth() , slayLogo.getY(), ANIMATION_DURATION, ANIMATION_INTERPOLATION));
    }

    private ClickListener underlineAnimation(Actor actor) {
        return new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(!actor.hasActions()) {
                rectangleActor.clearActions();
                rectangleActor.setSize(0, rectangleActor.getHeight());
                Vector2 coords = new Vector2(0,0);
                actor.localToStageCoordinates(coords);
                rectangleActor.setX(coords.x);
                rectangleActor.setY(coords.y);
                stage.addActor(rectangleActor);
                rectangleActor.addAction(sizeTo(actor.getWidth(), rectangleActor.getHeight(), 0.5f, ANIMATION_INTERPOLATION));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                rectangleActor.clearActions();
                rectangleActor.setSize(0, rectangleActor.getHeight());
            }
        };
    }
}
