package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import gui.app.Slay;

/**
 * Classe abstraite, parents de tous les menus
 */
public abstract class MenuScreen implements Screen {
    protected Stage stage;
    protected Slay parent;

    public MenuScreen(Slay parent) {
        this.parent = parent;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }
}
