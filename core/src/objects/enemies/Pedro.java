package objects.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import managers.AnimationManager;
import objects.GameEntity;

public class Pedro extends GameEntity {
    private boolean movingLeft = true;
    private float speed = 1.5f;
    private AnimationManager animationManager;
    private boolean isDead;
    public boolean pedroDeath = false;
    public boolean colliding = true;
    private float initialX, initialY;
    private float deathTimer = 0f;
    private final float RESPAWN_DELAY = 2f;
    public boolean shouldDestroy = false;
    private float deathX, deathY;

    public Pedro(float width, float height, Body body, float initialX, float initialY) {
        super(width, height, body);
        this.animationManager = new AnimationManager();
        this.initialX = initialX;
        this.initialY = initialY;
        pedroDeath = false;
    }

    @Override
    public void update() { 
    	if(shouldDestroy) {
    		return;
    	}
    	
        x = body.getPosition().x * 100.0f;
        y = body.getPosition().y * 100.0f;
        
        if(!pedroDeath) {
        	if (movingLeft) {
                body.setLinearVelocity(new Vector2(-speed, body.getLinearVelocity().y));
                animationManager.setFacingRight(false, "Pedro");
            } else {
                body.setLinearVelocity(new Vector2(speed, body.getLinearVelocity().y));
                animationManager.setFacingRight(true, "Pedro");
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
    	if (isDead) {
            batch.begin();
            batch.draw(animationManager.getPedroCurrentFrame(),
                    deathX - width * 1f, deathY - height / 1.25f,
                    width * 2f, height * 1.45f);
            batch.end();
        } else {
            batch.begin();
            batch.draw(animationManager.getPedroCurrentFrame(),
                    x - width * 1f, y - height / 1.25f,
                    width * 2f, height * 1.45f);
            batch.end();
        }
    }
    
    private void updateAnimationState() {
    	if (isDead()) {
            if (getAnimationManager().isAnimationFinished("Pedro")) {           	
                deathTimer += Gdx.graphics.getDeltaTime();
                if (deathTimer >= RESPAWN_DELAY) {
                	shouldDestroy = true;
                }             
            }
            return;
        } else
        	getAnimationManager().setState(AnimationManager.State.RUNNING, "Pedro");	
    }
    
    public void checkRespawn() {
        if (isDead && getAnimationManager().isAnimationFinished("Pedro")) {
            respawn();
        }
    }
    
    private void respawn() {
    	body.setTransform(initialX / 100f, initialY / 100f, 0);
        isDead = false;
        pedroDeath = false;
        deathTimer = 0f;       
        getAnimationManager().setState(AnimationManager.State.RUNNING, "Pedro");		
	}

	public void die() {
		if (!isDead) {
            isDead = true;
            pedroDeath = true;
            getAnimationManager().setState(AnimationManager.State.DYING, "Pedro");
            body.setLinearVelocity(0, 0);
            
            deathX = x;
            deathY = y;
            for (Fixture fixture : body.getFixtureList()) {
                fixture.setSensor(true);
            }
        }
    }
    
    public boolean isDead() {
		return isDead;
	}

	public AnimationManager getAnimationManager() {
		return animationManager;
	}
}
