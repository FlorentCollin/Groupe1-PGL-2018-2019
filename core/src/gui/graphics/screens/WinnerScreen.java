package gui.graphics.screens;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import gui.app.Slay;
import gui.utils.Language;
import logic.board.Board;

public class WinnerScreen extends BasicScreen{
    private Board board;
    private TextButton nextButton;
    private TextButton restartButton;
    private TextButton quitButton;

    public WinnerScreen(Slay parent, Board board) {
        super(parent);
        this.board = board;
        TextButton.TextButtonStyle textButtonStyle = uiSkin.get(TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontTitle;
        nextButton = new TextButton(Language.bundle.get("next"), textButtonStyle);
        restartButton = new TextButton(Language.bundle.get("restart"), textButtonStyle);
        quitButton = new TextButton(Language.bundle.get("quit"), textButtonStyle);

        VerticalGroup group = new VerticalGroup();
        group.space(20);
        group.center();
        group.setY(stage.getHeight() / 2 - stage.getHeight() / 10);
        group.addActor(nextButton);
        group.addActor(restartButton);
        group.addActor(quitButton);
        stage.addActor(group);
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

}