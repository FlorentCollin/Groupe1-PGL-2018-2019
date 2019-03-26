package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import communication.*;
import communication.Messages.CreateRoomMessage;
import communication.Messages.Message;
import gui.app.Slay;
import gui.utils.Constants;
import gui.utils.Language;
import roomController.GameRoom;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static gui.graphics.screens.animations.Animations.*;
import static gui.utils.Constants.PAD;

/**
 * Menu de création d'une partie
 */
public class CreateRoomMenuScreen extends SubMenuScreen{

    private final Table table;
    private final TextButton createRoomButton;
    private final Slider aiSlider;
    private final TextField mapName;
    private int pValue;  //Valeur précédente du ai slider
    private final SelectBox<String> mapSelectBox;
    private final ButtonGroup<TextButton> naturalGroup;
    private Table scrollTable;
    private ArrayList<Label> aiNames = new ArrayList<>();
    private ArrayList<SelectBox<String>> aiStrats = new ArrayList<>();
    private Boolean online;
    private MessageSender messageSender;
    private MessageListener messageListener;

    private HashMap<String, String> nameToFileName;
    private HashMap<String, XmlReader.Element> nameToXml;
    private GameRoom room;
    private String world;

    /**
     * Constructeur pour une partie en ligne
     */
    public CreateRoomMenuScreen(Slay parent, Stage stage, boolean online, MessageSender messageSender, MessageListener messageListener) {
        this(parent, stage, online);
        this.online = true;
        this.messageSender = messageSender;
        this.messageListener = messageListener;
    }

    /**
     * Constructeur pour une partie hors-ligne
     */
    public CreateRoomMenuScreen(Slay parent, Stage stage, boolean online) {
        super(parent, stage, Language.bundle.get("createRoom"));
        this.online = online;
        Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = defaultFont;

        //Création du field contenant le nom de la room
        TextField.TextFieldStyle textFieldStyle = uiSkin.get(TextField.TextFieldStyle.class);
        textFieldStyle.font = textFont;
        textFont.getData().padLeft = -10;

        mapName = new TextField("", textFieldStyle);
        mapName.appendText(Language.bundle.get("nameOfTheRoom"));
        mapName.setMaxLength(28);

        //Création du slider du nombre d'intelligence artificielle
        Label aiSliderNumber = new Label("0", labelStyle);
        pValue = 0; //Initialisation de la première valeur du ai slider

        aiSlider = new Slider(0, 1, 1, false, uiSkin);
        aiSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                int value = (int)aiSlider.getValue(); //On récupère la valeur du slider
                aiSliderNumber.setText(value);
                while (value > pValue) { //Si cette valeur est supérieur à la valeur précédente on ajoute des AI
                    addAI();
                    value--;
                }
                while (value < pValue) { //Sinon on supprime des AI
                    delAI(pValue - 1);
                    pValue--;
                }
                pValue = (int)aiSlider.getValue();
                //On repositionne le bouton de création de partie en haut de la liste des acteurs pour qu'il soit encore accessible
                createRoomButton.remove();
                stage.addActor(createRoomButton);
            }
        });

        //Création de la selectBox des maps
        SelectBox.SelectBoxStyle selectBoxStyle = uiSkin.get(SelectBox.SelectBoxStyle.class);
        selectBoxStyle.font = textFont;
        selectBoxStyle.listStyle.font = textFont;
        mapSelectBox = new SelectBox<>(selectBoxStyle);

        Array<String> worldsNames = initWorldsNames();
        mapSelectBox.setItems(worldsNames);
        mapSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeSliderSize(); //On change le nombre d'AI possible en fonction de la map
            }
        });
        changeSliderSize(); //On appelle la méthode qui change le slider des AI pour l'initialiser

        //Création des boutons pour l'extension Natural Disasters
        TextButton.TextButtonStyle textButtonStyle = uiSkin.get("button",TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;
        TextButton naturalOff = new TextButton("OFF", textButtonStyle);
        TextButton naturalOn = new TextButton("ON", textButtonStyle);
        naturalOff.setName("off");
        naturalOn.setName("on");
        //Par défaut l'extension est activée
        naturalOn.setChecked(true);

        naturalGroup = new ButtonGroup<>(naturalOn, naturalOff);
        naturalGroup.setMaxCheckCount(1);
        naturalGroup.setMinCheckCount(1);
        naturalGroup.setUncheckLast(true);

        //Création du bouton "Create Room"
        textButtonStyle = uiSkin.get("checked", TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;
        createRoomButton = new TextButton(Language.bundle.get("createRoom"), textButtonStyle);
        createRoomButton.setX(stage.getWidth() - createRoomButton.getWidth());
        createRoomButton.setY(stage.getHeight() / 10);
        createRoomButton.addListener(createRoomListener());
        stage.addActor(createRoomButton);

        //Création du scrollpane qui contiendra l'ensemble des paramètres de la room en cours de création
        scrollTable = new Table();
        scrollTable.add(new Label(Language.bundle.get("nameMap"), labelStyle)).align(Align.left);
        scrollTable.add(mapName).minWidth(350*ratio).pad(PAD).align(Align.left).colspan(2);
        scrollTable.row();
        scrollTable.add(new Label(Language.bundle.get("map"), labelStyle)).align(Align.left);
        scrollTable.add(mapSelectBox).minWidth(350*ratio).pad(PAD).align(Align.left).colspan(2);
        scrollTable.row();
        scrollTable.add(new Label(Language.bundle.get("naturalDisasters"), labelStyle)).align(Align.left);
        scrollTable.add(naturalOn).maxWidth(175*ratio).pad(PAD).align(Align.center);
        scrollTable.add(naturalOff).maxWidth(175*ratio).pad(PAD).align(Align.center);
        scrollTable.row();
        scrollTable.add(new Label(Language.bundle.get("numberOfAI"), labelStyle)).align(Align.left);
        scrollTable.add(aiSlider).minWidth(350*ratio).pad(PAD).align(Align.left).colspan(1);
        scrollTable.add(aiSliderNumber).pad(PAD).align(Align.left);
        scrollTable.row();

        ScrollPane scroller = new ScrollPane(scrollTable);
        scroller.setScrollingDisabled(true, false); //Désactivation du scrolling horizontal
        table = new Table(uiSkin);
        table.setWidth(stage.getWidth() - stage.getWidth() / 5);
        table.setHeight(stage.getHeight() - (stage.getHeight() -menuNameGroup.getY())*2);
        table.add(scroller).fillX().expand().align(Align.topLeft); //Ajout du scroller
        stage.addActor(table);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if(messageListener != null && messageListener.getPlayers().size() > 0) {
            parent.setScreen(new WaitingRoomScreen(parent, stage, messageSender, messageListener));
        }
        if(room != null && room.getBoard() != null) {
            InGameScreen gameScreen = new InGameScreen(parent, world, room.getBoard(), messageSender);
            parent.setScreen(gameScreen);
        }
    }

    @Override
    public void show() {
        super.show();
        table.addAction(slideFromRight(table, stage.getWidth() / 5, table.getY(), ANIMATION_DURATION / 4));
        createRoomButton.addAction(slideFromRight(createRoomButton, stage.getWidth() - createRoomButton.getWidth() - stage.getWidth() / 10, createRoomButton.getY(), ANIMATION_DURATION / 4));
    }

    @Override
    public void hide() {
        super.hide();
        table.addAction(slideToRight(table));
        createRoomButton.addAction(slideToRight(createRoomButton));
    }

    @Override
    public void dispose() {
        //Fermeture du socket entre le client et le server (partie en ligne uniquement)
        if(messageSender instanceof OnlineMessageSender) {
            ((OnlineMessageSender) messageSender).close();
        }
    }

    /**
     * Méthode qui initialise le nom des différentes maps jouables
     * @return La liste des noms des maps jouables
     */
    private Array<String> initWorldsNames() {
        //Répertoire des maps
        XmlReader xml = new XmlReader();
        nameToFileName = new HashMap<>();
        nameToXml = new HashMap<>();
        Array<String> worldsNames = new Array<>();
        //On itère sur l'ensemble des fichiers du répertoires
        for(int i=1; i <= Constants.MAP_NUMBER; i++) {
            FileHandle file = Gdx.files.internal("worlds/g1_World" + i + ".xml");
            if(file.extension().equals("xml")) { //Si le fichier est un fichier xml alors c'est que c'est un fichier d'une map
                XmlReader.Element xmlElement = xml.parse(file);
                String worldName = xmlElement.getAttribute("name");
                int maxValue = Integer.parseInt(xmlElement.getChildByName("players").getAttribute("number"));
                System.out.println(online);
                if (!online || maxValue >= parent.getUserSettings().getNumberOfPlayers()) {
                    worldsNames.add(worldName);
                    worldsNames.sort();
                    nameToFileName.put(worldName, file.nameWithoutExtension());
                    nameToXml.put(worldName, xmlElement);
                }
            }
        }
        return worldsNames;
    }

    /**
     * Méthode qui permet de savoir si l'extension Natural Disasters est activée
     * @return true si l'extension est activée, false sinon
     */
    private boolean isNaturalDisastersOn() {
        TextButton button = naturalGroup.getChecked();
        return button.getName().equals("on");

    }

    /**
     * Méthode qui crée le listener pour le bouton "Create Room"
     * Ce listener va créer les différents threads et rediriger vers les prochain menu ou l'écran de jeu
     * @return le listener du bouton "Create Room"
     */
    private ClickListener createRoomListener() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LinkedBlockingQueue<Message> messagesQueue = new LinkedBlockingQueue<>();
                world = nameToFileName.get(mapSelectBox.getSelected());
                ArrayList<String> ai = new ArrayList<>();
                aiStrats.forEach((i) -> ai.add(i.getSelected())); //Ajout des stratégies des ia des selectBox
                if (online) {
                    messageSender.send(new CreateRoomMessage(world, mapName.getText(), isNaturalDisastersOn(), ai));
                } else {
                    ArrayList<String> playersName = new ArrayList<>();
                    int number = Integer.parseInt(nameToXml.get(mapSelectBox.getSelected()).getChildByName("players").getAttribute("number"));
                    //Ajout des noms des joueurs
                    for (int i = 1; i <= number - ai.size(); i++) {
                        playersName.add(parent.getUserSettings().getUsername());
                    }
                    aiNames.forEach((i) -> playersName.add(i.getText().toString()));
                    room = new GameRoom(world, isNaturalDisastersOn(), ai, playersName, messagesQueue);
                    messageSender = new OfflineMessageSender(messagesQueue);
                    //Démarrage du thread qui s'occupe de la partie hors-ligne
                    room.start();
                }
            }
        };
    }

    /**
     * Méthode qui change la valeur maximum du slider des AI
     */
    private void changeSliderSize() {
        //Récupération de la valeur maximum
        XmlReader.Element xmlElement = nameToXml.get(mapSelectBox.getSelected());
        int maxValue = Integer.parseInt(xmlElement.getChildByName("players").getAttribute("number"));
        if(online)
            aiSlider.setRange(0, maxValue - parent.getUserSettings().getNumberOfPlayers());
        else
            aiSlider.setRange(0, maxValue - 1);

    }

    /**
     * Méthode qui ajoute une selectBox qui permet à l'utilisateur de choisir le niveau de l'AI
     */
    private void addAI() {
        Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = defaultFont;
        //Création du label et de la selectBox
        Label aiName = new Label("AI#" + (aiNames.size()+1), labelStyle);
        aiNames.add(aiName);
        SelectBox.SelectBoxStyle selectBoxStyle = uiSkin.get(SelectBox.SelectBoxStyle.class);
        selectBoxStyle.font = textFont;
        selectBoxStyle.listStyle.font = textFont;
        SelectBox<String> aiStrat = new SelectBox<>(selectBoxStyle);
        aiStrat.setItems("Random", "Easy", "Medium", "Hard");
        aiStrat.setSelected("Random");
        //Ajout de la stratégie dans la liste des stratégies des AI
        aiStrats.add(aiStrat);
        scrollTable.add(aiName).align(Align.left);
        scrollTable.add(aiStrat).minWidth(350*ratio).pad(PAD).align(Align.left).colspan(2);
        scrollTable.row();
    }

    private void delAI(int index) {
        Label aiName = aiNames.get(index);
        SelectBox<String> aiStrat = aiStrats.get(index);
        //On reset les cellules correspondantes à l'ia associé à l'index pour les retirer de l'interface utilisateuro
        scrollTable.getCell(aiName).reset();
        scrollTable.getCell(aiStrat).reset();
        scrollTable.row();
        aiName.remove();
        aiStrat.remove();
        aiNames.remove(index);
        aiStrats.remove(index);
    }
}