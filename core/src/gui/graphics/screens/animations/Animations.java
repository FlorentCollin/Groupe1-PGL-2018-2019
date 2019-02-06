package gui.graphics.screens.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Cette classe est utilisé pour resencer les différentes animations qui peuvent être aplliqués à un groupe d'acteurs
 */
public class Animations {
    public static final float ANIMATION_DURATION = 0.6f;
    public static final Interpolation ANIMATION_INTERPOLATION = Interpolation.pow5;

    public static void slideFromLeft(Actor actor, float x, float y) {
        slideFromLeft(actor, x, y, 0);
    }
    public static void slideFromLeft(Actor actor, float x, float y, float delay) {
        actor.setX(-x); actor.setY(y);
        actor.addAction(sequence(delay(delay), moveTo(x, y, ANIMATION_DURATION, ANIMATION_INTERPOLATION)));
    }

    public static void slideFromRight(Actor actor, float x, float y) {
        slideFromRight(actor, x, y, 0);
    }

    public static void slideFromRight(Actor actor, float x, float y, float delay) {
        actor.setX(x + actor.getStage().getWidth()); actor.setY(y);
        actor.addAction(sequence(delay(delay), moveTo(x, y, ANIMATION_DURATION, ANIMATION_INTERPOLATION)));

    }

    public static void slideToLeft(Actor actor) {
        slideToLeft(actor, 0);
    }

    public static void slideToLeft(Actor actor, float delay) {
        actor.addAction(sequence(delay(delay), moveTo(-actor.getX(), actor.getY(), ANIMATION_DURATION, ANIMATION_INTERPOLATION)));
    }

    public static void slideToRight(Actor actor) {
        slideToRight(actor, 0);
    }

    public static void slideToRight(Actor actor, float delay) {
        actor.addAction(sequence(delay(delay), moveTo(actor.getX() + actor.getStage().getWidth(), actor.getY(), ANIMATION_DURATION, ANIMATION_INTERPOLATION)));
    }
}
