package gui.graphics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import communication.Messages.JoinRoomMessage;
import communication.Messages.ListRoomsMessage;
import communication.Messages.TextMessage;
import communication.OnlineMessageListener;
import communication.OnlineMessageSender;
import gui.app.Slay;
import gui.utils.JoinButton;
import gui.utils.Language;

import java.util.ArrayList;
import java.util.UUID;

import static gui.graphics.screens.animations.Animations.*;
import static gui.graphics.screens.animations.Animations.ANIMATION_DURATION;

public class OnlineMenuScreen extends SubMenuScreen{

    private final Label.LabelStyle labelStyle;
    private final Table table;
    private final TextButton.TextButtonStyle textButtonStyle;
    private Table scrollTable;
    private OnlineMessageSender messageSender;
    private OnlineMessageListener messageListener;

    private TextButton createRoom;
    private TextButton refresh;

    public OnlineMenuScreen(Slay parent, Stage stage) {
        super(parent, stage, "Online Rooms");
        this.messageSender = new OnlineMessageSender(parent.getUserSettings().getUsername());
        this.messageListener = new OnlineMessageListener(messageSender.getClientChannel(), messageSender.getSelector());
        this.messageListener.start();
        messageSender.send(new TextMessage("getWaitingRooms"));

        Image background = new Image(uiSkin.getDrawable("room-background"));
        float width = background.getWidth();
        float height = background.getHeight();
        labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = textFont;

        textButtonStyle = uiSkin.get("checked",TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;
        createRoom = new TextButton(Language.bundle.get("createRoom"), textButtonStyle);
        createRoom.setY(stage.getHeight() / 10 - 20);
        createRoom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.setScreen(new CreateRoomMenuScreen(parent, stage, messageSender, messageListener));
            }
        });

        refresh = new TextButton(Language.bundle.get("refresh"), textButtonStyle);
        refresh.setY(createRoom.getY() + createRoom.getHeight() + 20);
        refresh.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                messageSender.send(new TextMessage("getWaitingRooms"));
            }
        });

        stage.addActor(createRoom);
        stage.addActor(refresh);
        scrollTable = new Table();
        ScrollPane scroller = new ScrollPane(scrollTable, uiSkin);
        scroller.setScrollingDisabled(true, false);
        scroller.setScrollbarsVisible(true);
        scroller.setFadeScrollBars(false);
        //TODO COMMENT
        table = new Table(uiSkin);
        table.setSize(width, height);
        table.setBackground("room-background");
        table.setY(100);
        table.add(scroller).fillX().expand().align(Align.topLeft).padTop(3).padBottom(3);
        stage.addActor(table);
        Gdx.input.setInputProcessor(this.stage);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if(messageListener.needRefresh()) {
            refreshList();
        }
        if(messageListener.getPlayers().size() > 0) {
            parent.setScreen(new WaitingRoomScreen(parent, stage, messageSender, messageListener));
        }
    }

    @Override
    public void show() {
        super.show();
        table.addAction(slideFromRight(table, 50, table.getY(), ANIMATION_DURATION / 4));
        refresh.addAction(slideFromRight(refresh, stage.getWidth() - refresh.getWidth() - 20, refresh.getY(), ANIMATION_DURATION / 4));
        createRoom.addAction(slideFromRight(createRoom, stage.getWidth() - createRoom.getWidth() - 20, createRoom.getY(), ANIMATION_DURATION / 4));
    }

    @Override
    public void hide() {
        super.hide();
        table.addAction(slideToRight(table));
        refresh.addAction(slideToRight(refresh));
        createRoom.addAction(slideToRight(createRoom));
    }

    @Override
    public void dispose() {
        messageSender.close();
    }

    private void refreshList() {
        scrollTable.reset();
        scrollTable.add(new Label("Room Name", labelStyle)).expandX().pad(10).align(Align.topLeft);
        scrollTable.add(new Label("Number of Players", labelStyle)).pad(10).padRight(50).align(Align.center);
        scrollTable.row();
        if(messageListener.getRoomNames() != null) {
            ArrayList<String> roomNames = messageListener.getRoomNames();
            ArrayList<Integer> nPlayer = messageListener.getnPlayer();
            ArrayList<Integer> nPlayerIn = messageListener.getnPlayerIn();
            ArrayList<UUID> ids = messageListener.getIds();
            for (int i = 0; i < roomNames.size(); i++) {
                scrollTable.add(new Label(roomNames.get(i), labelStyle)).pad(10).align(Align.left);
                scrollTable.add(new Label(nPlayerIn.get(i) + "/" + nPlayer.get(i), labelStyle)).pad(10).align(Align.center);
                JoinButton join = new JoinButton(Language.bundle.get("join"), textButtonStyle, ids.get(i));
                join.setChecked(true);
                join.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        messageSender.send(new JoinRoomMessage(join.getId()));
                    }
                });
                scrollTable.add(join).maxWidth(150).pad(10).padRight(50).align(Align.right);
                scrollTable.row();
            }
        }
    }
}
