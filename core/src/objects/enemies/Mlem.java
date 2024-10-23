package objects.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;

import config.Storage;
import managers.AnimationManager;
import objects.GameEntity;

public class Mlem extends GameEntity {
    private boolean movingLeft = true;
    private float speed = 1.2f;
    private AnimationManager animationManager;
    private boolean isDead;
    public boolean death = false;
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
        death = false;
        setHealth(50, 50);
    }
    
    @Override
    public void onDeath() {
    	die();
    }

    @Override
    public void update(float delta) {
    	if(shouldDestroy) {
    		return;
    	}
    	
        x = body.getPosition().x * 100.0f;
        y = body.getPosition().y * 100.0f;
        
        if(!death) {
        	if (movingLeft) {
                body.setLinearVelocity(new Vector2(-speed, body.getLinearVelocity().y));
                animationManager.setFacingRight(false, "Mlem");
            } else {
                body.setLinearVelocity(new Vector2(speed, body.getLinearVelocity().y));
                animationManager.setFacingRight(true, "Mlem");
            }
        }
        
        updateStopTimer(delta);

        if (isStopped() && !death) {
            body.setLinearVelocity(velX * speed, velY * speed);
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
    	
    	if (isDead) {
            batch.draw(animationManager.getMlemCurrentFrame(), 
                        deathX - width * 1f, deathY - height / 1.9f,
                        width * 2f, height * 0.9f);
        } else {
            batch.draw(animationManager.getMlemCurrentFrame(), 
                        x - width * 1f, y - height / 1.9f,
                        width * 2f, height * 0.8f);
            drawHealthBar(batch);
        }
    	
    	batch.end();
    }
    
    private void updateAnimationState() {
    	if (isDead()) {
            if (getAnimationManager().isAnimationFinished("Mlem")) {
            	deathTimer += Gdx.graphics.getDeltaTime();
                if (deathTimer >= RESPAWN_DELAY) {
                	shouldDestroy = true;
                	Storage.setInvulnerable(false);
                }           
            }
            return;
        } else
        	getAnimationManager().setState(AnimationManager.State.RUNNING, "Mlem");	
    }
    
    public void checkRespawn() {
        if (isDead && getAnimationManager().isAnimationFinished("Mlem")) {
            respawn();
        }
    }
    
    private void respawn() {
    	body.setTransform(initialX / 100f, initialY / 100f, 0);
        isDead = false;
        death = false;
        deathTimer = 0f;       
        getAnimationManager().setState(AnimationManager.State.RUNNING, "Mlem");		
	}

	public void die() {
		if (!isDead) {
			Storage.setInvulnerable(true);
            isDead = true;
            death = true;
            getAnimationManager().setState(AnimationManager.State.DYING, "Mlem");
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
