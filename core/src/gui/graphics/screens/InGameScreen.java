package gui.graphics.screens;


import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import gui.app.Slay;

public class InGameScreen extends BasicScreen {

    private TiledMap map;
    private HexagonalTiledMapRenderer tiledMapRenderer;
    private TiledMapTileLayer board;
    private TiledMapTileSet tileSet;

    public InGameScreen(Slay parent, String mapName) {
        super(parent);
        camera.setToOrtho(true);
        camera.update();
        map = new TmxMapLoader().load("maps/" + mapName);
        tiledMapRenderer = new HexagonalTiledMapRenderer(map);
        board = (TiledMapTileLayer) map.getLayers().get("background"); //cellules
        tileSet = map.getTileSets().getTileSet("hex");

    }

    @Override
    public void render(float delta) {
        super.render(delta);
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
    }

    @Override
    public void show() {

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
}
