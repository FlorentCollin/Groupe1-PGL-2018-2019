package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import gui.app.Slay;
import gui.utils.Constants;
import gui.utils.RectangleActor;

import static gui.utils.Constants.MAX_RES;

/**
 * Classe abstraite, parents de tous les menus
 */
public abstract class MenuScreen implements Screen {
    protected Stage stage;
    protected Slay parent;
    protected BitmapFont defaultFontTitle;
    protected BitmapFont defaultFont;
    protected BitmapFont defaultFontItalic;
    protected BitmapFont logoFont;
    protected Skin uiSkin;
    protected FreeTypeFontGenerator generator;
    protected FreeTypeFontGenerator.FreeTypeFontParameter parameter;
    protected float ratio;


    public MenuScreen(Slay parent) {
        this.parent = parent;
        uiSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        generateStage();
        generateFont(stage.getWidth());
        ratio = Constants.getRatio(stage.getWidth());

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
    }

    public MenuScreen(Slay parent, Stage stage) {
        this.parent = parent;
        uiSkin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        this.stage = stage;
        ratio = Constants.getRatio(stage.getWidth());
        generateFont(stage.getWidth());

        this.stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        this.stage.draw();
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

    @Override
    public void dispose() {
        generator.dispose();
        stage.dispose();
    }

    protected void generateStage() {
        OrthographicCamera camera = new OrthographicCamera();
        ScalingViewport viewport = new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
    }

    protected void generateFont(float stageWidth) {
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/LemonMilk/LemonMilk.otf"));
        parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 42 * (int) stageWidth / MAX_RES;
        defaultFont = generator.generateFont(parameter);
        parameter.size = 64 * (int) stageWidth / MAX_RES;
        defaultFontTitle = generator.generateFont(parameter);
        parameter.size = 128 * (int) stageWidth / MAX_RES;
        logoFont = generator.generateFont(parameter);

        generator.dispose();
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/LemonMilk/LemonMilkItalicLight.otf"));
        parameter.size = 42 * (int) stageWidth / MAX_RES;
        defaultFontItalic = generator.generateFont(parameter);
        generator.dispose();
    }

    protected HorizontalGroup generateMenuNameGroup(String name) {
        Label.LabelStyle textStyle = uiSkin.get(Label.LabelStyle.class);
        textStyle.font = defaultFont;
        Label label = new Label(name, textStyle);

        RectangleActor rectangle = new RectangleActor();
        rectangle.setColor(Color.WHITE);
        rectangle.setSize(25 * ratio, 25 * ratio);

        HorizontalGroup horizontalGroup = new HorizontalGroup();
        horizontalGroup.space(25 * ratio);
        horizontalGroup.setY(stage.getHeight() - 100 * ratio);
        horizontalGroup.addActor(rectangle);
        horizontalGroup.addActor(label);
        return horizontalGroup;
    }

    protected ImageButton generateArrowButton() {
        ImageButton arrowButton = new ImageButton(uiSkin, "arrow");
        arrowButton.setTransform(true);
        arrowButton.setScale(0.5f * ratio);
        arrowButton.setY(stage.getHeight() - 75 * ratio);
        return arrowButton;
    }

}
