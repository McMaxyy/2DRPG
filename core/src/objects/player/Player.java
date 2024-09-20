package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import game.GameProj;
import managers.AnimationManager;
import managers.AnimationManager.State;
import objects.GameEntity;

public class Player extends GameEntity {

    private int jumpCounter;
    private AnimationManager animationManager;
    private boolean isRunning, isDead;

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
                    x - width * 1.25f, y - height / 1.65f,
                    width * 2.5f, height * 1.3f);
        batch.end();
    }

    private void checkUserInput() {
        velX = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velX = 1;
            animationManager.setFacingRight(true);
            isRunning = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velX = -1;
            animationManager.setFacingRight(false);
            isRunning = true;
        }
        
        if(Gdx.input.isTouched(Input.Buttons.LEFT) && !isRunning) {
        	animationManager.setState(State.ATTACKING);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && jumpCounter < 2) {
            float force = body.getMass() * 4.5f;
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, force), body.getPosition(), true);
            jumpCounter++;
        }

        if (body.getLinearVelocity().y == 0) {
            jumpCounter = 0;
        }

        body.setLinearVelocity(velX * speed, body.getLinearVelocity().y < 25 ? body.getLinearVelocity().y : 25);
    }

    private void updateAnimationState() {
        if (animationManager.getState() == AnimationManager.State.ATTACKING) {
            if (animationManager.isAnimationFinished()) {
                animationManager.setState(AnimationManager.State.IDLE);
                isRunning = false;
            }
        } else if (animationManager.getState() == AnimationManager.State.DYING) {
            if (animationManager.isAnimationFinished()) {
            	animationManager.setState(AnimationManager.State.IDLE);
            }
        } else if (body.getLinearVelocity().y != 0) {
            animationManager.setState(AnimationManager.State.JUMPING);
        } else if (velX != 0) {
            animationManager.setState(AnimationManager.State.RUNNING);
        } else {
            animationManager.setState(AnimationManager.State.IDLE);
            isRunning = false;
        }
    }

    public void die() {
    	animationManager.setState(AnimationManager.State.DYING);
    	isDead = true;
    }
}
