package gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import gui.graphics.screens.BasicScreen;
import gui.utils.Constants;
import logic.item.level.SoldierLevel;
import logic.player.Player;

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
        public final ImageButton soldierLvl1;
        public final ImageButton soldierLvl2;
        public final ImageButton soldierLvl3;
        public final ImageButton soldierLvl4;
        private final Label soldierPrice;
        private final Image gold;
        private final Table price;
        private float width, height;

        public Shop() { //TODO Refactor for more soldier or others items
            //Définition de la taille du shop
            Image background = new Image(uiSkin.getDrawable("shop-background"));
            width = background.getWidth() * Constants.getRatioX(Gdx.graphics.getWidth());
            height = background.getHeight() * Constants.getRatioY(Gdx.graphics.getHeight());
            gold = new Image(uiSkin.getDrawable("Coin"));
            gold.setScaling(Scaling.fit);
            gold.setVisible(false);

            Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
            labelStyle.font = parent.getTextFont();
            soldierPrice = new Label("", labelStyle);
            price = new Table();
            price.add(soldierPrice).expandX().pad(2).align(Align.left);
            price.add(gold).width(width/2-10).align(Align.right).pad(2);
            price.setSize(width, height / 8);

            //Création des objets accessibles dans le shop
            ImageButton.ImageButtonStyle buttonStyle = uiSkin.get(ImageButton.ImageButtonStyle.class);
            buttonStyle.imageUp = uiSkin.getDrawable("shop_soldier_lvl1");
            soldierLvl1 = new ImageButton(uiSkin.getDrawable("shop_soldier_lvl1"));
            soldierLvl2 = new ImageButton(uiSkin.getDrawable("shop_soldier_lvl2"));
            soldierLvl3 = new ImageButton(uiSkin.getDrawable("shop_soldier_lvl3"));
            soldierLvl4 = new ImageButton(uiSkin.getDrawable("shop_soldier_lvl4"));

            soldierLvl1.addListener(soldierListener(SoldierLevel.level1));
            soldierLvl2.addListener(soldierListener(SoldierLevel.level2));
            soldierLvl3.addListener(soldierListener(SoldierLevel.level3));
            soldierLvl4.addListener(soldierListener(SoldierLevel.level4));

            Table scrollTable = new Table();
            //Ajout des objets achetables dans le shop
            addSoldier(scrollTable, soldierLvl1);
            addSoldier(scrollTable, soldierLvl2);
            addSoldier(scrollTable, soldierLvl3);
            addSoldier(scrollTable, soldierLvl4);

            ScrollPane scroller;
            scroller = new ScrollPane(scrollTable, uiSkin);
            scroller.setScrollingDisabled(true, false);
            scroller.setScrollbarsVisible(true);
            scroller.setFadeScrollBars(false);

            table = new Table(uiSkin);
            table.setBackground("shop-background");
            table.add(scroller).padTop(3).padBottom(3);
            table.setSize(width, height);
            table.addListener(new ClickListener() {
                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    System.out.println("table exited");
                    soldierPrice.setText("");
                    gold.setVisible(false);
                }
            });

        }

        private void addSoldier(Table scrollTable, ImageButton soldier) {
            scrollTable.add(soldier).size(width-20, height/4).padRight(10).padLeft(10).colspan(2);
            scrollTable.row();
        }

        private InputListener soldierListener(SoldierLevel level) {
            return new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    soldierPrice.setText(level.getPrice());
                    gold.setVisible(true);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    soldierPrice.setText("");
                    gold.setVisible(false);
                }
            };
        }

        public void addActorsToStage(Hud hud) {
            table.setPosition(hud.getWidth() - width, hud.getHeight() / 2 - height / 2);
            price.setPosition(hud.getWidth() - width, hud.getHeight() / 2  + height/2 + 15);
            hud.addActor(table);
            hud.addActor(price);
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
            Image gold = new Image(uiSkin.getDrawable("Coin"));
            gold.setScaling(Scaling.fit);

            Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
            labelStyle.font = parent.getTextFont();
            goldLabel = new Label("", labelStyle);

            Image currentPlayerImage = new Image(uiSkin.getDrawable("currentPlayer"));
            currentPlayerImage.setScaling(Scaling.fit);
            labelStyle.font = parent.getDefaultFontItalic();
            currentPlayer = new Label("", labelStyle);

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
