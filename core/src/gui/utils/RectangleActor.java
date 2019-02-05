package gui.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Cette classe sert à créer des rectangles
 * Provient de : https://stackoverflow.com/questions/15397074/libgdx-how-to-draw-filled-rectangle-in-the-right-place-in-scene2d
 * author Rod Hyde
 */
public class RectangleActor extends Actor {

    private ShapeRenderer shapeRenderer;
    static private boolean projectionMatrixSet;

    public RectangleActor(){
        shapeRenderer = new ShapeRenderer();
        projectionMatrixSet = false;
    }

    @Override
    public void draw(Batch batch, float alpha){
        batch.end();
        if(!projectionMatrixSet){
            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        }
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(getColor());
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        shapeRenderer.end();
        batch.begin();

    }
}
