package gui.graphics.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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

public class InGameScreen extends BasicScreen implements InputProcessor {

    private Map map;
    private Vector3 mouseLoc = new Vector3();
    private float worldWith;
    private float worldHeight;
    private TiledMapTileLayer cells;
    private Board board;
    private TextureAtlas itemsSkin;
    private Vector2 selectedCellPos = null;

    public InGameScreen(Slay parent, String mapName) {
        super(parent);
        itemsSkin = new TextureAtlas(Gdx.files.internal("items/items.atlas"));
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
        //TODO ne pas recréer le sprite à chaque render car cela est lourd il  vaut mieux le stocker en mémoire
        Cell[][] tab = board.getBoard();
        Sprite sprite = null;
        for (int i = 0; i < board.getColumns(); i++) {
            for (int j = 0; j < board.getRows(); j++) {
                if (tab[i][j].getItem() != null) {
                    Item item = tab[i][j].getItem();
                    if(item instanceof Soldier) {
                        switch(((Soldier) item).getLevel()) {
                            case level1:
                                sprite = itemsSkin.createSprite(item.getClass().getSimpleName() + "_lvl1");
                                break;
                            case level2:
                                sprite = itemsSkin.createSprite(item.getClass().getSimpleName() + "_lvl2");
                                break;
                            case level3:
                                sprite = itemsSkin.createSprite(item.getClass().getSimpleName() + "_lvl3");
                                break;
                            case level4:
                                sprite = itemsSkin.createSprite(item.getClass().getSimpleName() + "_lvl4");
                                break;
                        }
                    } else {
                        sprite = itemsSkin.createSprite(item.getClass().getSimpleName());
                    }
                        if(sprite != null) {
                            stage.getBatch().begin();
                            Vector2 pos = TransformCoords.hexToPixel(i, j+1, (int) cells.getTileWidth() / 2);
                            pos.y = Math.abs(worldHeight - pos.y); // inversion de l'axe y
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
        Gdx.input.setInputProcessor(this);
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
        return TransformCoords.pixelToOffset((int)(mouseLoc.x - cells.getTileWidth() / 2),
                (int)(mouseLoc.y - cells.getTileHeight() / 2), (int)cells.getTileWidth() /2);
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


    @Override
    public boolean keyDown(int keycode) {
        if(keycode == 66) { //ENTER
            board.nextPlayer();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        OffsetCoords coords = getCoordsFromMousePosition(getMouseLoc());
        if(cells.getCell(coords.col,coords.row) != null) {
            if(board.getSelectedCell() != null) {
                ArrayList<Cell> possibleMoves = board.possibleMove(board.getSelectedCell().getDistrict());
                for (Cell cell: possibleMoves) {
                    if(board.getCell(coords.col, coords.row) == cell) {
                        board.move(board.getCell(coords.col, coords.row));
                        board.resetSelectedCell();
                    }
                }
                    board.resetSelectedCell();
            } else {
                if(board.getCell(coords.col, coords.row).getDistrict() != null && board.getCell(coords.col, coords.row).getDistrict().getPlayer() == board.getActivePlayer())
                    board.setSelectedCell(board.getCell(coords.col, coords.row));
            }

//                ArrayList<Cell> moves = board.getCell(coords.col,coords.row).getDistrict().getCells();
//                for(Cell c : moves) {
//                    int [] position = board.getPosition(c);
//                    TiledMapTileLayer.Cell cell = cells.getCell(position[0], Math.abs(cells.getHeight()-1 - position[1]));
//                    cell.setTile(map.getTileSet().getTile(2));
//                }

        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {

        camera.translate(-Gdx.input.getDeltaX()*camera.zoom, Gdx.input.getDeltaY()*camera.zoom);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        if(amount == -1) {
            camera.zoom -= 0.1;
        } else {
            camera.zoom += 0.1;
        }
        return true;
    }
    public Board getBoard() {
        return board;
    }
}
