package gui.graphics.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import gui.app.Slay;
import gui.utils.Map;
import logic.Coords.OffsetCoords;
import logic.Coords.TransformCoords;
import logic.board.Board;
import logic.board.cell.Cell;
import logic.item.Item;
import logic.item.Soldier;

import java.util.ArrayList;

public class InGameScreen extends BasicScreen {

    private Map map;
    private Vector3 mouseLoc = new Vector3();
    private float worldWith;
    private float worldHeight;
    private TiledMapTileLayer cells;
    private Board board;

    public InGameScreen(Slay parent, String mapName) {
        super(parent);
        map = new Map();
        board = map.load(mapName);
        cells = map.getCells();
        //Calcule de la grandeur de la carte
        worldWith = (cells.getWidth()/2) * cells.getTileWidth() + (cells.getWidth() / 2) * (cells.getTileWidth() / 2) + cells.getTileWidth()/4;
        worldHeight = cells.getHeight() * cells.getTileHeight() + cells.getTileHeight() / 2;
        viewport = new FillViewport(worldWith, worldHeight, camera);
        camera.update();

    }

    @Override
    public void render(float delta) {
        super.render(delta);
        camera.update();
        map.getTiledMapRenderer().setView(camera);
        map.getTiledMapRenderer().render(); //Rendering des cellules
        renderItems();

        if(Gdx.input.isButtonPressed(102)) {
            OffsetCoords coords = getCoordsFromMousePosition(getMouseLoc());
            if(cells.getCell(coords.col,coords.row) != null) {
                ArrayList<Cell> moves = board.possibleMove(board.getCell(9,7));
                for(Cell c : moves) {
                    int [] position = board.getPosition(c);
                    TiledMapTileLayer.Cell cell = cells.getCell(position[0], Math.abs(cells.getHeight()-1 - position[1]));
                    cell.setTile(map.getTileSet().getTile(2));
                }
            }
        }
        if(Gdx.input.isKeyPressed(19)) {
            camera.zoom -= 0.05;
        }
        if(Gdx.input.isKeyPressed(20)) {
            camera.zoom += 0.05;
        }

        if(Gdx.input.isKeyPressed(51)) {
            camera.translate(0,10);
        }
        if(Gdx.input.isKeyPressed(47)) {
            camera.translate(0,-10);
        }
        if(Gdx.input.isKeyPressed(29)) {
            camera.translate(-10,0);
        }
        if(Gdx.input.isKeyPressed(32)) {
            camera.translate(+10,0);
        }
        if(Gdx.input.isKeyPressed(131)) {
            parent.setScreen(new MainMenuScreen(parent));
        }
    }

    private void renderItems() {
        Cell[][] tab = board.getBoard();
        Texture texture = null;
        for (int i = 0; i < board.getColumns(); i++) {
            for (int j = 0; j < board.getRows(); j++) {
                if (tab[i][j].getItem() != null) {
                    Item item = tab[j][i].getItem();
                    if(item instanceof Soldier) {
                        switch(((Soldier) item).getLevel()) {
                            case level1:
                                texture = new Texture(Gdx.files.internal("items/" + item.getClass().getSimpleName() + "_lvl1.png"));
                                break;
                            case level2:
                                texture = new Texture(Gdx.files.internal("items/" + item.getClass().getSimpleName() + "_lvl2.png"));
                                break;
                            case level3:
                                texture = new Texture(Gdx.files.internal("items/" + item.getClass().getSimpleName() + "_lvl3.png"));
                                break;
                            case level4:
                                texture = new Texture(Gdx.files.internal("items/" + item.getClass().getSimpleName() + "_lvl4.png"));
                                break;
                        }
                    } else {
                        texture = new Texture(Gdx.files.internal("items/" + item.getClass().getSimpleName() + ".png"));
                    }
                        if(texture != null) {
                            Sprite sprite = new Sprite(texture);
                            sprite.flip(false, true);
                            stage.getBatch().begin();
                            Vector2 pos = TransformCoords.hexToPixel(j, i, (int) cells.getTileWidth() / 2);
                            stage.getBatch().draw(sprite, pos.x, pos.y);
                            stage.getBatch().end();
                        }
                }

            }

        }
    }

    protected void generateStage() {
        camera = new OrthographicCamera();
        viewport = new FillViewport(1920, 1080, camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
    }

    private Vector3 getMouseLoc() {
        mouseLoc.x = Gdx.input.getX();
        mouseLoc.y = Gdx.input.getY();
        camera.unproject(mouseLoc); //Récupération des coordonnées de la souris sur la map
        mouseLoc.y = Math.abs(worldHeight -mouseLoc.y); //On inverse l'axe des ordonnées
        //Cela permet d'avoir le repère placé en haut à gauche avec les y allant vers le bas et les x vers la droite
        return mouseLoc;
    }
    /**
     * Retourne les coordonnées de la cellule qui se trouve à la position de la souris
     * @param mouseLoc position de la souris
     * @return les coordonnées de la cellule qui est à la position de la souris
     */
    private OffsetCoords getCoordsFromMousePosition(Vector3 mouseLoc) {
        //- cells.getTileWidth() / 2 et - cells.getTileHeight() / 2 sont là pour créer le décalage de l'origine.
        // Ce qui permet de retrouver les bonnes coordonnés
        // le (int)cells.getTileWidth() /2 correspond à la taille de l'hexagone (ie la longueur de la droite qui va du
        // centre vers une des pointes de l'hexagone
        OffsetCoords coords = TransformCoords.pixelToOffset((int)(mouseLoc.x - cells.getTileWidth() / 2),
                (int)(mouseLoc.y - cells.getTileHeight() / 2), (int)cells.getTileWidth() /2);
        coords.row = Math.abs(cells.getHeight()-1 - coords.row); //On inverse l'axe des ordonnées
        //Cela permet d'avoir le repère placé en haut à gauche avec les y allant vers le bas et les x vers la droite
        return coords;
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

    public Board getBoard() {
        return board;
    }
}
