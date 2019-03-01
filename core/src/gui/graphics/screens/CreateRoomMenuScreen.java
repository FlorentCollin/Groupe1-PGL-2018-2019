package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import gui.app.Slay;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;

import static gui.graphics.screens.animations.Animations.*;
import static gui.utils.Constants.PAD;

public class CreateRoomMenuScreen extends SubMenuScreen{

    private final Table table;
    private final TextButton createRoomButton;

    public CreateRoomMenuScreen(Slay parent, Stage stage) {
        super(parent, stage, "CREATE ROOM");
        Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = defaultFont;

        TextField.TextFieldStyle textFieldStyle = uiSkin.get(TextField.TextFieldStyle.class);
        textFieldStyle.font = textFont;
        textFont.getData().padLeft = -10;

        TextField mapName = new TextField("", textFieldStyle);
        mapName.appendText("name of the room");


        Label playersSliderNumber = new Label("1", labelStyle);
        Label aiSliderNumber = new Label("0", labelStyle);

        Slider playersSlider = new Slider(1, 6, 1, false, uiSkin);
        playersSlider.setValue(1);
        Slider aiSlider = new Slider(0, 5, 1, false, uiSkin);
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
        SelectBox mapSelectBox = new SelectBox(selectBoxStyle);

        ArrayList<XmlReader.Element> worldsXml = getWorldsXml();
        Array<String> worldsNames = getWorldsName(worldsXml);
        mapSelectBox.setItems(worldsNames);
        mapSelectBox.setSelected(worldsNames.get(0));

        TextButton.TextButtonStyle textButtonStyle = uiSkin.get("button",TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;
        TextButton naturalOff = new TextButton("OFF", textButtonStyle);
        TextButton naturalOn = new TextButton("ON", textButtonStyle);
        naturalOn.setChecked(true);

        ButtonGroup<TextButton> naturalGroup = new ButtonGroup<>(naturalOn, naturalOff);
        naturalGroup.setMaxCheckCount(1);
        naturalGroup.setMinCheckCount(1);
        naturalGroup.setUncheckLast(true);

        createRoomButton = new TextButton("Create Room", textButtonStyle);
        createRoomButton.setX(stage.getWidth() - createRoomButton.getWidth());
        createRoomButton.setY(stage.getHeight() / 10);
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
//        scrollTable.setDebug(true);

        //TODO
        ScrollPane scroller = new ScrollPane(scrollTable);
        scroller.setScrollingDisabled(true, false);
        table = new Table();
        table.setWidth(stage.getWidth() - stage.getWidth() / 5);
        table.setHeight(stage.getHeight() - (stage.getHeight() -menuNameGroup.getY())*2);
        table.add(scroller).fillX().expand().align(Align.topLeft);

        stage.addActor(table);

        mapSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                for(XmlReader.Element xmlElement : worldsXml) {
                    if(xmlElement.getAttribute("name") == mapSelectBox.getSelected()) {
                        int maxValue = Integer.parseInt(xmlElement.getChildByName("players").getAttribute("number"));
                        Cell cell = scrollTable.getCell(playersSlider);
                    }
                }
            }
        });

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

    private ArrayList<XmlReader.Element> getWorldsXml() {
        ArrayList<XmlReader.Element> worldsXml = new ArrayList<>();
        FileHandle dirHandle = Gdx.files.internal("worlds");
        XmlReader xml = new XmlReader();
        for(FileHandle file : dirHandle.list()) {
            if(file.extension().equals("xml")) {
                XmlReader.Element xml_element = xml.parse(file);
                worldsXml.add(xml_element);
            }
        }
        return worldsXml;
    }

    private Array<String> getWorldsName(ArrayList<XmlReader.Element> worldsXml) {
        Array<String> worldsNames = new Array<>();
        for(XmlReader.Element xmlElement : worldsXml) {
            worldsNames.add(xmlElement.getAttribute("name"));
        }
        return worldsNames;
    }

    private void addPlayersSliderListener(Slider playersSlider) {
        
    }
}