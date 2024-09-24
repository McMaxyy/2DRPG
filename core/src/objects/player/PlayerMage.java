package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import managers.AnimationManager;
import managers.AnimationManager.State;
import managers.AnimationManager.vfxState;
import objects.GameEntity;

public class PlayerMage extends GameEntity {
    private float initialX, initialY;
    private int jumpCounter;
    private AnimationManager animationManager;
    private boolean isRunning, isDead;
    public static boolean death = false;
    private World world;
    private Body lightningBody;

    public PlayerMage(float width, float height, Body body, float initialX, float initialY, World world) {
        super(width, height, body);
        this.speed = 3f;
        this.jumpCounter = 0;
        this.animationManager = new AnimationManager();
        this.initialX = initialX;
        this.initialY = initialY;
        this.world = world;
    }
    
    public void respawn() {
        body.setTransform(initialX / 100f, initialY / 100f, 0);
        isDead = false;
        death = false;
        jumpCounter = 0;
        getAnimationManager().setState(AnimationManager.State.IDLE, "Pedro");
    }

    @Override
    public void update() {
        x = body.getPosition().x * 100.0f;
        y = body.getPosition().y * 100.0f;

        checkUserInput();
        updateAnimationState();
        getAnimationManager().update(Gdx.graphics.getDeltaTime());
        
        if (getAnimationManager().isAnimationFinished("VFX")) {
            removeLightning();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
    	if(!death) {
    		batch.begin();
            batch.draw(getAnimationManager().getPedroCurrentFrame(), 
                        x - width * 1.25f, y - height / 1.4f,
                        width * 2.5f, height * 1.3f);
            
            if (getAnimationManager().getState() == vfxState.LIGHTNING) {
                TextureRegion lightningFrame = getAnimationManager().getVfxCurrentFrame();
                if (lightningFrame != null && lightningBody != null) {
                    float lightningX = lightningBody.getPosition().x * 100f;
                    float lightningY = lightningBody.getPosition().y * 100f;
                    batch.draw(lightningFrame, 
                               lightningX - 40f / 2, lightningY - 50f / 2, 
                               40f, 220f);
                }
            }
            
            batch.end();
    	}     
    }

    private void checkUserInput() {
    	if(!isDead) {
    		velX = 0;

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                velX = 1;
                getAnimationManager().setFacingRight(true, "Pedro");
                isRunning = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                velX = -1;
                getAnimationManager().setFacingRight(false, "Pedro");
                isRunning = true;
            }

            if (Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT) && lightningBody == null) {
                getAnimationManager().setState(State.ATTACKING, "Pedro");                
                createLightning();
                getAnimationManager().setState(vfxState.LIGHTNING);
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
    }
    
    private void createLightning() {
        if (lightningBody != null) {
            return;
        }

        Body nearestEnemyBody = findNearestDynamicBodyInRange(5f);
        Vector2 lightningPosition = body.getPosition();

        if (nearestEnemyBody != null) {
            lightningPosition = nearestEnemyBody.getPosition();
            lightningPosition.add(0, -0.15f);
        } else {
            float lightningOffsetX = getAnimationManager().isFacingRight("Pedro") ? 0.5f : -0.5f;
            lightningPosition.add(lightningOffsetX, -0.15f);
        }

        BodyDef lightningBodyDef = new BodyDef();
        lightningBodyDef.type = BodyDef.BodyType.KinematicBody;
        lightningBodyDef.position.set(lightningPosition);

        lightningBody = world.createBody(lightningBodyDef);
        
        PolygonShape lightningShape = new PolygonShape();
        lightningShape.setAsBox(0.2f, 0.2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = lightningShape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = true;

        lightningBody.createFixture(fixtureDef);
        lightningShape.dispose();
    }

    private Body findNearestDynamicBodyInRange(float range) {
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);

        Body nearestBody = null;
        float nearestDistance = range * range;

        float playerY = this.body.getPosition().y;
        float playerHeight = this.height;
        float heightTolerance = playerHeight / 300f;

        for (Body body : bodies) {
            if (body.getType() == BodyType.DynamicBody && body != this.body) {
                Vector2 bodyPos = body.getPosition();
                Vector2 playerPos = this.body.getPosition();

                if (Math.abs(bodyPos.y - playerY) <= heightTolerance) {

                    float distanceSquared = playerPos.dst2(bodyPos);

                    if (distanceSquared <= nearestDistance) {
                        nearestBody = body;
                        nearestDistance = distanceSquared;
                    }
                }
            }
        }

        return nearestBody;
    }

    private void removeLightning() {
        if (lightningBody != null) {
            world.destroyBody(lightningBody);
            lightningBody = null;
            getAnimationManager().setState(vfxState.NULL);
        }
    }

    private void updateAnimationState() {
        if (isDead()) {
            if (getAnimationManager().isAnimationFinished("Pedro")) {
                death = true;
            }
            return;
        }

        if (getAnimationManager().getState("Pedro") == AnimationManager.State.ATTACKING) {
            if (getAnimationManager().isAnimationFinished("Pedro")) {
                getAnimationManager().setState(AnimationManager.State.IDLE, "Pedro");
                isRunning = false;
                removeLightning();
            }
        } else if (body.getLinearVelocity().y != 0) {
            getAnimationManager().setState(AnimationManager.State.RUNNING, "Pedro");
        } else if (velX != 0) {
            getAnimationManager().setState(AnimationManager.State.RUNNING, "Pedro");
        } else {
            getAnimationManager().setState(AnimationManager.State.IDLE, "Pedro");
            isRunning = false;
        }
    }


    public void die() {
        if (!isDead) {
            isDead = true;
            getAnimationManager().setState(AnimationManager.State.DYING, "Pedro");
            body.setLinearVelocity(0, 0);
        }
    }
    
    public void checkRespawn() {
        if (isDead && getAnimationManager().isAnimationFinished("Pedro")) {
            respawn();
        }
    }
    
    public boolean isDead() {
    	return isDead;
    }

	public AnimationManager getAnimationManager() {
		return animationManager;
	}
}
