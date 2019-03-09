package gui.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.utils.XmlReader;

import logic.board.Board;
import logic.board.District;
import logic.board.cell.Cell;
import logic.item.Capital;
import logic.item.Item;
import logic.item.Soldier;
import logic.item.level.SoldierLevel;
import logic.naturalDisasters.NaturalDisastersController;
import logic.player.Player;
import logic.player.ai.strategy.Strategy;
import logic.shop.Shop;
import org.mockito.Mockito;

/** Classe utilisé pour charger et convertir une map TMX en Board **/
//TODO NEED XML VALIDATOR FOR VALIDATE THE XML FILE

public class Map {

    private XmlReader.Element xmlElement;
    protected TiledMap map;
    protected HexagonalTiledMapRenderer tiledMapRenderer;
    protected TiledMapTileLayer cells;
    protected TiledMapTileLayer selectedCells;
    protected TiledMapTileSet tileSet;
    protected TiledMapTileSet tileSetSelected;
    protected Board board;
    protected int numberOfPlayers;
    protected Constructor<?> constructor;


    public Map(String worldName) {
        XmlReader xml = new XmlReader();
        //Si l'application qui à été lancé n'est pas une application Libgdx, alors on charge une fausse application
        //Pour charger toutes les variables contenus dans Gdx.files et autres.
        if(Gdx.files == null) {
            loadLibgdx();
        }
        //Création du parseur xml et on récupère le xml_element contenant les informations du monde
        xmlElement = xml.parse(Gdx.files.internal("worlds/" + worldName + ".xml"));
    }
    /**
     * Méthode qui permet de charger un monde du jeu
     * @param loadTmxRenderer true s'il faut charger le TmxRenderer, false sinon
     * @param naturalDisasters true s'il l'extension natural disasters est activé, false sinon
     * @return Le board initialisé si loadBoard == true, null sinon
     */
    public Board loadBoard(boolean loadTmxRenderer, boolean naturalDisasters, ArrayList<String> playersName) {
        if(cells == null) {
            generateTmxMap(xmlElement, loadTmxRenderer);
        }
            generateBoard(xmlElement, naturalDisasters, playersName);
            generateDistricts();
            generateItems(xmlElement);
        return board;
    }

    public void loadTmx() {
        generateTmxMap(xmlElement, true);
    }

    /**
     * Si aucune application libgdx n'a été crée, on initialise une fausse application
     * On n'a besoin de cette méthode pour le serveur par exemple.
     * Le serveur ne gérant pas de GUI, libgdx n'est pas initialisé et il est impossible d'avoir accès à Gdx.files
     * Source : https://www.badlogicgames.com/forum/viewtopic.php?f=11&t=17805#
     */
    private void loadLibgdx() {
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;
        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        config.preferencesDirectory = "core/assets";
        //Lancement de l'application et initialisation des paramètres Gdx.
        new HeadlessApplication(new EmptyApplication(), config);
    }

    /**
     * Méthode qui génère la map Tmx ainsi que le TmxRenderer à partir du fichier tmx
     * @param xmlElement l'xmlElement contenant les informations du monde
     * @param loadTmxRenderer true s'il faut charger le TmxRenderer et false sinon.
     *                        Cette variable permet au serveur de ne pas charger des variables inutiles
     *                        pour son fonctionnement.
     */
    private void generateTmxMap(XmlReader.Element xmlElement, boolean loadTmxRenderer) {
        String worldTmx = xmlElement.getAttribute("map");
        map = new TmxMapLoader().load("worlds/" + worldTmx + ".tmx");
        if(loadTmxRenderer) {
            tiledMapRenderer = new HexagonalTiledMapRenderer(map);
        }
        cells = (TiledMapTileLayer) map.getLayers().get("background"); //cellules
        selectedCells = (TiledMapTileLayer) map.getLayers().get("selectedTiles");
        tileSet = map.getTileSets().getTileSet("hex"); //le tileset des hexagones
        tileSetSelected = map.getTileSets().getTileSet("hexSelected");

        for (int i = 0; i < selectedCells.getWidth(); i++) {
            for (int j = 0; j < selectedCells.getHeight(); j++) {
                selectedCells.setCell(i,j, new TiledMapTileLayer.Cell());
            }
        }
    }

    /**
     * Méthode qui génère le board
     * @param xmlElement l'xmlElement contenant les informations du monde
     */
    private void generateBoard(XmlReader.Element xmlElement, boolean naturalDisasters, ArrayList<String> playersName) {
        numberOfPlayers = Integer.parseInt(xmlElement.getChildByName("players").getAttribute("number"));
        ArrayList<Player> players = new ArrayList<>();
        Player newPlayer;
        for (int i = 0; i < numberOfPlayers; i++) {
            if(playersName != null)
                newPlayer = new Player(playersName.get(i));
            else
                newPlayer = new Player();
            //Ajout de l'id du player
            newPlayer.setId(i+1); //Car i=0 correspond aux cellules neutres
            players.add(newPlayer);
        }
        int width = Integer.parseInt(xmlElement.getAttribute("width"));
        int height = Integer.parseInt(xmlElement.getAttribute("height"));
        if (naturalDisasters)
            board = new Board(width, height, players, new NaturalDisastersController(), new Shop());
        else
            board = new Board(width, height, players, new Shop());
    }

    /**
     * Méthode qui va initialiser les districts à partir de la map Tmx.
     */
    private void generateDistricts() {
        //TODO Méthode qui génère les district dans board
        for (int i = 0; i < cells.getWidth(); i++) {
            for (int j = 0; j < cells.getHeight(); j++) {
                //Note : le Math.abs(cells.getHeight()-1 - j) est utilisé ici car la map Tmx à son origine centré
                //En bas à gauche tandis que le board lui à son origine centré en haut à gauche.
                TiledMapTileLayer.Cell cell = cells.getCell(i, Math.abs(cells.getHeight()-1 - j));
                MapProperties properties = cell.getTile().getProperties();
                if(!(boolean) properties.get("available")) { //On change la cellule pour une cellule d'eau si la cellule n'est pas accesible
                    board.changeToWaterCell(i, j);
                }
                int nPlayer = (int) properties.get("player");
                if (nPlayer != 0) { //Si la cellule appartient à un joueur (car 0 est la valeur pour une cellule neutre
                    District district = new District(board.getPlayers().get(nPlayer - 1));
                    district.addCell(board.getCell(i,j));
                    board.addDistrict(district);
                    board.getCell(i, j).setDistrict(district);
                    board.checkMerge(board.getCell(i, j)); //On regarde si on ne peut pas merge deux districts
                    //Car les districts ne sont pas spécifiés dans le fichier xml du monde
                }
            }
        }
    }

    /**
     * Méthode qui génère les items dans le board à partir des informations du monde
     * @param xmlElement l'xmlElement contenant les informations du monde
     */
    private void generateItems(XmlReader.Element xmlElement) {
        XmlReader.Element items = xmlElement.getChildByName("items"); //Récupération de la section Items
        for (int i = 0; i < items.getChildCount(); i++) { //Itération sur l'ensemble des items
            XmlReader.Element item = items.getChild(i);
            //Ici on récupère la classe associé à l'item en utilisant la réflexion
            Class<?> itemClass = getClassFromString(item.getAttribute("type"));
            //Récupération de la cellule où il faut placer l'item
            Cell cell = board.getCell(Integer.parseInt(item.getAttribute("x")),
                    Integer.parseInt(item.getAttribute("y")));
            try {
                //Cas spécifique le constructeur de base ne suffit pas. Un soldat doit avoir un level
                if(itemClass.equals(Soldier.class)) {
                    Constructor<?> constructor = itemClass.getConstructor(SoldierLevel.class);
                    int soldierLevel = Integer.parseInt(item.getAttribute("level"));
                    Item newItem = null;
                    newItem = (Item) constructor.newInstance(SoldierLevel.values()[soldierLevel-1]);
                    cell.setItem(newItem);
                } else {
                    Constructor<?> constructor = itemClass.getConstructors()[0]; //Constructeur de base
                    cell.setItem((Item) constructor.newInstance());
                }
                if(itemClass.equals(Capital.class)) {
                	cell.getDistrict().removeCapital();
                    cell.getDistrict().setGold(Integer.parseInt(item.getAttribute("golds")));
                    cell.getDistrict().addCapital(cell);
                }
                //TODO
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Méthode qui récupère la class associé à un String
     * @param str le nom de la classe
     * @return la classe associé au String
     */
    private static Class<?> getClassFromString(String str) {
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
            Gdx.app.log("ERROR","A name of an Item is wrong in the xml file : " + str);
        }
        return null;
    }
    
    protected Class<?> getStrategy(String strategy){
    	try {
    		return Class.forName("logic.player.ai.strategy."+strategy);
    	}
    	catch(ClassNotFoundException e) {
    		System.out.println("ERROR : the strategy "+strategy+" didn't exist");
    		return null;
    	}
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

    public TiledMapTileLayer getSelectedCells() {
        return selectedCells;
    }

    public TiledMapTileSet getTileSetSelected() {
        return tileSetSelected;
    }
}
