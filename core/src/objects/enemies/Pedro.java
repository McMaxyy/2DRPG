package objects.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;

import managers.AnimationManager;

import com.badlogic.gdx.math.Vector2;
import objects.GameEntity;

public class Pedro extends GameEntity {
    private boolean movingLeft = true;
    private float speed = 1.5f;
    private AnimationManager animationManager;

    public Pedro(float width, float height, Body body) {
        super(width, height, body);
        this.animationManager = new AnimationManager();
    }

    @Override
    public void update() {
        x = body.getPosition().x * 100.0f;
        y = body.getPosition().y * 100.0f;

        if (movingLeft) {
            body.setLinearVelocity(new Vector2(-speed, body.getLinearVelocity().y));
            animationManager.setPedroFacingRight(false);
        } else {
            body.setLinearVelocity(new Vector2(speed, body.getLinearVelocity().y));
            animationManager.setPedroFacingRight(true);
        }
        
        animationManager.update(Gdx.graphics.getDeltaTime());
    }

    public void reverseDirection() {
        movingLeft = !movingLeft;
    }

    @Override
    public void render(SpriteBatch batch) {
    	batch.begin();
        batch.draw(animationManager.getPedroCurrentFrame(), 
                    x - width * 1f, y - height / 1.25f,
                    width * 2f, height * 1.45f);
        batch.end();
    }
}
