package gui.graphics.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FillViewport;
import communication.MessageListener;
import communication.MessageSender;
import communication.Messages.NaturalDisasterMessage;
import communication.Messages.PlayMessage;
import communication.Messages.ShopMessage;
import communication.Messages.TextMessage;
import communication.OnlineMessageSender;
import gui.Hud;
import gui.app.Slay;
import gui.utils.Constants;
import gui.utils.Language;
import gui.utils.Map;
import logic.Coords.OffsetCoords;
import logic.Coords.TransformCoords;
import logic.board.Board;
import logic.board.cell.Cell;
import logic.board.cell.*;
import logic.item.Item;
import logic.item.Soldier;
import logic.item.level.SoldierLevel;
import logic.player.Player;
import roomController.Room;

import java.util.ArrayList;
import java.util.List;

public class InGameScreen extends MenuScreen implements InputProcessor {

    private final ImageButton arrowButton;
    private Map map;
    private Vector3 mouseLoc = new Vector3();
    private float worldHeight;
    private TiledMapTileLayer cells;
    private TiledMapTileLayer selectedLayer;
    private final Board board;
    private TextureAtlas itemsSkin;
    private List<Cell> selectedCells = new ArrayList<>();
    private FillViewport fillViewport;
    private Hud hud;
    private ArrayList<Integer> playerNumber;
    private boolean alreadyShow = false;

    private MessageListener messageListener;
    private MessageSender messageSender;
    private Room room;

    public InGameScreen(Slay parent, String mapName, Board board, MessageSender messageSender) {
        super(parent);
        this.messageSender = messageSender;
        this.board = board;
        playerNumber = null;
        //Chargement du TmxRenderer et des textures
        itemsSkin = new TextureAtlas(Gdx.files.internal("items/items.atlas"));
        map = new Map(mapName);
        map.loadTmx();
        cells = map.getCells();
        selectedLayer = map.getSelectedCells();
        //Calcule de la grandeur de la carte
        worldHeight = cells.getHeight() * cells.getTileHeight() + cells.getTileHeight() / 2;

        hud = new Hud(this, itemsSkin, board.isNaturalDisasters());
        arrowButton = generateArrowButton();
        arrowButton.setX(25 * ratio);
        arrowButton.setY(Gdx.graphics.getHeight() - 75 * Constants.getRatioY(Gdx.graphics.getHeight()));
        arrowButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                arrowListener();
            }
        });
        hud.addActor(arrowButton);
        Hud.Shop shop = hud.getShop();
        shop.soldierLvl1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                messageSender.send(new ShopMessage(new Soldier(SoldierLevel.level1)));}
        });
        shop.soldierLvl2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                messageSender.send(new ShopMessage(new Soldier(SoldierLevel.level2)));}
        });
        shop.soldierLvl3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                messageSender.send(new ShopMessage(new Soldier(SoldierLevel.level3)));}
        });
        shop.soldierLvl4.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                messageSender.send(new ShopMessage(new Soldier(SoldierLevel.level4)));}
        });
        hud.getDistrictInfo().endTurn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("clicked");
                for(int i = 0; i < 200; i++) {
                    messageSender.send(new TextMessage("nextPlayer"));

                }
            }
        });
        Hud.DisastersInfo disaster = hud.getDisastersInfo();
        if(disaster != null) {
            disaster.blizzard.addListener(disasterListener("blizzard", disaster.blizzard));
            disaster.drought.addListener(disasterListener("drought", disaster.drought));
            disaster.forestFire.addListener(disasterListener("forestFire", disaster.forestFire));
            disaster.landErosion.addListener(disasterListener("landErosion", disaster.landErosion));
            disaster.tsunami.addListener(disasterListener("tsunami", disaster.tsunami));
            disaster.volcanicEruption.addListener(disasterListener("volcanicEruption", disaster.volcanicEruption));
        }
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(this);
        multiplexer.addProcessor(hud);
        Gdx.input.setInputProcessor(multiplexer);
        Gdx.graphics.setResizable(false);
    }

    public InGameScreen(Slay parent, String mapName, Board board, MessageSender messageSender, MessageListener messageListener) {
        this(parent, mapName, board, messageSender);
        this.messageListener = messageListener;
        this.playerNumber = messageListener.getPlayerNumber();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        change();
        checkInput();
        synchronized (board) {
            if (board.getSelectedCell() != null) {
                selectCells(board.possibleMove(board.getSelectedCell()));
            }
            if (board.getShop().getSelectedItem() != null && board.getSelectedCell() != null) {
                selectCells(board.possibleMove(board.getSelectedCell().getDistrict()));
            }
        }
        if (board.getWinner() != null && !alreadyShow) {
            showEndDialog(board.getWinner());
            alreadyShow = true;
        }
        map.getTiledMapRenderer().setView(camera);
        map.getTiledMapRenderer().render(); //Rendering des cellules
        renderItems();
        if (board.getWinner() != board.getGodPlayer()) {
            hud.getDistrictInfo().setCurrentPlayer(board.getActivePlayer());
        }
        hud.getViewport().apply();
        hud.act(delta);
        hud.draw();
    }

    private void checkInput() {
        Integer[] values = parent.getUserShortcuts().getShortcut("Move camera up");
        if(Gdx.input.isKeyPressed(values[0]) || Gdx.input.isKeyPressed(values[1])) {
            camera.translate(0, 15);
        }
        values = parent.getUserShortcuts().getShortcut("Move camera down");
        if(Gdx.input.isKeyPressed(values[0]) || Gdx.input.isKeyPressed(values[1])) {
            camera.translate(0, -15);
        }
        values = parent.getUserShortcuts().getShortcut("Move camera right");
        if(Gdx.input.isKeyPressed(values[0]) || Gdx.input.isKeyPressed(values[1])) {
            camera.translate(15, 0);
        }
        values = parent.getUserShortcuts().getShortcut("Move camera left");
        if(Gdx.input.isKeyPressed(values[0]) || Gdx.input.isKeyPressed(values[1])) {
            camera.translate(-15, 0);
        }
    }

    /**
     * Permet de récupérer une tile en fonction de l'id joueur
     * @param id l'id du joueur
     * @return la tile correspondante
     */
    private TiledMapTile getTile(int id, boolean isLava, boolean isWater) {
    	if(isLava) {
    		return map.getTileSet().getTile(9);
    	}
    	if(isWater) {
    		return map.getTileSet().getTile(5);
    	}
        for(int i = 0; i < map.getTileSet().size(); i++) {
            TiledMapTile tile = map.getTileSet().getTile(i+1); //Le i+1 vient du first gid de tiled
            if((int)tile.getProperties().get("player") == id && (boolean)tile.getProperties().get("available")) {
                return tile;
            }
        }
        return map.getTileSet().getTile(5); //Parce que sinon ça ne veut pas prendre la cellule bleue :'(
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        hud.resize(width, height);

    }

    private void renderItems() {
        //TODO ne pas recréer le sprite à chaque render car cela est lourd il  vaut mieux le stocker en mémoire
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
                        if(!item.canMove()) { //On change l'opacité du sprite si l'item ne peut plus bouger
                            stage.getBatch().setColor(1,1,1,0.5f);
                        } else {
                            stage.getBatch().setColor(1,1,1,1);
                        }
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
        fillViewport = new FillViewport(1280, 720, camera);
        fillViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(fillViewport);
    }

    private Vector3 getHudMouseLoc() {
        mouseLoc.x = Gdx.input.getX();
        mouseLoc.y = Gdx.input.getY();
        return hud.getViewport().unproject(mouseLoc);
    }
    private Vector3 getMouseLoc() {
        mouseLoc.x = Gdx.input.getX();
        mouseLoc.y = Gdx.input.getY();
        fillViewport.unproject(mouseLoc); //Récupération des coordonnées de la souris sur la map
        mouseLoc.y = Math.abs(worldHeight -mouseLoc.y); //On inverse l'axe des ordonnées
        //Cela permet d'avoir le repère placé en haut à gauche avec les y allant vers le bas et les x vers la droite
        return mouseLoc;
    }
    /**
     * Retourne les coordonnées de la cellule qui se trouve à la position de la souris
     * @param mouseLoc position de la souris
     * @return les coordonnées de la cellule qui est à la position de la souris
     */
    private OffsetCoords getCoordsFromMousePosition(Vector3 mouseLoc) {
        //- cells.getTileWidth() / 2 et - cells.getTileHeight() / 2 sont là pour créer le décalage de l'origine.
        // Ce qui permet de retrouver les bonnes coordonnés
        // le (int)cells.getTileWidth() /2 correspond à la taille de l'hexagone (ie la longueur de la droite qui va du
        // centre vers une des pointes de l'hexagon
        return TransformCoords.pixelToOffset((int)(mouseLoc.x - cells.getTileWidth() / 2),
                (int)(mouseLoc.y - cells.getTileHeight() / 2), (int)cells.getTileWidth() /2);
    }


    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if(messageSender instanceof OnlineMessageSender) {
            messageListener.stopRunning();
            ((OnlineMessageSender) messageSender).close();
        }
        super.dispose();
    }


    @Override
    public boolean keyDown(int keycode) {
        if(parent.getUserShortcuts().isShortcut("End turn", keycode)) {
            messageSender.send(new TextMessage("nextPlayer"));
        } else if(parent.getUserShortcuts().isShortcut("Menu", keycode)) {
            arrowListener();
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
        if(hud.hit(getHudMouseLoc().x, getHudMouseLoc().y, true) != null)
            return false;
        OffsetCoords boardCoords = getCoordsFromMousePosition(getMouseLoc());
        if(boardCoords.col >= 0 && boardCoords.col < board.getColumns()
                && boardCoords.row >= 0 && boardCoords.row < board.getRows()) {
            messageSender.send(new PlayMessage(boardCoords.col, boardCoords.row));
            System.out.println(board.getCell(boardCoords.col, boardCoords.row).getClass().getSimpleName()+", "+board.getCell(boardCoords.col, boardCoords.row).getDistrict());
            if(board.getCell(boardCoords.col, boardCoords.row).getDistrict() != null) {
            	System.out.println(board.getCell(boardCoords.col, boardCoords.row).getDistrict().getCapital());
            	System.out.println(board.getCell(boardCoords.col, boardCoords.row).getDistrict().getCapital().getX()+" "+board.getCell(boardCoords.col, boardCoords.row).getDistrict().getCapital().getY());
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if(hud.hit(getHudMouseLoc().x, getHudMouseLoc().y, true) != null)
            return false;
        camera.translate(-Gdx.input.getDeltaX()*camera.zoom, Gdx.input.getDeltaY()*camera.zoom);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        if (hud.hit(screenX, screenY, true) != null)
            return false;
        //Cette méthode va lorsqu'on passe la souris sur un district montrer ce district
        synchronized (board) {
            if (board.getSelectedCell() == null) { // On vérifie qu'aucune cellule n'a été sélectionnée pour une action dans le board
                // car sinon on ne doit pas montrer le district
                OffsetCoords boardCoords = getCoordsFromMousePosition(getMouseLoc());
                //On vérifie que les coordonnées sont bien dans les limites de la cartes
                if (boardCoords.col >= 0 && boardCoords.col < board.getColumns()
                        && boardCoords.row >= 0 && boardCoords.row < board.getRows()) {
                    Cell cell = board.getCell(boardCoords.col, boardCoords.row);
                    if (cell != null) {
                        // Ne s'applique que si la case appartient au joueur
                        // Ainsi il voit directement avec quelles cases il peut interagir
                        if (playerNumber == null || playerNumber.contains(board.getActivePlayerNumber())) {
                        	if(cell.getDistrict() != null) {
//                            if (cell.getDistrict() != null && cell.getDistrict().getPlayer().getId() == board.getActivePlayer().getId()) {
                                hud.getDistrictInfo().goldLabel.setText(cell.getDistrict().getGold());
                                cells.setOpacity(0.9f);
                                selectCells(cell.getDistrict().getCells());
                            }
                        }
                        if(cell.getDistrict() == null) {
                            hud.getDistrictInfo().goldLabel.setText("");
                            cells.setOpacity(1f);
                            unselectCells();
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        if(hud.hit(getHudMouseLoc().x, getHudMouseLoc().y, true) != null)
            return false;
        if(amount == -1) {
            if(camera.zoom - 0.2 >= 0) {
                camera.zoom -= 0.2;
            }
        } else {
            camera.zoom += 0.2;
        }
        return true;
    }

    public Board getBoard() {
        return board;
    }

    private void selectCells(List<Cell> cellsArray) {
        unselectCells();
        if (playerNumber == null || playerNumber.contains(board.getActivePlayerNumber())) {
            selectedCells = new ArrayList<>(cellsArray);
            for (Cell cell : selectedCells) {
                // On récupère les coordonnées dans la mapTmx car celles-ci sont différentes des coordonnées dans le board
                OffsetCoords tmxCoords = boardToTmxCoords(new OffsetCoords(cell.getX(), cell.getY()));
                TiledMapTileLayer.Cell tmxSelectedCell = selectedLayer.getCell(tmxCoords.col, tmxCoords.row);
                // On change la tile (l'image) de la cellule à sélectionner.
                tmxSelectedCell.setTile(map.getTileSetSelected().iterator().next());
            }
        }
    }

    private void unselectCells() {
        for (Cell selectedCell : selectedCells) {

            // On récupère les coordonnées dans la mapTmx car celles-ci sont différentes des coordonnées dans le board
            OffsetCoords tmxCoords = boardToTmxCoords(new OffsetCoords(selectedCell.getX(),selectedCell.getY()));
            // Récupération de la cellule dans la mapTmx

            TiledMapTileLayer.Cell tmxSelectedCell = selectedLayer.getCell(tmxCoords.col, tmxCoords.row);
            // On change la tile (l'image) de la cellule à sélectionner.
            tmxSelectedCell.setTile(null);
        }
        selectedCells = new ArrayList<>();
    }

    /**
     * Modifie la couleur des tiles en fonction du joueur la possédant
     */
    private void change() {
        OffsetCoords tmxCoords;
        TiledMapTileLayer.Cell tmxCell;
        int playerId;
        TiledMapTile tile;
        synchronized (board) {
            for(Cell cell : board.getModificatedCells()) {
                playerId = 0;
                if(cell.getDistrict() != null) {
                    playerId = cell.getDistrict().getPlayer().getId();
                }
                if(cell instanceof DroughtCell) {
                    changeTo(cell.getX(), cell.getY(), "drought");
                } else if(cell instanceof BlizzardCell) {
                    changeTo(cell.getX(), cell.getY(), "blizzard");
                } else if(cell instanceof LandCell) {
                    changeTo(cell.getX(), cell.getY(), null);
                }
                if(cell instanceof LavaCell) {
                	tile = getTile(playerId, true, false);
                }
                else if(cell instanceof WaterCell) {
                	tile = getTile(playerId, false, true);
                }
                else {
                	tile = getTile(playerId, false, false);
                }
                tmxCoords = boardToTmxCoords(new OffsetCoords(cell.getX(), cell.getY()));
	            tmxCell = cells.getCell(tmxCoords.col, tmxCoords.row);
	            tmxCell.setTile(tile);
            }
            board.getModificatedCells().clear();
        }
    }

    private void changeTo(int x, int y, String name) {
        OffsetCoords tmxCoords = boardToTmxCoords(new OffsetCoords(x, y));
        TiledMapTileLayer disasterCell = map.getDisasterCells();
        TiledMapTileSet disasterTileSet = map.getTileSetDisaster();
        if(name == null) {
            disasterCell.getCell(tmxCoords.col, tmxCoords.row).setTile(null);
        } else {
            for (TiledMapTile tile : disasterTileSet) {
                if (tile.getProperties().get("name").equals(name)) {
                    disasterCell.getCell(tmxCoords.col, tmxCoords.row).setTile(tile);
                    break;
                }
            }
        }
    }

    private OffsetCoords boardToTmxCoords(OffsetCoords boardCoords) {
        return new OffsetCoords(boardCoords.col, Math.abs(cells.getHeight()-1 - boardCoords.row));
    }


    private void arrowListener() {
        Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = textFont;
        Window.WindowStyle windowStyle = uiSkin.get(Window.WindowStyle.class);
        windowStyle.titleFont = textFont;
        TextButton.TextButtonStyle buttonStyle = uiSkin.get("checked", TextButton.TextButtonStyle.class);
        buttonStyle.font = textFont;
        Dialog dialog = new Dialog("", windowStyle);
        Table table = dialog.getContentTable();
        //Ajout des différents éléments au dialogue
        table.align(Align.topLeft);
        table.add(new Label(Language.bundle.get("quitGame"), labelStyle)).padTop(10).padLeft(10).row();
        TextButton cancel = new TextButton(Language.bundle.get("no"), buttonStyle);
        //Ajout d'un listener au bouton cancel (qui cache le dialogue)
        cancel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                dialog.hide();
            }
        });
        TextButton menuButton = new TextButton(Language.bundle.get("yes"), buttonStyle);
        //Ajout d'un listener au bouton Join Server qui permet au client de rejoindre le serveur indiqué dans l'ipField
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.changeScreen(MainMenuScreen.class);
                dispose();
                dialog.hide();
            }
        });
        table.add(cancel).expandY().pad(10).padRight(25).padLeft(25);
        table.add(menuButton).expandY().pad(10).padRight(25).padLeft(25).align(Align.left);
        dialog.show(hud);
    }

    private void showEndDialog(Player winner) {
        Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = textFont;
        if(winner != board.getGodPlayer()) {
        	labelStyle.fontColor = winner.getColor();
        }
        Window.WindowStyle windowStyle = uiSkin.get(Window.WindowStyle.class);
        windowStyle.titleFont = textFont;
        TextButton.TextButtonStyle buttonStyle = uiSkin.get("checked", TextButton.TextButtonStyle.class);
        buttonStyle.font = textFont;
        Dialog dialog = new Dialog("", windowStyle);
        Table table = dialog.getContentTable();
        table.align(Align.topLeft);
        if(winner != board.getGodPlayer()) {
        	table.add(new Label(winner.getName() + " " + Language.bundle.get("winner"), labelStyle)).align(Align.center).pad(50);
        }
        else {
        	table.add(new Label("Nature is the winner, humans are dead", labelStyle)).align(Align.center).pad(50);
        }
        TextButton returnButton = new TextButton(Language.bundle.get("returnToMainMenu"), buttonStyle);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.changeScreen(MainMenuScreen.class);
                dispose();
                dialog.hide();
            }
        });
        table.add(returnButton).expandY().pad(10).padRight(25).padLeft(25).align(Align.center);
        dialog.show(hud);
    }

    private ChangeListener disasterListener(String name, Slider slider) {
        return new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                messageSender.send(new NaturalDisasterMessage(name, (int)slider.getValue()));
            }
        };
    }
}
