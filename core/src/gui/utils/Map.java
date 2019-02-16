package gui.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.utils.XmlReader;
import logic.board.Board;
import logic.naturalDisasters.NaturalDisastersController;
import logic.player.Player;
import logic.shop.Shop;

/** Classe utilisé pour charger et convertir une map TMX en Board **/

public class Map {

    private TiledMap map;
    private HexagonalTiledMapRenderer tiledMapRenderer;
    private TiledMapTileLayer cells;
    private TiledMapTileSet tileSet;
    private Board board;
    private int numberOfPlayers;

    public  Board load(String worldName) {
        XmlReader xml = new XmlReader();
        XmlReader.Element xml_element = xml.parse(Gdx.files.internal("worlds/" + worldName + ".xml"));
        generateTmxMap(xml_element);
        generateBoard(xml_element);
        return board;
    }
    private void generateTmxMap(XmlReader.Element xmlElement) {
        String worldTmx = xmlElement.getAttribute("map");
        map = new TmxMapLoader().load("worlds/" + worldTmx + ".tmx");
        tiledMapRenderer = new HexagonalTiledMapRenderer(map);
        cells = (TiledMapTileLayer) map.getLayers().get("background"); //cellules
        tileSet = map.getTileSets().getTileSet("hex");
    }

    private void generateBoard(XmlReader.Element xmlElement) {
        numberOfPlayers = Integer.parseInt(xmlElement.getChildByName("players").getAttribute("number"));
        Player[] players = new Player[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            players[i] = new Player();
        }
        board = new Board(cells.getWidth(), cells.getHeight(), players, new NaturalDisastersController(), new Shop());
    }

    private void generateDistricts() {
        //TODO Méthode qui génère les district dans board
        for (int i = 0; i < cells.getWidth(); i++) {
            for (int j = 0; j < cells.getHeight() ; j++) {
                TiledMapTileLayer.Cell cell = cells.getCell(i,j);
                MapProperties properties = cell.getTile().getProperties();
                int nPlayer = (int)properties.get("player");
                switch (nPlayer) {
                    case 1:
                        
                        break;
                }
            }

        }
    }

    private void generateItems(XmlReader.Element xmlElement) {
        XmlReader.Element items = xmlElement.getChildByName("items");
        for (int i = 0; i < items.getChildCount(); i++) {
            XmlReader.Element item = items.getChild(i);

        }
    }

    public static Class<?> getClassFromString(String str) {
        /*
         * Ici on va utiliser le principe de réflexion Ce principe va nous permettre de
         * trouver une class à partir d'un String Et donc d'éviter de devoir placer un
         * switch(str) qui aurait du énumérer tous les éléments possible
         *  Source :
         * https://stackoverflow.com/questions/22439436/loading-a-class-from-a-different
         * -package
         */
        try {
            return Class.forName("logic.item." + str); // Comme les Class Item sont dans un autre package on doit
            // indiquer où les trouver
        } catch (ClassNotFoundException e) {
            //TODO
            System.out.println("ERROR : A name of an Item is wrong in the xml file : " + str);
        }
        return null;
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
