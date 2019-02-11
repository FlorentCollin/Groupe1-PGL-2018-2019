package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import gui.app.Slay;
import gui.graphics.screens.animations.Animations;

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
        fullScreen.setScale(ratio);
        TextButton windowed = new TextButton("windowed", textButtonStyle);
        windowed.setTransform(true);
        windowed.setScale(ratio);
        windowed.setChecked(true);
        ButtonGroup buttonGroup = new ButtonGroup(fullScreen, windowed);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);
        buttonGroup.setUncheckLast(true);

        Skin skin = new Skin(Gdx.files.internal("skin/basic/uiskin.json"));
        SelectBox.SelectBoxStyle selectBoxStyle = uiSkin.get(SelectBox.SelectBoxStyle.class);
        selectBoxStyle.font = defaultFontItalic;
        selectBoxStyle.listStyle.font = defaultFontItalic;
        final SelectBox selectBox = new SelectBox(selectBoxStyle);
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
        scrollTable.add(windowMode).expandX().fillY().align(Align.left);
        scrollTable.add(fullScreen).pad(PAD).fillY().align(Align.right);
        scrollTable.add(windowed).padRight(PAD);
        scrollTable.row();
        scrollTable.add(screenResolution).fillY().align(Align.left);
        scrollTable.add(selectBox).maxWidth(fullScreen.getWidth()).padLeft(PAD).padRight(PAD).fillY().align(Align.right);
        scrollTable.row();
        scrollTable.add(musicLevel).expandX().fillY().align(Align.left);
        scrollTable.add(musicSlider).padRight(PAD).padLeft(PAD).minWidth(100 * ratio).fillX().colspan(2);
        scrollTable.add(musicSliderPourcent).minWidth(musicSliderPourcent.getWidth()).padRight(PAD).fillY().align(Align.right);
        scrollTable.row();
        scrollTable.add(soundLevel).fillY().align(Align.left);
        scrollTable.add(soundSlider).minWidth(100*ratio).padLeft(PAD).padRight(PAD).fillX().colspan(2);
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
