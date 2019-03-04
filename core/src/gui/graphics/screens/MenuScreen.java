package gui.graphics.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import gui.app.Slay;
import gui.graphics.screens.animations.RectangleActor;

/**
 * Classe abstraite, parents de tous les menus
 */
public abstract class MenuScreen extends BasicScreen {

    public MenuScreen(Slay parent) {
      super(parent);
    }

    public MenuScreen(Slay parent, Stage stage) {
        super(parent, stage);
    }

    /**
     * Méthode qui génère le petit Titre du menu visible en haut à gauche
     * @param name le nom du menu à afficher
     * @return le groupe contenant le Titre du menu et le petit carré à sa gauche
     */
    protected HorizontalGroup generateMenuNameGroup(String name) {
        Label.LabelStyle textStyle = uiSkin.get(Label.LabelStyle.class);
        textStyle.font = defaultFont;
        Label label = new Label(name, textStyle);

        RectangleActor rectangle = new RectangleActor();
        rectangle.setColor(Color.WHITE);
        rectangle.setSize(25 * ratio, 25 * ratio);

        HorizontalGroup horizontalGroup = new HorizontalGroup();
        horizontalGroup.space(25 * ratio);
        horizontalGroup.setY(stage.getHeight() - 100 * ratio);
        horizontalGroup.addActor(rectangle);
        horizontalGroup.addActor(label);
        return horizontalGroup;
    }

    /**
     * Méthode qui génère la flèche situé en haut à gauche d'un sous-menu pour permettre à l'utilisateur
     * de revenir au menu précédent
     * @return l'image contenant la flèche contenant la flèche
     */
    protected ImageButton generateArrowButton() {
        ImageButton arrowButton = new ImageButton(uiSkin, "arrow");
        arrowButton.setTransform(true);
        arrowButton.setScale(0.5f * ratio);
        arrowButton.setY(stage.getHeight() - 75 * ratio);
        return arrowButton;
    }

}
