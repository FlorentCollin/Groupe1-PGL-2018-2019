package gui.graphics.screens;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import gui.app.Slay;

import static gui.graphics.screens.animations.Animations.ANIMATION_DURATION;
import static gui.graphics.screens.animations.Animations.slideFromRight;
import static gui.graphics.screens.animations.Animations.slideToRight;

public abstract class SubMenuScreen extends MenuScreen {


    protected final HorizontalGroup menuNameGroup;
    protected final ImageButton arrowButton;

    public SubMenuScreen(Slay parent, Stage stage, String menuName) {
        super(parent, stage);

        menuNameGroup = generateMenuNameGroup(menuName);
        stage.addActor(menuNameGroup);
        arrowButton = generateArrowButton();
        stage.addActor(arrowButton);
        arrowButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                parent.changeScreen(MainMenuScreen.class);
            }
        });
    }

    @Override
    public void show() {
        menuNameGroup.addAction(slideFromRight(menuNameGroup, 100 * ratio, menuNameGroup.getY(), ANIMATION_DURATION / 4));
        arrowButton.addAction(slideFromRight(arrowButton, 25 * ratio, arrowButton.getY(), ANIMATION_DURATION / 4));
    }

    @Override
    public void hide() {
        menuNameGroup.addAction(slideToRight(menuNameGroup));
        arrowButton.addAction(slideToRight(arrowButton));
    }
}
