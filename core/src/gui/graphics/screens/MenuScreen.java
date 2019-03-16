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
        //Récupération du style d'un texte
        Label.LabelStyle textStyle = uiSkin.get(Label.LabelStyle.class);
        textStyle.font = defaultFont;
        //Création du label qui contient le nom du menu
        Label label = new Label(name, textStyle);
        label.setName("name"); //On lui ajoute un nom pour pouvoir le modifier par la suite
        //Note: Par exemple le nom du menu est changé par le nom de le nom de la room dans des parties en ligne

        //Création du carré situé a gauche du nom du menu
        RectangleActor rectangle = new RectangleActor();
        rectangle.setColor(Color.WHITE);
        rectangle.setSize(25 * ratio, 25 * ratio);

        HorizontalGroup horizontalGroup = new HorizontalGroup();
        horizontalGroup.space(25 * ratio); //Espace entre les différents acteurs du groupe
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
