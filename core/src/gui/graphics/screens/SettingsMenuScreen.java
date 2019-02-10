package gui.graphics.screens;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import gui.app.Slay;

public class SettingsMenuScreen extends SubMenuScreen {
    private Label windowMode;
    private Label screenResolution;
    private Label musicLevel;
    private Label soundLevel;

    public SettingsMenuScreen(Slay parent, Stage stage) {
        super(parent, stage, "SETTINGS");
        windowMode = new Label("Window Mode", uiSkin);
        windowMode.setAlignment(Align.left);
        screenResolution = new Label("Screen Resolution", uiSkin);
        musicLevel = new Label("Music", uiSkin);
        soundLevel = new Label("Sound", uiSkin);
        Table scrollTable = new Table();
        scrollTable.add(windowMode).fillY().align(Align.left);
        scrollTable.row();
        scrollTable.add(screenResolution).fillY().align(Align.left);
        scrollTable.row();
        scrollTable.add(musicLevel).fillY().align(Align.left);
        scrollTable.row();
        scrollTable.add(soundLevel).fillY().align(Align.left);
        scrollTable.row();

        ScrollPane scroller = new ScrollPane(scrollTable);
        Table table = new Table();
        table.setWidth(stage.getWidth() - stage.getWidth() / 4);
        table.setHeight(stage.getHeight() - (stage.getHeight() -menuNameGroup.getY())*2);
        table.setX(stage.getWidth() / 4);
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
