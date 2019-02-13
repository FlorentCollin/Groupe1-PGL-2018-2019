package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import gui.app.Slay;

import static gui.graphics.screens.animations.Animations.*;
import static gui.utils.Constants.PAD;

/**
 * Classe qui représente le menu des paramètres
 */
public class SettingsMenuScreen extends SubMenuScreen {
    private Table table;
    private Label windowMode;
    private Label screenResolution;
    private Label musicLevel;
    private Label soundLevel;
    private Slider musicSlider;
    private Slider soundSlider;
    private Label musicSliderPourcent;
    private Label soundSliderPourcent;


    public SettingsMenuScreen(Slay parent, Stage stage) {
        super(parent, stage, "SETTINGS");
        //Style des Label
        Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = defaultFont;

        //Création des différents labels
        windowMode = new Label("Window Mode", labelStyle);
        windowMode.setAlignment(Align.left);
        screenResolution = new Label("Screen Resolution", labelStyle);
        musicLevel = new Label("Music", labelStyle);
        soundLevel = new Label("Sound", labelStyle);
        TextButton.TextButtonStyle textButtonStyle = uiSkin.get("button", TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;
        //Création des différents boutons.
        TextButton fullScreen = new TextButton("fullscreen", textButtonStyle);
        fullScreen.setTransform(true);
        TextButton windowed = new TextButton("windowed", textButtonStyle);
        windowed.setTransform(true);
        windowed.setChecked(true);

        ButtonGroup<TextButton> buttonGroup = new ButtonGroup<>(fullScreen, windowed);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        buttonGroup.setUncheckLast(true);

        SelectBox.SelectBoxStyle selectBoxStyle = uiSkin.get(SelectBox.SelectBoxStyle.class);
        selectBoxStyle.font = textFont;
        selectBoxStyle.listStyle.font = textFont;
        SelectBox selectBox = new SelectBox(selectBoxStyle);
        selectBox.setAlignment(Align.center);
        selectBox.getList().setAlignment(Align.right);
        selectBox.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                System.out.println(selectBox.getSelected());
            }
        });
        selectBox.setItems("Android1", "Windows1 long text in item", "Linux1", "OSX1", "Android2", "Windows2", "Linux2", "OSX2",
                "Android3", "Windows3", "Linux3", "OSX3", "Android4", "Windows4", "Linux4", "OSX4", "Android5", "Windows5", "Linux5",
                "OSX5", "Android6", "Windows6", "Linux6", "OSX6", "Android7", "Windows7", "Linux7", "OSX7");
        selectBox.setSelected("Linux6");
        musicSlider = new Slider(0, 100, 1, false, uiSkin);
        musicSlider.setValue(100);
        //Update du pourcentage affiché à l'écran
        musicSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                musicSliderPourcent.setText((int)musicSlider.getValue() + "%");
            }
        });
        soundSlider = new Slider(0, 100, 1, false, uiSkin);
        soundSlider.setValue(100);
        //Update du pourcentage affiché à l'écran
        soundSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                soundSliderPourcent.setText((int)soundSlider.getValue() + "%");

            }
        });
        //Création des pourcentage correspondant au slider
        musicSliderPourcent = new Label("100%", labelStyle);
        soundSliderPourcent = new Label("100%", labelStyle);

        //Création de la table contenant l'ensemble des éléments définis plus haut
        Table scrollTable = new Table();
        scrollTable.add(windowMode).expandX().align(Align.left);
        scrollTable.add(fullScreen).pad(PAD).align(Align.right);
        scrollTable.add(windowed).pad(PAD);
        scrollTable.row();

        scrollTable.add(screenResolution).align(Align.left);
        scrollTable.add(selectBox).grow().pad(PAD).width(fullScreen.getWidth()).align(Align.right);
        scrollTable.row();
        scrollTable.add(musicLevel).expandX().fillY().align(Align.left);
        scrollTable.add(musicSlider).pad(PAD)
                .minWidth(100 * ratio).maxWidth(fullScreen.getWidth()*2 + PAD*2).fillX().align(Align.right).colspan(2);
        scrollTable.add(musicSliderPourcent).minWidth(musicSliderPourcent.getWidth()).padRight(PAD).fillY().align(Align.right);
        scrollTable.row();
        scrollTable.add(soundLevel).fillY().align(Align.left);
        scrollTable.add(soundSlider).padRight(PAD).minWidth(100*ratio).maxWidth(fullScreen.getWidth()*2 + PAD*2)
                .pad(PAD).fillX().align(Align.right).colspan(2);
        scrollTable.add(soundSliderPourcent).minWidth(soundSliderPourcent.getWidth()).padRight(PAD).fillY().align(Align.right);
        scrollTable.row();
        //TODO
        ScrollPane scroller = new ScrollPane(scrollTable);
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

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}
