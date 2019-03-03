package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Interpolation;
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
import roomController.Room;

import javax.sound.sampled.Line;
import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import static gui.graphics.screens.animations.Animations.*;
import static gui.utils.Constants.PAD;

public class CreateRoomMenuScreen extends SubMenuScreen{

    private final Table table;
    private final TextButton createRoomButton;
    private final Slider playersSlider, aiSlider;
    private final SelectBox<String> mapSelectBox;
    private final ButtonGroup<TextButton> naturalGroup;
    private Boolean online;

    private HashMap<String, String> nameToFileName;
    private HashMap<String, XmlReader.Element> nameToXml;

    public CreateRoomMenuScreen(Slay parent, Stage stage, boolean online) {
        super(parent, stage, "CREATE ROOM");
        this.online = online;
        Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = defaultFont;

        TextField.TextFieldStyle textFieldStyle = uiSkin.get(TextField.TextFieldStyle.class);
        textFieldStyle.font = textFont;
        textFont.getData().padLeft = -10;

        TextField mapName = new TextField("", textFieldStyle);
        mapName.appendText("name of the room");


        Label playersSliderNumber = new Label("1", labelStyle);
        Label aiSliderNumber = new Label("0", labelStyle);

        playersSlider = new Slider(1, 2, 1, false, uiSkin);
        aiSlider = new Slider(0, 1, 1, false, uiSkin);
        playersSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                playersSliderNumber.setText((int)playersSlider.getValue());
                //On change le PlayerSlider si le nombre de players
                // et le nombre d'ia est supérieur au nombre max de joueurs
                if(aiSlider.getValue() + playersSlider.getValue() > playersSlider.getMaxValue()) {
                    aiSlider.setValue(aiSlider.getValue()-1);
                }
            }
        });
        aiSlider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                aiSliderNumber.setText((int)aiSlider.getValue());
                //On change le PlayerSlider si le nombre de players
                // et le nombre d'ia est supérieur au nombre max de joueurs
                if(playersSlider.getValue() + aiSlider.getValue() > playersSlider.getMaxValue()) {
                    playersSlider.setValue(playersSlider.getValue()-1);
                }
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

        createRoomButton = new TextButton("Create Room", textButtonStyle);
        createRoomButton.setX(stage.getWidth() - createRoomButton.getWidth());
        createRoomButton.setY(stage.getHeight() / 10);
        createRoomButton.addListener(createRoomListener());
        stage.addActor(createRoomButton);

        Table scrollTable = new Table();
        scrollTable.add(new Label("Name", labelStyle)).align(Align.left);
        scrollTable.add(mapName).minWidth(350*ratio).pad(PAD).align(Align.left).colspan(2);
        scrollTable.row();
        scrollTable.add(new Label("Map", labelStyle)).align(Align.left);
        scrollTable.add(mapSelectBox).minWidth(350*ratio).pad(PAD).align(Align.left).colspan(2);
        scrollTable.row();
        scrollTable.add(new Label("Numbers of Players", labelStyle)).align(Align.left);
        scrollTable.add(playersSlider).minWidth(350*ratio).pad(PAD).align(Align.left).colspan(1);
        scrollTable.add(playersSliderNumber).pad(PAD).align(Align.left);
        scrollTable.row();
        scrollTable.add(new Label("Numbers of AI", labelStyle)).align(Align.left);
        scrollTable.add(aiSlider).minWidth(350*ratio).pad(PAD).align(Align.left).colspan(1);
        scrollTable.add(aiSliderNumber).pad(PAD).align(Align.left);
        scrollTable.row();
        scrollTable.add(new Label("Naturals Disasters", labelStyle)).align(Align.left);
        scrollTable.add(naturalOn).maxWidth(175*ratio).pad(PAD).align(Align.center);
        scrollTable.add(naturalOff).maxWidth(175*ratio).pad(PAD).align(Align.center);

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
                InGameScreen gameScreen;
                if (online) { //TODO
                    OnlineMessageSender messageSender = new OnlineMessageSender();
                    OnlineMessageListener messageListener = new OnlineMessageListener(messageSender.getClientChannel(), messageSender.getSelector());
                    messageListener.start();

                } else {
                    Room room = new Room(world, isNaturalDisastersOn(), messagesQueue);
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
        playersSlider.setRange(1, maxValue);
        aiSlider.setRange(0, maxValue - 1);
    }
}