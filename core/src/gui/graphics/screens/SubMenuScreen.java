package gui.graphics.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import gui.app.Slay;

import static gui.graphics.screens.animations.Animations.ANIMATION_DURATION;
import static gui.graphics.screens.animations.Animations.slideFromRight;
import static gui.graphics.screens.animations.Animations.slideToRight;

/**
 * Classe abstraite qui est parent des sous-menus comme les raccourcis où les paramètres
 */
public abstract class SubMenuScreen extends MenuScreen {


    protected final HorizontalGroup menuNameGroup;
    protected final ImageButton arrowButton;

    public SubMenuScreen(Slay parent, Stage stage, String menuName) {
        super(parent, stage);
        menuNameGroup = generateMenuNameGroup(menuName);
        stage.addActor(menuNameGroup);
        arrowButton = generateArrowButton();
        stage.addActor(arrowButton);
        //Listener pour revenir au menu principal
        arrowButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.changeScreen(MainMenuScreen.class);
            }
        });
    }

    @Override
    public void show() {
        //Ajout des animations d'entrées
        menuNameGroup.addAction(slideFromRight(menuNameGroup, 100 * ratio, menuNameGroup.getY(), ANIMATION_DURATION / 4));
        arrowButton.addAction(slideFromRight(arrowButton, 25 * ratio, arrowButton.getY(), ANIMATION_DURATION / 4));
    }

    @Override
    public void hide() {
        //Ajout des animations de sortie
        menuNameGroup.addAction(slideToRight(menuNameGroup));
        arrowButton.addAction(slideToRight(arrowButton));
    }

    /**
     * Méthode qui permet d'ajoute une ligne blanche entre deux lignes d'une table
     * @param table La table où il faut rajouter une ligne blanche
     */
    protected void addLine(Table table) {
        Image line = new Image(uiSkin, "line");
        line.setSize(5, 5);
        table.add(line).growX().align(Align.right).colspan(100);
        table.row();
    }
}
