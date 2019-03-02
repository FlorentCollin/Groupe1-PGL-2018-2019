package gui.graphics.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import communication.CreateRoomMessage;
import communication.JoinRoomMessage;
import communication.OnlineMessageListener;
import communication.OnlineMessageSender;
import gui.app.Slay;

import static gui.graphics.screens.animations.Animations.slideFromRight;
import static gui.graphics.screens.animations.Animations.slideToRight;

public class OnlineMenuScreen extends SubMenuScreen{

    private OnlineMessageSender messageSender;
    private OnlineMessageListener messageListener;

    private TextButton createRoom;
    private TextButton joinRoom;

    public OnlineMenuScreen(Slay parent, Stage stage) {
        super(parent, stage, "Online Rooms");
        this.messageSender = new OnlineMessageSender();
        this.messageListener = new OnlineMessageListener(messageSender.getClientChannel(), messageSender.getSelector());
        this.messageListener.start();

        TextButton.TextButtonStyle textButtonStyle = uiSkin.get("button",TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;

        createRoom = new TextButton("Create Room", textButtonStyle);
        createRoom.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
        createRoom.addListener(new ClickListener() {
           @Override
           public void clicked(InputEvent event, float x, float y) {
               System.out.println("Send Message CreateRoom ");
               messageSender.send(new CreateRoomMessage("g1_World1", true));
           }
        });
        joinRoom = new TextButton("Join Room", textButtonStyle);
        joinRoom.setPosition(stage.getWidth() / 2, stage.getHeight() / 2 + createRoom.getHeight() + 50);
        joinRoom.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Send Message JoinRoom");
                messageSender.send(new JoinRoomMessage());
            }
        });
        stage.addActor(createRoom);
        stage.addActor(joinRoom);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if(messageListener.getBoard() != null) {
            parent.setScreen(new InGameScreen(parent,"g1_World1", messageListener.getBoard(), messageSender, messageListener));
        }
    }

    @Override
    public void show() {
        super.show();
        createRoom.addAction(slideFromRight(createRoom, stage.getWidth() / 2, createRoom.getY()));
        joinRoom.addAction(slideFromRight(joinRoom, stage.getWidth() / 2, joinRoom.getY()));
    }

    @Override
    public void hide() {
        super.hide();
        createRoom.addAction(slideToRight(createRoom));
        joinRoom.addAction(slideToRight(joinRoom));
    }

}
