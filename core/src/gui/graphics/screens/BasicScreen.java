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
    private static BitmapFont smallTextFont;
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
        if (!init) //Si aucun screen n'a été instancié on initialise le skin et les polices
            init();
        ratio = Constants.getRatioX(stage.getWidth());
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    public BasicScreen(Slay parent, Stage stage) {
        this.parent = parent;
        this.stage = stage;
        ratio = Constants.getRatioX(stage.getWidth());
        if (!init) //Si aucun screen n'a été instancié on initialise le skin et les polices
            init();
        this.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        this.stage.draw();
    }

    public void init() {
        uiSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        generateFont();
    }

    @Override
    public void render(float delta) {
        //On reset la couleur de fond
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
        stage.dispose();
    }

    /**
     * Méthode qui va générer le stage
     */
    protected void generateStage() {
        camera = new OrthographicCamera();
        viewport = new ScalingViewport(Scaling.stretch, 1920, 1080, camera);
        //Update du viewport avec la taille actuel de l'application
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Méthode qui génère les différentes polices utilisés dans les différents screens de l'application
     */
    private void generateFont() {
        //Lemon Milk font
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/LemonMilk/LemonMilk.otf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 42;
        defaultFont = generator.generateFont(parameter);
        parameter.size = 64;
        defaultFontTitle = generator.generateFont(parameter);
        parameter.size = 128;
        logoFont = generator.generateFont(parameter);

        //Lemon Milk Italic font
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/LemonMilk/LemonMilkItalicLight.otf"));
        parameter.size = 36;
        defaultFontItalic = generator.generateFont(parameter);

        //Roboto Light font
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto/Roboto-Light.ttf"));
        parameter.size = 28;
        textFont = generator.generateFont(parameter);
        parameter.size = 14;
        smallTextFont = generator.generateFont(parameter);

        //Application d'un filtre qui permet de resize et d'éviter la pixellisation
        defaultFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        defaultFontTitle.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        defaultFontItalic.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        textFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        smallTextFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
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

    public BitmapFont getSmallTextFont() {
        return smallTextFont;
    }

    public Skin getUiSkin() {
        return uiSkin;
    }
}
