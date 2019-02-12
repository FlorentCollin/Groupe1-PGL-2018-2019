package gui.graphics.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import gui.app.Slay;

import static gui.graphics.screens.animations.Animations.*;
import static gui.utils.Constants.PAD;


public class ShortcutsMenuScreen extends SubMenuScreen {

    private final Table table;
    private ButtonGroup<TextButton> keyBindGroup;
    private Table scrollTable;

    public ShortcutsMenuScreen(Slay parent, Stage stage) {
        super(parent, stage, "SHORTCUTS");
        Label.LabelStyle labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = defaultFont;

        keyBindGroup = new ButtonGroup<>();
        keyBindGroup.setMaxCheckCount(1);
        keyBindGroup.setMinCheckCount(0);
        keyBindGroup.setUncheckLast(true);

        scrollTable = new Table();
        scrollTable.add(new Label("Move camera up", labelStyle)).align(Align.left);
        createKeybindButton();
        scrollTable.row();
        scrollTable.add(new Label("Move camera down", labelStyle)).align(Align.left);
        createKeybindButton();
        scrollTable.row();
        scrollTable.add(new Label("Move camera left", labelStyle)).align(Align.left);
        createKeybindButton();
        scrollTable.row();
        scrollTable.add(new Label("Move camera left", labelStyle)).align(Align.left);
        createKeybindButton();
        scrollTable.row();
        scrollTable.add(new Label("Move camera right", labelStyle)).align(Align.left);
        createKeybindButton();
        scrollTable.row();
        scrollTable.add(new Label("End turn", labelStyle)).align(Align.left);
        createKeybindButton();
        scrollTable.row();
        scrollTable.add(new Label("Menu", labelStyle)).align(Align.left);
        createKeybindButton();
        //TODO
        ScrollPane scroller = new ScrollPane(scrollTable);
        scroller.setScrollingDisabled(true, false);
        table = new Table();
        table.setWidth(stage.getWidth() - stage.getWidth() / 5);
        table.setHeight(stage.getHeight() - (stage.getHeight() -menuNameGroup.getY())*2);
        table.add(scroller).fillX().expand().align(Align.topLeft);

        stage.addActor(table);
    }

    @Override
    public void show() {
        super.show();
        table.addAction(slideFromRight(table, stage.getWidth() / 5, table.getY(), ANIMATION_DURATION / 4));

    }

    @Override
    public void hide() {
        super.hide();
        table.addAction(slideToRight(table));
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    private void createKeybindButton() {
        TextButton.TextButtonStyle textButtonStyle = uiSkin.get("button", TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;
        for(int i=0; i<2; i++) {

            TextButton button = new TextButton("", textButtonStyle);
            keyBindGroup.add(button);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                            System.out.println("Yes i'm here");
                    button.addListener(new InputListener() {
                        @Override
                        public boolean keyTyped(InputEvent event, char character) {
                            button.setText(Character.toString(character));
                            return true;
                        }
                    });
                }
            });
            scrollTable.add(button).pad(PAD).align(Align.left);
        }
    }

}
