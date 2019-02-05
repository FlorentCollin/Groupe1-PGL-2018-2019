package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import gui.app.Slay;

import java.util.ArrayList;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;

public class MainMenuScreen extends MenuScreen implements Screen {
    private Table table;
    private ArrayList<TextButton> textButtons;
    public MainMenuScreen(Slay parent) {
        super(parent);
        table = new Table();
        table.setFillParent(true);
//        table.setDebug(true);
        Skin uiSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        textButtons = new ArrayList<>();
        textButtons.add(new TextButton("Play Offline", uiSkin));
        textButtons.add(new TextButton("Play Online", uiSkin));
        textButtons.add(new TextButton("Settings", uiSkin));
        textButtons.add(new TextButton("Exit", uiSkin));
        textButtons.get(textButtons.size()-1).addListener( new ClickListener() {
            @Override
            public void clicked(InputEvent input, float x, float y) {
                dispose();
            }
        });
        table.setX(-Gdx.graphics.getWidth());
        for(TextButton textButton : textButtons) {
            table.add(textButton);
            table.row().pad(10);
        }
        stage.addActor(table);
    }

    @Override
    public void show() {
        Action action = moveTo(0, table.getY(), 1, Interpolation.pow5);
        table.addAction(action);
//        int delay = 0;
//        for(TextButton textButton : textButtons) {
//            Vector2 position = textButton.localToStageCoordinates(new Vector2(textButton.getX(), textButton.getY()));
//            textButton.addAction(sequence(delay(delay), moveTo(1000, position.y, 1, Interpolation.pow5)));
//            delay += 0.2;
//        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(8/255,8/255f,8/255f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
        Gdx.app.exit();
        System.exit(0);
    }
}
