package gui.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.XmlReader;

import logic.board.Board;
import logic.board.District;
import logic.naturalDisasters.NaturalDisastersController;
import logic.player.Player;
import logic.player.ai.Strategy;
import logic.shop.Shop;

public class MyMap extends Map{
//	private TiledMap map;
//	private HexagonalTiledMapRenderer tiledMapRenderer;
//	private TiledMapTileLayer cells;
//	private TiledMapTileSet tileSet;
//	private Board board;
//	private int numberOfPlayer;
//	private Constructor<?> constructor;
	private ArrayList<Player> players;
	
	public MyMap() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Board load(String worldName) {
		XmlReader xml = new XmlReader();
		XmlReader.Element xml_element = xml.parse(Gdx.files.internal("worlds/" + worldName + ".xml"));
		generateTmxMap(xml_element);
		players = new ArrayList<>();
		board = new Board(cells.getWidth(), cells.getHeight(), players, new NaturalDisastersController(), new Shop());
		generate(xml_element);
		generateItems(xml_element);
		board.checkCapitals();
		return board;
	}
	
//	private void generateTmxMap(XmlReader.Element xmlElement) {
//        String worldTmx = xmlElement.getAttribute("map");
//        map = new TmxMapLoader().load("worlds/" + worldTmx + ".tmx");
//        tiledMapRenderer = new HexagonalTiledMapRenderer(map);
//        cells = (TiledMapTileLayer) map.getLayers().get("background"); //cellules
//        tileSet = map.getTileSets().getTileSet("hex");
//    }
	
	private void generate(XmlReader.Element xmlElement) {
		TiledMapTileLayer.Cell cell;
		MapProperties properties;
		int nPlayer;
		boolean isAI;
		boolean available;
		District district;
		Strategy strategy = null;
		Class<?> strategyClass;
		for(int i=0; i<cells.getWidth(); i++) {
			for(int j=0; j<cells.getHeight(); j++) {
				cell = cells.getCell(i,  Math.abs(cells.getHeight()-1-j));
				properties = cell.getTile().getProperties();
				nPlayer = (int) properties.get("player");
				available = (boolean) properties.get("available");
				isAI = (boolean) properties.get("isAI");
				if(nPlayer != 0) {
					if(players.size() < nPlayer) {
						for(int k = players.size(); k<nPlayer; k++) {
							players.add(new Player());
						}
					}
					district = new District(players.get(nPlayer-1));
					district.addCell(board.getCell(i, j));
					board.getCell(i, j).setDistrict(district);
					board.addDistrict(district);
					board.checkMerge(board.getCell(i, j));
				}
				if(nPlayer == 0 && !available) {
					board.changeToWaterCell(i, j);
				}
				if(isAI) {
					strategyClass = getStrategy(String.valueOf(properties.get("strategy")));
					try {
						constructor = strategyClass.getConstructor();
					} catch (NoSuchMethodException | SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						strategy = (Strategy) constructor.newInstance();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					board.changeToAI(nPlayer-1, strategy);
				}
			}
		}
	}
	
//	 private void generateItems(XmlReader.Element xmlElement) {
//	        XmlReader.Element items = xmlElement.getChildByName("items");
//	        for (int i = 0; i < items.getChildCount(); i++) {
//	            XmlReader.Element item = items.getChild(i);
//	            Class<?> itemClass = getClassFromString(item.getAttribute("type"));
//	            Cell cell = board.getCell(Integer.parseInt(item.getAttribute("x")),
//	                    Integer.parseInt(item.getAttribute("y")));
//	            try {
//	                if(itemClass.equals(Soldier.class)) {
//	                    Constructor<?> constructor = itemClass.getConstructor(SoldierLevel.class);
//	                    int soldierLevel = Integer.parseInt(item.getAttribute("level"));
//	                    Item newItem = null;
//	                    newItem = (Item) constructor.newInstance(SoldierLevel.values()[soldierLevel-1]);
//	                    cell.setItem(newItem);
//	                } else {
//	                    Constructor<?> constructor = itemClass.getConstructors()[0]; //Constructeur de base
//	                    cell.setItem((Item) constructor.newInstance());
//	                }
//	                //TODO
//	            } catch (InstantiationException e) {
//	                e.printStackTrace();
//	            } catch (IllegalAccessException e) {
//	                e.printStackTrace();
//	            } catch (InvocationTargetException e) {
//	                e.printStackTrace();
//	            } catch (NoSuchMethodException e) {
//	                e.printStackTrace();
//	            }
//	            if(itemClass.equals(Capital.class)) {
//	                cell.getDistrict().setGold(Integer.parseInt(item.getAttribute("golds")));
//	            }
//	        }
//	    }
//	 
//	 public static Class<?> getClassFromString(String str) {
//	        /*
//	         * Ici on va utiliser le principe de réflexion Ce principe va nous permettre de
//	         * trouver une class à partir d'un String Et donc d'éviter de devoir placer un
//	         * switch(str) qui aurait du énumérer tous les éléments possible
//	         *  Source :
//	         * https://stackoverflow.com/questions/22439436/loading-a-class-from-a-different
//	         * -package
//	         */
//	        try {
//	            return Class.forName("logic.item." + str); // Comme les Class Item sont dans un autre package on doit
//	            // indiquer où les trouver
//	        } catch (ClassNotFoundException e) {
//	            //TODO
//	            System.out.println("ERROR : A name of an Item is wrong in the xml file : " + str);
//	        }
//	        return null;
//	    }
//	
//	private Class<?> getStrategy(String strategy){
//    	try {
//    		return Class.forName("logic.player.ai."+strategy);
//    	}
//    	catch(ClassNotFoundException e) {
//    		System.out.println("ERROR : the strategy "+strategy+" didn't exist");
//    		return null;
//    	}
//    }

}
