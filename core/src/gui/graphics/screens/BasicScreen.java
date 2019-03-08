package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import gui.app.Slay;
import gui.utils.Constants;

/**
 * Classe représentant un Screen, c'est la classe étendue par tous les autres screens.
 */
public abstract class BasicScreen implements Screen {
    protected Stage stage;
    protected Slay parent;
    protected static boolean init = false;
    protected static BitmapFont defaultFontTitle;
    protected static BitmapFont defaultFont;
    protected static BitmapFont defaultFontItalic;
    protected static BitmapFont logoFont;
    protected static BitmapFont textFont;
    protected static Skin uiSkin;
    protected FreeTypeFontGenerator generator;
    protected FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    protected float ratio;
    protected OrthographicCamera camera;
    protected ScalingViewport viewport;

    public BasicScreen(Slay parent) {
        this.parent = parent;
        //Chargement du skin spécifique pour l'interface graphique
        generateStage();
        if (!init)
            init();
        ratio = Constants.getRatioX(stage.getWidth());
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    public BasicScreen(Slay parent, Stage stage) {
        this.parent = parent;
        this.stage = stage;
        ratio = Constants.getRatioX(stage.getWidth());
        if (!init)
            init();
        generateFont();
        this.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        this.stage.draw();
    }

    public void init() {
        uiSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        generateFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(16 / 255, 16 / 255f, 16 / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getViewport().apply();
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        stage.getBatch().setProjectionMatrix(stage.getCamera().combined);
    }

    @Override
    public void dispose() {
        generator.dispose();
        stage.dispose();
    }

    protected void generateStage() {
        camera = new OrthographicCamera();
        viewport = new ScalingViewport(Scaling.stretch, 1920, 1080, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
    }

    protected void generateFont() {
        //Génération des différentes polices utilisées dans le jeu
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/LemonMilk/LemonMilk.otf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 42;
        defaultFont = generator.generateFont(parameter);
        parameter.size = 64;
        defaultFontTitle = generator.generateFont(parameter);
        parameter.size = 128;
        logoFont = generator.generateFont(parameter);

        generator.dispose();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/LemonMilk/LemonMilkItalicLight.otf"));
        parameter.size = 36;
        defaultFontItalic = generator.generateFont(parameter);

        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto/Roboto-Light.ttf"));
        parameter.size = 28;
        textFont = generator.generateFont(parameter);

        //Application d'un filtre qui permet de resize et d'éviter la pixellisation
        defaultFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        defaultFontTitle.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        defaultFontItalic.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        textFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        generator.dispose();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    public BitmapFont getDefaultFont() {
        return defaultFont;
    }

    public BitmapFont getDefaultFontTitle() {
        return defaultFontTitle;
    }

    public BitmapFont getDefaultFontItalic() {
        return defaultFontItalic;
    }

    public BitmapFont getTextFont() {
        return textFont;
    }

    public Skin getUiSkin() {
        return uiSkin;
    }
}
