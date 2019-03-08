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
import communication.Message;
import communication.OfflineMessageSender;
import communication.OnlineMessageListener;
import communication.OnlineMessageSender;
import gui.app.Slay;
import gui.utils.Language;
import roomController.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static gui.graphics.screens.animations.Animations.*;
import static gui.utils.Constants.PAD;

public class CreateRoomMenuScreen extends SubMenuScreen{

    private final Table table;
    private final TextButton createRoomButton;
    private final Slider aiSlider;
    private int pValue;  //Valeur précédente du ai slider
    private final SelectBox<String> mapSelectBox;
    private final ButtonGroup<TextButton> naturalGroup;
    private Table scrollTable;
    private ArrayList<Label> aiNames = new ArrayList<>();
    private ArrayList<SelectBox<String>> aiStrats = new ArrayList<>();
    private Boolean online;

    private HashMap<String, String> nameToFileName;
    private HashMap<String, XmlReader.Element> nameToXml;

    public CreateRoomMenuScreen(Slay parent, Stage stage, boolean online) {
        super(parent, stage, Language.bundle.get("createRoom"));
        this.online = online;
        Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = defaultFont;

        TextField.TextFieldStyle textFieldStyle = uiSkin.get(TextField.TextFieldStyle.class);
        textFieldStyle.font = textFont;
        textFont.getData().padLeft = -10;

        TextField mapName = new TextField("", textFieldStyle);
        mapName.appendText(Language.bundle.get("nameOfTheRoom"));


        Label aiSliderNumber = new Label("0", labelStyle);
        pValue = 0; //Initialisation de la première valeur du ai slider

        aiSlider = new Slider(0, 1, 1, false, uiSkin);
        aiSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                int value = (int)aiSlider.getValue();
                aiSliderNumber.setText(value);
                while (value > pValue) {
                    addAI();
                    value--;
                }
                while (value < pValue) {
                    delAI(pValue - 1);
                    pValue--;
                }
                pValue = (int)aiSlider.getValue();
            }
        });

        SelectBox.SelectBoxStyle selectBoxStyle = uiSkin.get(SelectBox.SelectBoxStyle.class);
        selectBoxStyle.font = textFont;
        selectBoxStyle.listStyle.font = textFont;
        mapSelectBox = new SelectBox<>(selectBoxStyle);

        Array<String> worldsNames = initWorldsNames();
        mapSelectBox.setItems(worldsNames);
        mapSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                changeSliderSize();
            }
        });
        changeSliderSize();

        TextButton.TextButtonStyle textButtonStyle = uiSkin.get("button",TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;
        TextButton naturalOff = new TextButton("OFF", textButtonStyle);
        TextButton naturalOn = new TextButton("ON", textButtonStyle);
        naturalOn.setChecked(true);

        naturalGroup = new ButtonGroup<>(naturalOn, naturalOff);
        naturalGroup.setMaxCheckCount(1);
        naturalGroup.setMinCheckCount(1);
        naturalGroup.setUncheckLast(true);

        createRoomButton = new TextButton(Language.bundle.get("createRoom"), textButtonStyle);
        createRoomButton.setX(stage.getWidth() - createRoomButton.getWidth());
        createRoomButton.setY(stage.getHeight() / 10);
        createRoomButton.addListener(createRoomListener());
        stage.addActor(createRoomButton);

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
        //TODO COMMENT
        ScrollPane scroller = new ScrollPane(scrollTable);
        scroller.setScrollingDisabled(true, false);
        table = new Table(uiSkin);
        table.setWidth(stage.getWidth() - stage.getWidth() / 5);
        table.setHeight(stage.getHeight() - (stage.getHeight() -menuNameGroup.getY())*2);
        table.add(scroller).fillX().expand().align(Align.topLeft);
        stage.addActor(table);
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
    public void pause() {

    }

    @Override
    public void resume() {
    }

    private Array<String> initWorldsNames() {
        FileHandle dirHandle = Gdx.files.internal("worlds");
        XmlReader xml = new XmlReader();
        nameToFileName = new HashMap<>();
        nameToXml = new HashMap<>();
        Array<String> worldsNames = new Array<>();
        for(FileHandle file : dirHandle.list()) {
            if(file.extension().equals("xml")) {
                XmlReader.Element xmlElement = xml.parse(file);
                String worldName = xmlElement.getAttribute("name");
                worldsNames.add(worldName);
                nameToFileName.put(worldName, file.nameWithoutExtension());
                nameToXml.put(worldName, xmlElement);
            }
        }
        return worldsNames;
    }

    private boolean isNaturalDisastersOn() {
        TextButton button = naturalGroup.getChecked();
        return button.getText().equals("ON");

    }

    private ClickListener createRoomListener() {
        return new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                LinkedBlockingQueue<Message> messagesQueue = new LinkedBlockingQueue<>();
                String world = nameToFileName.get(mapSelectBox.getSelected());
                ArrayList<String> ai = new ArrayList<>();
                aiStrats.forEach((i) -> ai.add(i.getSelected()));
                ArrayList<String> playersName = new ArrayList<>();
                int number = Integer.parseInt(nameToXml.get(mapSelectBox.getSelected()).getChildByName("players").getAttribute("number"));
                for (int i = 1; i <= number - ai.size(); i++) {
                    playersName.add(parent.getUserSettings().getUsername());
                }
                ai.forEach((i) -> playersName.add("AI"));
                InGameScreen gameScreen;
                if (online) { //TODO
                    OnlineMessageSender messageSender = new OnlineMessageSender();
                    OnlineMessageListener messageListener = new OnlineMessageListener(messageSender.getClientChannel(), messageSender.getSelector());
                    messageListener.start();

                } else {
                    Room room = new Room(world, isNaturalDisastersOn(), ai, playersName, messagesQueue);
                    OfflineMessageSender messageSender = new OfflineMessageSender(messagesQueue);
                    room.start();
                    while (room.getBoard() == null) { //TODO MODIFY THIS PART
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    gameScreen = new InGameScreen(parent, world, room.getBoard(), messageSender);
                    parent.changeScreen(gameScreen);
                }
            }
        };
    }

    private void changeSliderSize() {
        XmlReader.Element xmlElement = nameToXml.get(mapSelectBox.getSelected());
        int maxValue = Integer.parseInt(xmlElement.getChildByName("players").getAttribute("number"));
        aiSlider.setRange(0, maxValue - 1);
    }

    private void addAI() {
        Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = defaultFont;
        Label aiName = new Label("AI#" + (aiNames.size()+1), labelStyle);
        aiNames.add(aiName);
        SelectBox.SelectBoxStyle selectBoxStyle = uiSkin.get(SelectBox.SelectBoxStyle.class);
        selectBoxStyle.font = textFont;
        selectBoxStyle.listStyle.font = textFont;
        SelectBox<String> aiStrat = new SelectBox<>(selectBoxStyle);
        aiStrat.setItems("Random", "Easy", "Medium", "Hard");
        aiStrat.setSelected("Random");
        aiStrats.add(aiStrat);
        scrollTable.add(aiName).align(Align.left);
        scrollTable.add(aiStrat).minWidth(350*ratio).pad(PAD).align(Align.left).colspan(2);
        scrollTable.row();
    }

    private void delAI(int index) {
        Label aiName = aiNames.get(index);
        SelectBox<String> aiStrat = aiStrats.get(index);
        scrollTable.getCell(aiName).reset();
        scrollTable.getCell(aiStrat).reset();
        scrollTable.row();
        aiName.remove();
        aiStrat.remove();
        aiNames.remove(index);
        aiStrats.remove(index);
    }
}