package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import gui.app.Slay;

public class MainMenuScreen extends MenuScreen implements Screen {

    public MainMenuScreen(Slay parent) {
        super(parent);
        Skin uiSkin = new Skin(Gdx.files.internal("uiskin.json"));
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        TextButton button = new TextButton("Play", uiSkin);
        button.scaleBy(10);
        table.row().uniformX();
        table.add(button);
        stage.addActor(table);
<<<<<<< HEAD
        Skin uiskin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        TextButton button = new TextButton("This is a TextButton", uiskin);
        table.add(button).uniformX();
=======
>>>>>>> 035352f61b2af629aefdefac28d33fead443afc8

    }

    @Override
    public void show() {

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

    }
}
