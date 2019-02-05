package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import gui.app.Slay;

/**
 * Classe abstraite, parents de tous les menus
 */
public abstract class MenuScreen implements Screen {
    protected Stage stage;
    protected Slay parent;
    protected BitmapFont defaultFont;
    protected BitmapFont logoFont;
    protected static final float ANIMATION_DURATION = 0.6f;
    protected static final Interpolation ANIMATION_INTERPOLATION = Interpolation.pow5;

    public MenuScreen(Slay parent) {
        this.parent = parent;
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/LemonMilk/LemonMilk.otf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 64;
        defaultFont = generator.generateFont(parameter);
        parameter.size = 128;
        logoFont = generator.generateFont(parameter);
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    public MenuScreen(Slay parent, Stage stage) {
        this(parent);
        this.stage = stage;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(16/255,16/255f,16/255f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
}
