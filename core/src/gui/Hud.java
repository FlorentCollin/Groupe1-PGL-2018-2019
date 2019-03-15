package gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import gui.graphics.screens.BasicScreen;
import gui.graphics.screens.animations.RectangleActor;
import gui.utils.Constants;
import logic.item.level.SoldierLevel;
import logic.player.Player;

import java.util.ArrayList;

/**
 * Classe représentant l'HUD in game qui contient notamment le shop ainsi que le nom du joueur actif.
 */
public class Hud extends Stage {
    private final DistrictInfo districtInfo;
    protected BasicScreen parent;
    protected Skin uiSkin;
    protected TextureAtlas itemSkin;
    private Shop shop;

    public Hud(BasicScreen parent, TextureAtlas itemSkin) {
        super();
        this.parent = parent;
        this.uiSkin = parent.getUiSkin();
        this.itemSkin = itemSkin;
        shop = new Shop();
        districtInfo = new DistrictInfo();
        districtInfo.addActorsToStage(this);
        shop.addActorsToStage(this);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void resize(int width, int height) {
        this.getViewport().update(width, height, true);
        this.getBatch().setProjectionMatrix(this.getCamera().combined);
    }

    public Shop getShop() {
        return shop;
    }

    public DistrictInfo getDistrictInfo() {
        return districtInfo;
    }

    /**
     * Classe interne qui correspond au Shop (où il est possible d'acheter des soldats)
     */
    public class Shop {
        private final Table table;
        public final Image soldierLvl1;
        public final Image soldierLvl2;
        public final Image soldierLvl3;
        public final Image soldierLvl4;
        private final Label soldierLvl1Price;
        private final Label soldierLvl2Price;
        private final Label soldierLvl3Price;
        private final Label soldierLvl4Price;
        private float width, height;

        public Shop() { //TODO Refactor for more soldier or others items
            //Définition de la taille du shop
            Image background = new Image(uiSkin.getDrawable("shop-background"));
            width = background.getWidth() * Constants.getRatioX(Gdx.graphics.getWidth());
            height = background.getHeight() * Constants.getRatioY(Gdx.graphics.getHeight());
            ArrayList<Image> goldImages = new ArrayList<>();
            for(int i = 0; i < 4; i++) {
                Image gold = new Image(itemSkin.createSprite("Coin"));
                gold.setScaling(Scaling.fit);
                goldImages.add(gold);
            }
            Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
            labelStyle.font = parent.getTextFont();
            soldierLvl1Price = new Label(Integer.toString(SoldierLevel.level1.getPrice()), labelStyle);
            soldierLvl2Price = new Label(Integer.toString(SoldierLevel.level2.getPrice()), labelStyle);
            soldierLvl3Price = new Label(Integer.toString(SoldierLevel.level3.getPrice()), labelStyle);
            soldierLvl4Price = new Label(Integer.toString(SoldierLevel.level4.getPrice()), labelStyle);
            //Création des objets accessibles dans le shop
            soldierLvl1 = new Image(new Sprite(itemSkin.createSprite("Soldier_lvl1_big")));
            soldierLvl2 = new Image(new Sprite(itemSkin.createSprite("Soldier_lvl2_big")));
            soldierLvl3 = new Image(new Sprite(itemSkin.createSprite("Soldier_lvl3_big")));
            soldierLvl4 = new Image(new Sprite(itemSkin.createSprite("Soldier_lvl4_big")));
            soldierLvl1.setScaling(Scaling.fit);
            soldierLvl2.setScaling(Scaling.fit);
            soldierLvl3.setScaling(Scaling.fit);
            soldierLvl4.setScaling(Scaling.fit);
            Table scrollTable = new Table();
            //Ajout des objets achetables dans le shop
            addSoldier(scrollTable, soldierLvl1Price, goldImages.get(0), soldierLvl1);
            addSoldier(scrollTable, soldierLvl2Price, goldImages.get(1), soldierLvl2);
            addSoldier(scrollTable, soldierLvl3Price, goldImages.get(2), soldierLvl3);
            addSoldier(scrollTable, soldierLvl4Price, goldImages.get(3), soldierLvl4);
            //TODO COMMENT
            ScrollPane scroller;
            scroller = new ScrollPane(scrollTable, uiSkin);
            scroller.setScrollingDisabled(true, false);
            scroller.setScrollbarsVisible(true);
            scroller.setFadeScrollBars(false);

            table = new Table(uiSkin);
            table.setBackground("shop-background");
            table.add(scroller).fillX().expand().align(Align.topLeft).padTop(3).padBottom(3);
            table.setSize(width, height);

        }

        private void addSoldier(Table scrollTable, Label soldierPrice, Image gold, Image soldier) {
            scrollTable.add(soldier).maxHeight(height/4).padRight(10).padLeft(10).colspan(2);
            scrollTable.row();
            scrollTable.add(soldierPrice).maxHeight(10).padLeft(5).padRight(5).padBottom(20);
            scrollTable.add(gold).maxHeight(height/8).padRight(1).padBottom(20);
            scrollTable.row();
        }

        public void addActorsToStage(Hud hud) {
            table.setPosition(hud.getWidth() - width, hud.getHeight() / 2 - height / 2);
            hud.addActor(table);
        }
    }

    public class DistrictInfo {

        private final float width, height;
        private final Table table;
        private Label currentPlayer;
        public Label goldLabel;

        public DistrictInfo() {
            //Définition de la taille du shop
            Image background = new Image(uiSkin.getDrawable("info-background"));
            width = background.getWidth() * Constants.getRatioX(Gdx.graphics.getWidth());
            height = background.getHeight() * Constants.getRatioY(Gdx.graphics.getHeight());
            Image gold = new Image(itemSkin.createSprite("Coin"));
            gold.setScaling(Scaling.fit);

            Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
            labelStyle.font = parent.getTextFont();
            goldLabel = new Label("", labelStyle);

            Image currentPlayerImage = new Image(itemSkin.createSprite("currentPlayer"));
            currentPlayerImage.setScaling(Scaling.fit);
            labelStyle.font = parent.getDefaultFontItalic();
            currentPlayer = new Label("", labelStyle);

            //TODO COMMENT
            table = new Table(uiSkin);
            table.setSize(width, height);
            table.setBackground("info-background");
            table.add(currentPlayerImage).maxWidth(50).padLeft(10).padRight(2);
            table.add(currentPlayer).expandX().pad(10).align(Align.left);
            table.add(goldLabel).expandX().pad(10).align(Align.right);
            table.add(gold).maxWidth(50).align(Align.right).pad(10);
        }

        private void addActorsToStage(Hud hud) {
            table.setPosition(hud.getWidth() - width, 0);
            hud.addActor(table);
        }

        public void setCurrentPlayer(Player player) {
            currentPlayer.setColor(player.getColor());
            currentPlayer.setText(player.getName());
        }
    }
}
