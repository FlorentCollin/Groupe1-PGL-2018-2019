package gui.graphics.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import gui.app.Slay;

public class SettingsMenuScreen extends SubMenuScreen {
    private Label windowMode;
    private Label screenResolution;
    private Label musicLevel;
    private Label soundLevel;


    public SettingsMenuScreen(Slay parent, Stage stage) {
        super(parent, stage, "SETTINGS");
        Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = defaultFont;
        windowMode = new Label("Window Mode", labelStyle);
        windowMode.setAlignment(Align.left);
        screenResolution = new Label("Screen Resolution", labelStyle);
        musicLevel = new Label("Music", labelStyle);
        soundLevel = new Label("Sound", labelStyle);
        TextButton.TextButtonStyle textButtonStyle = uiSkin.get("button", TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;
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
        Table scrollTable = new Table();
        scrollTable.add(windowMode).expandX().fillY().align(Align.left);
        scrollTable.add(fullScreen).fillY().align(Align.left);
        scrollTable.add(windowed).expandX().fillY().align(Align.left).padRight(10);
        scrollTable.row();
        scrollTable.add(screenResolution).fillY().align(Align.left);
        scrollTable.row();
        scrollTable.add(musicLevel).fillY().align(Align.left);
        scrollTable.row();
        scrollTable.add(soundLevel).fillY().align(Align.left);
        scrollTable.row();

        ScrollPane scroller = new ScrollPane(scrollTable);
        Table table = new Table();
        table.setWidth(stage.getWidth() - stage.getWidth() / 5);
        table.setHeight(stage.getHeight() - (stage.getHeight() -menuNameGroup.getY())*2);
        table.setX(stage.getWidth() / 5);
        table.add(scroller).fill().expand();
        table.setDebug(true);

        stage.addActor(table);

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}
