package gui.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
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
import logic.player.ai.Strategy;
import logic.shop.Shop;

/** Classe utilisé pour charger et convertir une map TMX en Board **/

public class Map {

    private TiledMap map;
    private HexagonalTiledMapRenderer tiledMapRenderer;
    private TiledMapTileLayer cells;
    private TiledMapTileSet tileSet;
    private Board board;
    private int numberOfPlayers;
    private Constructor<?> constructor;

    public  Board load(String worldName) {
        XmlReader xml = new XmlReader();
        XmlReader.Element xml_element = xml.parse(Gdx.files.internal("worlds/" + worldName + ".xml"));
        generateTmxMap(xml_element);
        generateBoard(xml_element);
        generateDistricts();
        generateItems(xml_element);
        checkCapitals();
        addWaterCells(xml_element);
        checkAI(xml_element);
        return board;
    }
    
    private void checkAI(XmlReader.Element xmlElement) {
    	XmlReader.Element ais = xmlElement.getChildByName("ais");
    	for(int i = 0; i < ais.getChildCount(); i++) {
    		XmlReader.Element ai = ais.getChild(i);
    		int nPlayer = Integer.parseInt(ai.getAttribute("soldierNumber"));
    		Class<?> strategyClass = getStrategy(ai.getAttribute("strategy"));
    		try {
				constructor = strategyClass.getConstructor();
			} catch (NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		Strategy strategy = null;
			try {
				strategy = (Strategy) constructor.newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		board.changeToAI(nPlayer, strategy);
    	}
    }
    
    private void addWaterCells(XmlReader.Element xmlElement) {
    	XmlReader.Element waterCells = xmlElement.getChildByName("waterCells");
    	for(int i = 0; i < waterCells.getChildCount(); i++) {
    		XmlReader.Element waterCell = waterCells.getChild(i);
    		int x = Integer.parseInt(waterCell.getAttribute("x"));
    		int y = Integer.parseInt(waterCell.getAttribute("y"));
    		board.changeToWaterCell(x, y);
    	}
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
        ArrayList<Player> players = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new Player());
        }
        board = new Board(cells.getWidth(), cells.getHeight(), players, new NaturalDisastersController(), new Shop());
    }

    private void generateDistricts() {
        //TODO Méthode qui génère les district dans board
        for (int i = 0; i < cells.getWidth(); i++) {
            for (int j = 0; j < cells.getHeight(); j++) {
                TiledMapTileLayer.Cell cell = cells.getCell(i, Math.abs(cells.getHeight()-1 - j));
                MapProperties properties = cell.getTile().getProperties();
                int nPlayer = (int) properties.get("player");
                if (nPlayer != 0) { //Si la cellule appartient à un joueur (car 0 est la valeur pour une cellule neutre
                    District district = new District(board.getPlayers().get(nPlayer - 1));
                    district.addCell(board.getCell(i,j));
                    board.addDistrict(district);
                    board.getCell(i, j).setDistrict(district);
                    board.checkMerge(board.getCell(i, j));
                }
            }
        }
    }
    
    /**
     * Ajout aux districts leurs capitals respectives
     * */
    private void checkCapitals() {
    	for(int i=0; i<board.getColumns(); i++) {
    		for(int j=0; j<board.getRows(); j++) {
    			if(board.getCell(i, j).getItem() instanceof Capital) {
    				board.getCell(i, j).getDistrict().addCapital(board.getCell(i, j));
    			}
    		}
    	}
    }

    private void generateItems(XmlReader.Element xmlElement) {
        XmlReader.Element items = xmlElement.getChildByName("items");
        for (int i = 0; i < items.getChildCount(); i++) {
            XmlReader.Element item = items.getChild(i);
            Class<?> itemClass = getClassFromString(item.getAttribute("type"));
            Cell cell = board.getCell(Integer.parseInt(item.getAttribute("x")),
                    Integer.parseInt(item.getAttribute("y")));
            try {
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
            if(itemClass.equals(Capital.class)) {
                cell.getDistrict().setGold(Integer.parseInt(item.getAttribute("golds")));
            }
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
    
    private Class<?> getStrategy(String strategy){
    	try {
    		return Class.forName("logic.player.ai."+strategy);
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
}
