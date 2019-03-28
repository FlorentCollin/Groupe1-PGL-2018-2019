package gui.graphics.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import gui.app.Slay;
import gui.utils.Language;

import static gui.graphics.screens.animations.Animations.*;
import static gui.utils.Constants.PAD;

/**
 * Classe qui représente le menu des paramètres
 */
public class SettingsMenuScreen extends SubMenuScreen {
    private Table table;
    private Slider playerSlider;
    private Label playerSliderPourcent;

    public SettingsMenuScreen(Slay parent, Stage stage) {
        super(parent, stage, Language.bundle.get("settings"));
        //Style des Label
        Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = defaultFont;

        //Création des différents labels
        Label windowMode = new Label(Language.bundle.get("windowMode"), labelStyle);
        Label username = new Label(Language.bundle.get("username"), labelStyle);
        Label language = new Label(Language.bundle.get("language"), labelStyle);
        Label numberOfPlayers = new Label(Language.bundle.get("numberOnline"), labelStyle);
        TextButton.TextButtonStyle textButtonStyle = uiSkin.get("button", TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;

        //Création des différents boutons.
        //Window mode
        TextButton fullScreen = new TextButton(Language.bundle.get("fullscreen"), textButtonStyle);
        fullScreen.setChecked(parent.getUserSettings().isFullScreen());
        TextButton windowed = new TextButton(Language.bundle.get("windowed"), textButtonStyle);
        windowed.setChecked(!parent.getUserSettings().isFullScreen());

        fullScreen.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               if(fullScreen.isChecked() && ! parent.getUserSettings().isFullScreen()) {
                   parent.getUserSettings().setFullScreen(true);
               }
           }
        });

        windowed.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(windowed.isChecked() && parent.getUserSettings().isFullScreen()) {
                    parent.getUserSettings().setFullScreen(false);
                }
            }
        });

        ButtonGroup<TextButton> buttonGroup = new ButtonGroup<>(fullScreen, windowed);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        buttonGroup.setUncheckLast(true);

        //Username field
        TextField.TextFieldStyle textFieldStyle = uiSkin.get(TextField.TextFieldStyle.class);
        textFieldStyle.font = textFont;
        textFont.getData().padLeft = -10;

        TextField usernameField = new TextField(parent.getUserSettings().getUsername(), textFieldStyle);
        usernameField.setMaxLength(10);
        usernameField.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                parent.getUserSettings().setUsername(usernameField.getText());
            }
        });

        //Language selectBox
        SelectBox.SelectBoxStyle selectBoxStyle = uiSkin.get(SelectBox.SelectBoxStyle.class);
        selectBoxStyle.font = textFont;
        selectBoxStyle.listStyle.font = textFont;

        SelectBox<String> languageSelectBox = new SelectBox<>(selectBoxStyle);
        languageSelectBox.setItems("English", "Français");
        if(parent.getUserSettings().getLanguage().equals("fr")) {
            languageSelectBox.setSelected("Français");
        }
        languageSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String language = languageSelectBox.getSelected().toLowerCase().substring(0,2);
                parent.getUserSettings().setLanguage(language);
                Language.setLanguage(language);
                //On clear le screen pour reafficher le menu principal dans la langue choisie par l'utilisateur
                parent.clearScreen();
                parent.changeScreen(MainMenuScreen.class);
            }
        });

        //Slider pour la musique et les sons
        playerSlider = new Slider(1, 6, 1, false, uiSkin);
        playerSlider.setValue(parent.getUserSettings().getNumberOfPlayers());
        //Update du pourcentage affiché à l'écran
        playerSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                playerSliderPourcent.setText((int)playerSlider.getValue());
                parent.getUserSettings().setNumberOfPlayer((int) playerSlider.getValue());
            }
        });

        //Création des pourcentage correspondant au slider
        playerSliderPourcent = new Label(Integer.toString(parent.getUserSettings().getNumberOfPlayers()), labelStyle);

        //Création de la table contenant l'ensemble des éléments définis plus haut
        Table scrollTable = new Table();
        scrollTable.add(windowMode).expandX().align(Align.left);
        scrollTable.add(fullScreen).pad(PAD).align(Align.right);
        scrollTable.add(windowed).pad(PAD/2f);
        scrollTable.row();

        scrollTable.add(username).expandX().align(Align.left);
        scrollTable.add(usernameField).pad(PAD).minWidth(fullScreen.getWidth());
        scrollTable.row();

        scrollTable.add(language).expandX().align(Align.left);
        scrollTable.add(languageSelectBox).pad(PAD).minWidth(fullScreen.getWidth());
        scrollTable.row();

        scrollTable.add(numberOfPlayers).expandX().fillY().align(Align.left);
        scrollTable.add(playerSlider).pad(PAD)
                .minWidth(100 * ratio).maxWidth(fullScreen.getWidth()*2 + PAD*2).fillX().align(Align.right).colspan(2);
        scrollTable.add(playerSliderPourcent).minWidth(playerSlider.getWidth()).padRight(5).fillY().align(Align.right);
        scrollTable.row();

        ScrollPane scroller = new ScrollPane(scrollTable);
        //Désactivation du scrolling horizontal
        scroller.setScrollingDisabled(true, false);
        table = new Table();
        table.setWidth(stage.getWidth() - stage.getWidth() / 5);
        table.setHeight(stage.getHeight() - (stage.getHeight() -menuNameGroup.getY())*2);
        table.add(scroller).fillX().expand().align(Align.topLeft);

        stage.addActor(table);
    }

    @Override
    public void show() {
        super.show();
        table.addAction(slideFromRight(table, stage.getWidth() / 5, table.getY(), ANIMATION_DURATION / 4));

    }

    @Override
    public void hide() {
        super.hide();
        table.addAction(slideToRight(table));
    }
}
