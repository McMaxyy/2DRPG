package objects.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Filter;

import managers.AnimationManager;
import objects.GameEntity;

public class Pedro extends GameEntity {
    private boolean movingLeft = true;
    private float speed = 1.5f;
    private AnimationManager animationManager;
    private boolean isDead;
    public static boolean pedroDeath = false;
    private float initialX, initialY;
    private float deathTimer = 0f;
    private final float RESPAWN_DELAY = 2f;

    public Pedro(float width, float height, Body body, float initialX, float initialY) {
        super(width, height, body);
        this.animationManager = new AnimationManager();
        this.initialX = initialX;
        this.initialY = initialY;
    }

    @Override
    public void update() {    	
        x = body.getPosition().x * 100.0f;
        y = body.getPosition().y * 100.0f;
        
        if(!pedroDeath) {
        	if (movingLeft) {
                body.setLinearVelocity(new Vector2(-speed, body.getLinearVelocity().y));
                animationManager.setPedroFacingRight(false);
            } else {
                body.setLinearVelocity(new Vector2(speed, body.getLinearVelocity().y));
                animationManager.setPedroFacingRight(true);
            }
        }
                
        updateAnimationState();
        
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
    
    private void updateAnimationState() {
    	if (isDead()) {
            if (getAnimationManager().isPedroAnimationFinished()) {
                pedroDeath = true;
                deathTimer += Gdx.graphics.getDeltaTime();
                if (deathTimer >= RESPAWN_DELAY) {
                	checkRespawn();
                }             
            }
            return;
        } else
        	getAnimationManager().setPedroState(AnimationManager.PedroState.RUNNING);	
    }
    
    public void checkRespawn() {
        if (isDead && getAnimationManager().isPedroAnimationFinished()) {
            respawn();
        }
    }
    
    private void respawn() {
    	body.setTransform(initialX / 100f, initialY / 100f, 0);
        isDead = false;
        pedroDeath = false;
        deathTimer = 0f;       
        getAnimationManager().setPedroState(AnimationManager.PedroState.RUNNING);		
	}

	public void die() {
		if (!isDead) {
            isDead = true;
            pedroDeath = true;
            getAnimationManager().setPedroState(AnimationManager.PedroState.DYING);
            body.setLinearVelocity(0, 0);
        }
    }
    
    public boolean isDead() {
		return isDead;
	}

	public AnimationManager getAnimationManager() {
		return animationManager;
	}
}
