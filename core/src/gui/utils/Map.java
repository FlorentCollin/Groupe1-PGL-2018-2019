package gui.utils;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import logic.board.Board;
import logic.board.District;
import logic.naturalDisasters.NaturalDisastersController;
import logic.player.Player;
import logic.shop.Shop;

/** Classe utilisé pour charger et convertir une map TMX en Board **/

public class Map {

    private TiledMap map;
    private HexagonalTiledMapRenderer tiledMapRenderer;
    private TiledMapTileLayer cells;
    private TiledMapTileSet tileSet;

    public  Board load(String mapName) {
        map = new TmxMapLoader().load("maps/" + mapName);
        tiledMapRenderer = new HexagonalTiledMapRenderer(map);
        cells = (TiledMapTileLayer) map.getLayers().get("background"); //cellules
        tileSet = map.getTileSets().getTileSet("hex");
        Player[] players = new Player[2];
        Player p1 = new Player();
        Player p2 = new Player();
        players[0] = p1;
        players[1] = p2;
        Board board = new Board(cells.getWidth(), cells.getHeight(), players, new NaturalDisastersController(), new Shop());
        return board;
    }

    public TiledMap getMap() {
        return map;
    }

    public HexagonalTiledMapRenderer getTiledMapRenderer() {
        return tiledMapRenderer;
    }


    public TiledMapTileSet getTileSet() {
        return tileSet;
    }

    public TiledMapTileLayer getCells() {
        return cells;
    }
}
