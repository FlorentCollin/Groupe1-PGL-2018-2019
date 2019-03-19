package gui.graphics.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import gui.app.Slay;
import gui.settings.UserShortcuts;
import gui.utils.Language;
import gui.utils.ShortcutsButton;

import static gui.graphics.screens.animations.Animations.*;
import static gui.utils.Constants.PAD;

/**
 * Menu des paramètres utilisateur
 */
public class ShortcutsMenuScreen extends SubMenuScreen {

    private final Table table;
    private final Label.LabelStyle labelStyle;
    private ButtonGroup<ShortcutsButton> keyBindGroup;
    private Table scrollTable;

    public ShortcutsMenuScreen(Slay parent, Stage stage) {
        super(parent, stage, Language.bundle.get("shortcuts"));
        labelStyle = uiSkin.get(Label.LabelStyle.class);
        labelStyle.font = defaultFont;

        //Groupe qui contient l'ensemble des raccourcis
        keyBindGroup = new ButtonGroup<>();
        //Un seul bouton ne peut être actif à la fois. En effet on ne peut changer qu'un seul raccourci à la fois
        keyBindGroup.setMaxCheckCount(1);
        keyBindGroup.setMinCheckCount(0);
        keyBindGroup.setUncheckLast(true);

        //Listener que l'utilisateur déclenche lorsqu'il veut changer un raccourci
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keyBindGroup.getChecked() != null) {
                    UserShortcuts userShortcuts = parent.getUserShortcuts();
                    //Si le raccourci est déjà assigné alors celui-ci est automatiquement retiré
                    for (ShortcutsButton button : keyBindGroup.getButtons()) {
                        if (button.getText().toString().equals(Input.Keys.toString(keycode))) {
                            Integer[] value = userShortcuts.getShortcuts().get(button.getShortcutName());
                            value[button.getNumber()] = null;
                            userShortcuts.changeShortcut(button.getShortcutName(), value);
                            button.setText("");
                        }
                        //Changement du raccourci dans le textButton correspondant
                        ShortcutsButton checkedButton = keyBindGroup.getChecked();
                        Integer[] value = userShortcuts.getShortcuts().get(checkedButton.getShortcutName());
                        System.out.println(value.length);
                        value[checkedButton.getNumber()] = keycode;
                        userShortcuts.changeShortcut(checkedButton.getShortcutName(), value);
                        checkedButton.setText(Input.Keys.toString(keycode));
                    }
                    keyBindGroup.uncheckAll();
                }
                return true;
            }
        });

        //Table des raccourcis claviers
        scrollTable = new Table();
        scrollTable.add(); //Ajout d'une cellule
        scrollTable.add(new Label(Language.bundle.get("key") + " 1", labelStyle)).align(Align.center);
        scrollTable.add(new Label(Language.bundle.get("key") + " 2", labelStyle)).align(Align.center);
        scrollTable.row();
        //Création des différents labels et boutons pour les raccourcis
        for (String shortcutName : parent.getUserShortcuts().getShortcutsName()) {
            createShortcut(shortcutName, Language.bundle.get(shortcutName.replaceAll(" ", "")));
            scrollTable.row();
        }
        ScrollPane scroller = new ScrollPane(scrollTable);
        scroller.setScrollingDisabled(true, false); //Désactivation du scrolling horizontal

        table = new Table();
        table.setWidth(stage.getWidth() - stage.getWidth() / 5);
        table.setHeight(stage.getHeight() - (stage.getHeight() - menuNameGroup.getY()) * 2);
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

    /**
     * Méthode qui permet de créer un label avec le nom du raccourci
     * ainsi que deux textButtons qui montrent quelles sont les touches associées à ce raccourci
     *
     * @param text le text qui décrit le raccourci (ex: Move camera down)
     */
    private void createKeybindButton(String text) {
        TextButton.TextButtonStyle textButtonStyle = uiSkin.get("button", TextButton.TextButtonStyle.class);
        textButtonStyle.font = defaultFontItalic;
        for (int i = 0; i < 2; i++) {
            ShortcutsButton button;
            Integer keycode = parent.getUserShortcuts().getShortcuts().get(text)[i];
            if (keycode != -2) { //keycode == -2 indique que le raccourci n'est pas défini (-1 étant pris par Libgdx comme n'importe quelle touche
                button = new ShortcutsButton(text, i, Input.Keys.toString(parent.getUserShortcuts().getShortcuts().get(text)[i]), textButtonStyle);
            } else {
                button = new ShortcutsButton(text, i, "", textButtonStyle);

            }
            keyBindGroup.add(button);
            scrollTable.add(button).pad(PAD).align(Align.center);
        }
    }

    private void createShortcut(String text, String printText) {
        scrollTable.add(new Label(printText, labelStyle)).align(Align.left);
        createKeybindButton(text);

    }
}
