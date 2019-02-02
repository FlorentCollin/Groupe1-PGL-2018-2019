package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import gui.app.Slay;

public class MainMenuScreen extends MenuScreen implements Screen {

    public MainMenuScreen(Slay parent) {
        super(parent);
        Table table = new Table();
        table.setFillParent(true);
        table.setDebug(true);
        stage.addActor(table);
        Texture texture = new Texture("play.png");
        ImageButton play = new ImageButton(new TextureRegionDrawable(new TextureRegion(texture)));
        ImageButton exit = new ImageButton(new TextureRegionDrawable(new TextureRegion(new Texture("exit.png"))));
        table.add(play).uniformX();
        table.row().pad(10,0,10,0);
        table.add(exit).uniformX();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f,0f,0f,1);
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
