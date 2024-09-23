package objects.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import managers.AnimationManager;
import objects.GameEntity;

public class Mlem extends GameEntity {
    private boolean movingLeft = true;
    private float speed = 1.2f;
    private AnimationManager animationManager;
    private boolean isDead;
    public boolean mlemDeath = false;
    private float initialX, initialY;
    private float deathTimer = 0f;
    public boolean shouldDestroy = false;
    private final float RESPAWN_DELAY = 2f;
    private float deathX, deathY;

    public Mlem(float width, float height, Body body, float initialX, float initialY) {
        super(width, height, body);
        this.animationManager = new AnimationManager();
        this.initialX = initialX;
        this.initialY = initialY;
    }

    @Override
    public void update() {
    	if(shouldDestroy) {
    		return;
    	}
    	
        x = body.getPosition().x * 100.0f;
        y = body.getPosition().y * 100.0f;
        
        if(!mlemDeath) {
        	if (movingLeft) {
                body.setLinearVelocity(new Vector2(-speed, body.getLinearVelocity().y));
                animationManager.setFacingRight(false, "Mlem");
            } else {
                body.setLinearVelocity(new Vector2(speed, body.getLinearVelocity().y));
                animationManager.setFacingRight(true, "Mlem");
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
            batch.draw(animationManager.getMlemCurrentFrame(), 
                        deathX - width * 1f, deathY - height / 1.9f,
                        width * 2f, height * 1.2f);
            batch.end();
        } else {
        	batch.begin();
            batch.draw(animationManager.getMlemCurrentFrame(), 
                        x - width * 1f, y - height / 1.9f,
                        width * 2f, height * 1.2f);
            batch.end();
        }
    }
    
    private void updateAnimationState() {
    	if (isDead()) {
            if (getAnimationManager().isMlemAnimationFinished()) {
            	deathTimer += Gdx.graphics.getDeltaTime();
                if (deathTimer >= RESPAWN_DELAY) {
                	shouldDestroy = true;
                }           
            }
            return;
        } else
        	getAnimationManager().setMlemState(AnimationManager.MlemState.RUNNING);	
    }
    
    public void checkRespawn() {
        if (isDead && getAnimationManager().isMlemAnimationFinished()) {
            respawn();
        }
    }
    
    private void respawn() {
    	body.setTransform(initialX / 100f, initialY / 100f, 0);
        isDead = false;
        mlemDeath = false;
        deathTimer = 0f;       
        getAnimationManager().setMlemState(AnimationManager.MlemState.RUNNING);		
	}

	public void die() {
		if (!isDead) {
            isDead = true;
            mlemDeath = true;
            getAnimationManager().setMlemState(AnimationManager.MlemState.DYING);
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
