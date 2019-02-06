package gui.graphics.screens.animations;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Cette classe est utilisé pour resencer les différentes animations qui peuvent être aplliqués à un groupe d'acteurs
 */
public class Animations {
    public static final float ANIMATION_DURATION = 0.6f;
    public static final Interpolation ANIMATION_INTERPOLATION = Interpolation.pow5;

    public static Action slideFromLeft(Actor actor, float x, float y) {
        return slideFromLeft(actor, x, y, 0);
    }
    public static Action slideFromLeft(Actor actor, float x, float y, float delay) {
        return sequence(moveTo(-x, y), delay(delay), moveTo(x, y, ANIMATION_DURATION, ANIMATION_INTERPOLATION));
    }

    public static Action slideFromRight(Actor actor, float x, float y) {
        return slideFromRight(actor, x, y, 0);
    }

    public static Action slideFromRight(Actor actor, float x, float y, float delay) {
        return sequence(moveTo(x + actor.getStage().getWidth(), actor.getY()), delay(delay), moveTo(x, y, ANIMATION_DURATION, ANIMATION_INTERPOLATION));
    }

    public static Action slideToLeft(Actor actor) {
        return slideToLeft(actor, 0);
    }

    public static Action slideToLeft(Actor actor, float delay) {
        return sequence(delay(delay), moveTo(-actor.getX(), actor.getY(), ANIMATION_DURATION, ANIMATION_INTERPOLATION));
    }

    public static Action slideToRight(Actor actor) {
        return slideToRight(actor, 0);
    }

    public static Action slideToRight(Actor actor, float delay) {
        return sequence(delay(delay), moveTo(actor.getX() + actor.getStage().getWidth(), actor.getY(), ANIMATION_DURATION, ANIMATION_INTERPOLATION));
    }
}
