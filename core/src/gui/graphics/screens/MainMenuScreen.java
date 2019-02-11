package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import gui.app.Slay;
import gui.utils.Constants;
import gui.utils.RectangleActor;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sizeTo;
import static gui.graphics.screens.animations.Animations.*;

public class MainMenuScreen extends MenuScreen {
    private VerticalGroup bouttonsGroup;
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

        //Création des différents bouttons disponibles pour l'utilisateur dans le menu principal
        TextButton.TextButtonStyle textButtonStyle = uiSkin.get(TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontTitle;
        playOfflineButton = new TextButton("Play Offline", textButtonStyle);
        playOnlineButton = new TextButton("Play Online", textButtonStyle);
        shorcutsButton = new TextButton("Shortcuts", textButtonStyle);
        settingsButton = new TextButton("Settings", textButtonStyle);
        exitButton = new TextButton("Exit", textButtonStyle);
        //Ajout des bouttons dans un group
        bouttonsGroup = new VerticalGroup();
        bouttonsGroup.space(20);
        bouttonsGroup.center();
        bouttonsGroup.setY(stage.getHeight() / 2 - stage.getHeight() / 10);
        bouttonsGroup.addActor(playOfflineButton);
        bouttonsGroup.addActor(playOnlineButton);
        bouttonsGroup.addActor(shorcutsButton);
        bouttonsGroup.addActor(settingsButton);
        bouttonsGroup.addActor(exitButton);

        stage.addActor(bouttonsGroup);

        //Création du rectangle qui apparait en dessous des bouttons lors que
        // la souris de l'utilisateur se trouve sur un boutton
        underlineActor = new RectangleActor();
        underlineActor.setSize(0, 10 * Constants.getRatio(stage.getWidth()));
        underlineActor.setColor(Color.WHITE);
        stage.addActor(underlineActor);

        //Création du logo Slay qui apparait en haut dans le menu principal
        slayLogo = new Group();

        textButtonStyle = uiSkin.get("logo", TextButton.TextButtonStyle.class);
        textButtonStyle.font = logoFont;
        whiteSlay = new TextButton("SLAY", textButtonStyle);
        textButtonStyle = uiSkin.get("logoShadow", TextButton.TextButtonStyle.class);
        textButtonStyle.font = logoFont;
        shadowSlay = new TextButton("SLAY", textButtonStyle);
        shadowSlay.setX(-7); //Décalage de l'ombre pour créer une ombre portée
        shadowSlay.setY(-5);
        slayLogo.setY(stage.getHeight() / 2 + stage.getHeight() / 5);

        slayLogo.addActor(shadowSlay);
        slayLogo.addActor(whiteSlay);

        stage.addActor(slayLogo);

        //Ajout des animations de soulignage du boutton sélectionné
        //Ainsi que des différents listeners pour changer de menu ou quitter le jeu
        playOfflineButton.addListener(this.underlineAnimation(playOfflineButton));
        playOfflineButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.changeScreen(CreateRoomMenuScreen.class);
            }
        });
        playOnlineButton.addListener(this.underlineAnimation(playOnlineButton));
        playOnlineButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    parent.changeScreen(OnlineMenuScreen.class);
                }
            });
        shorcutsButton.addListener(this.underlineAnimation(shorcutsButton));
        shorcutsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.changeScreen(ShortcutsMenuScreen.class);
            }
        });
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
        //Animation d'entrée
        bouttonsGroup.addAction(slideFromLeft(bouttonsGroup, stage.getWidth() / 2, bouttonsGroup.getY()));
        slayLogo.addAction(slideFromLeft(slayLogo, stage.getWidth() / 2 - whiteSlay.getWidth() / 2, slayLogo.getY()));
    }
    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        //Animation de sortie
        bouttonsGroup.addAction(slideToLeft(bouttonsGroup));
        slayLogo.addAction(slideToLeft(slayLogo));
    }

    @Override
    public void dispose() {
    }

    /**
     * Méthode qui créer un ClickListener qui réagit pour créer l'effet de soulignage dans le menu
     * lorsque l'utilisateur positionne sa souris au dessus d'un boutton
     * @param actor l'actor sur lequel le ClickListener va agir
     * @return le ClickListener configuré pour le soulignage
     */
    private ClickListener underlineAnimation(Actor actor) {
        return new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                //On évite d'activer l'animation de soulignage si le parent est en train lui aussi d'effectuer une action
                if(!actor.getParent().hasActions()) {
                underlineActor.clearActions();
                underlineActor.setSize(0, underlineActor.getHeight());
                //Récupération des coordonnés pas rapport au stage et non au group
                Vector2 coords = new Vector2(0,0);
                actor.localToStageCoordinates(coords);
                underlineActor.setX(coords.x);
                underlineActor.setY(coords.y);
                //Animation de soulignage
                underlineActor.addAction(sizeTo(actor.getWidth(), underlineActor.getHeight(), 0.5f, ANIMATION_INTERPOLATION));
                }
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                underlineActor.clearActions(); //Permet de détruire l'action en cours 
                underlineActor.setSize(0, underlineActor.getHeight());
            }
        };
    }
}
