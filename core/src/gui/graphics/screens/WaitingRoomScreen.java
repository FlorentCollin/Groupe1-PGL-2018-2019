package gui.graphics.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import communication.MessageListener;
import communication.MessageSender;
import communication.Messages.TextMessage;
import gui.app.Slay;
import gui.utils.Language;
import logic.player.Player;

import java.util.ArrayList;

import static gui.graphics.screens.animations.Animations.*;

/**
 * Salle d'attente d'une partie en ligne
 */
public class WaitingRoomScreen extends SubMenuScreen {


    private final Table table;
    private final Label.LabelStyle labelStyle;
    private final MessageSender messageSender;
    private final MessageListener messageListener;
    private final TextButton readyButton;
    private final TextButton launchGameButton;

    public WaitingRoomScreen(Slay parent, Stage stage, MessageSender messageSender, MessageListener messageListener) {
        super(parent, stage, Language.bundle.get("room"));
        this.messageSender = messageSender;
        this.messageListener = messageListener;
        //Récupération de l'image de fond de la table
        Image background = new Image(uiSkin.getDrawable("room-background"));
        float width = background.getWidth();
        float height = background.getHeight();
        labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = textFont;

        //Création des différents boutons
        TextButton.TextButtonStyle textButtonStyle = uiSkin.get("button",TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;
        readyButton = new TextButton(Language.bundle.get("ready"), textButtonStyle);
        readyButton.setY(stage.getHeight() / 10);
        readyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                messageSender.send(new TextMessage("ready"));
            }
        });
        textButtonStyle = uiSkin.get("checked", TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;
        launchGameButton = new TextButton(Language.bundle.get("launchGame"), textButtonStyle);
        launchGameButton.setY(stage.getHeight() / 10 - readyButton.getHeight() - 20);
        launchGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                messageSender.send(new TextMessage("launchGame"));
            }
        });
        stage.addActor(readyButton);
        stage.addActor(launchGameButton);


        table = new Table(uiSkin);
        table.setSize(width, height);
        table.setBackground("room-background");
        table.setY(100);
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        Label roomName =  menuNameGroup.findActor("name");
        roomName.setText(messageListener.getRoomName()); //Ajout du nom la room dans le label en haut à gauche
        //Mise à jour de la table des joueurs avec leur statut (prêt ou non prêt)
        table.reset();
        table.left().top();
        //Ajout des labels qui sont toujours présent
        table.add(new Label("Player's name", labelStyle)).expandX().pad(10).align(Align.topLeft);
        table.add(new Label("Ready", labelStyle)).pad(10).padRight(50).align(Align.topRight);
        table.row();
        addLine(table); //Ajout d'une ligne blanche de séparation
        ArrayList<Player> players = messageListener.getPlayers();
        ArrayList<Boolean> playersReady = messageListener.getPlayersReady();
        for (int i = 0; i < players.size(); i++){ //Itération sur la liste des joueurs
            //Si le nom du joueur n'est pas précisé c'est qu'on attend toujours qu'un joueur se connecte
            if(players.get(i).getName() == null) {
                table.add(new Label(Language.bundle.get("waitingPlayer"), labelStyle)).pad(10).align(Align.left);
            } else {
                table.add(new Label(players.get(i).getName(), labelStyle)).pad(10).align(Align.left);
            }
            if(playersReady.get(i)) {
                table.add(new Label("V", labelStyle)).pad(10).padRight(50).align(Align.right);
            } else {
                table.add(new Label("X", labelStyle)).pad(10).padRight(50).align(Align.right);
            }
            table.row();
        }
        //Si le board est chargé c'est que la partie peut se lancer donc on change le screen pour un InGameScreen
        if (messageListener.getBoard() != null) {
            parent.changeScreen(new InGameScreen(parent, messageListener.getMapName(), messageListener.getBoard(), messageSender, messageListener));
        }
        super.render(delta);
    }

    @Override
    public void show() {
        super.show();
        table.addAction(slideFromRight(table, 50, table.getY(), ANIMATION_DURATION / 4));
        readyButton.addAction(slideFromRight(readyButton, stage.getWidth() - readyButton.getWidth() - 20, readyButton.getY(), ANIMATION_DURATION / 4));
        launchGameButton.addAction(slideFromRight(launchGameButton, stage.getWidth() - launchGameButton.getWidth() - 20, launchGameButton.getY(), ANIMATION_DURATION / 4));

    }

    @Override
    public void hide() {
        super.hide();
        table.addAction(slideToRight(table));
        readyButton.addAction(slideToRight(readyButton));
        launchGameButton.addAction(slideToRight(launchGameButton));

    }
}
