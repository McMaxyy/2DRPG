package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import managers.AnimationManager;
import managers.AnimationManager.State;
import objects.GameEntity;

public class Player extends GameEntity {

    private int jumpCounter;
    private AnimationManager animationManager;

    public Player(float width, float height, Body body) {
        super(width, height, body);
        this.speed = 3f;
        this.jumpCounter = 0;
        this.animationManager = new AnimationManager();
    }

    @Override
    public void update() {
        x = body.getPosition().x * 100.0f;
        y = body.getPosition().y * 100.0f;

        checkUserInput();
        updateAnimationState();
        animationManager.update(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.draw(animationManager.getCurrentFrame(), 
                    x - width * 1.25f, y - height / 1.65f, // Position
                    width * 2.5f, height * 1.3f); // Size
        batch.end();
    }

    private void checkUserInput() {
        velX = 0;

        // Movement Input
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velX = 1;
            animationManager.setFacingRight(true);  // Facing right
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velX = -1;
            animationManager.setFacingRight(false); // Facing left
        }

        // Jump Input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && jumpCounter < 2) {
            float force = body.getMass() * 4.5f;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
            jumpCounter++;
        }

        // Reset jumpCounter when player is grounded
        if (body.getLinearVelocity().y == 0) {
            jumpCounter = 0;
        }

        // Set player's velocity based on input
        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 25 ? body.getLinearVelocity().y : 25);
    }

    private void updateAnimationState() {
        // Check if player is jumping
        if (body.getLinearVelocity().y != 0) {
            animationManager.setState(State.JUMPING);
        }
        // Check if player is moving left or right
        else if (velX != 0) {
            animationManager.setState(State.RUNNING);
        }
        // Player is idle
        else {
            animationManager.setState(State.IDLE);
        }
    }
}
