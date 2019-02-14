package gui.graphics.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import gui.app.Slay;
import logic.Coords.OffsetCoords;
import logic.Coords.TransformCoords;

public class InGameScreen extends BasicScreen {

    private TiledMap map;
    private HexagonalTiledMapRenderer tiledMapRenderer;
    private TiledMapTileLayer board;
    private TiledMapTileSet tileSet;
    private Vector3 mouseLoc = new Vector3();


    public InGameScreen(Slay parent, String mapName) {
        super(parent);
        camera.setToOrtho(true);
        map = new TmxMapLoader().load("maps/" + mapName);
        tiledMapRenderer = new HexagonalTiledMapRenderer(map);
        board = (TiledMapTileLayer) map.getLayers().get("background"); //cellules
        tileSet = map.getTileSets().getTileSet("hex");
        System.out.println(tileSet.getTile(1));

        viewport = new FillViewport((board.getWidth()/2) * board.getTileWidth() + (board.getWidth() / 2) * (board.getTileWidth() / 2) + board.getTileWidth()/4,
                board.getHeight() * board.getTileHeight() + board.getTileHeight() / 2, camera);
        camera.update();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        camera.update();
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
        mouseLoc.x = Gdx.input.getX();
        mouseLoc.y = Gdx.input.getY();
        camera.unproject(mouseLoc);
        if(Gdx.input.isButtonPressed(102)) {
            OffsetCoords coords = getCoordsFromMousePosition(mouseLoc);
            if(board.getCell(coords.col,coords.row) != null) {
                board.getCell(coords.col, coords.row).setTile(tileSet.getTile(1));
            }
        }
        if(Gdx.input.isKeyPressed(19)) {
            camera.zoom -= 0.05;
        }
        if(Gdx.input.isKeyPressed(20)) {
            camera.zoom += 0.05;
        }

        if(Gdx.input.isKeyPressed(51)) {
            camera.translate(0,-10);
        }
        if(Gdx.input.isKeyPressed(47)) {
            camera.translate(0,10);
        }
        if(Gdx.input.isKeyPressed(29)) {
            camera.translate(-10,0);
        }
        if(Gdx.input.isKeyPressed(32)) {
            camera.translate(+10,0);
        }

    }

    protected void generateStage() {
        camera = new OrthographicCamera();
        viewport = new FillViewport(1920, 1080, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Retourne les coordonnées de la cellule qui se trouve à la position de la souris
     * @param mouseLoc position de la souris
     * @return les coordonnées de la cellule qui est à la position de la souris
     */
    private OffsetCoords getCoordsFromMousePosition(Vector3 mouseLoc) {
        //- board.getTileWidth() / 2 et - board.getTileHeight() / 2 sont là pour créer le décalage de l'origine.
        // Ce qui permet de retrouver les bonnes coordonnés
        // le (int)board.getTileWidth() /2 correspond à la taille de l'hexagone (ie la longueur de la droite qui va du
        // centre vers une des pointes de l'hexagone
        return TransformCoords.pixelToOffset((int)(mouseLoc.x - board.getTileWidth() / 2),
                (int)(mouseLoc.y - board.getTileHeight() / 2), (int)board.getTileWidth() /2);
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
