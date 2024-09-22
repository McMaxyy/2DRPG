package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import managers.AnimationManager;
import managers.AnimationManager.State;
import objects.GameEntity;

public class Player extends GameEntity {
    private float initialX, initialY;
    private int jumpCounter;
    private AnimationManager animationManager;
    private boolean isRunning, isDead;
    public static boolean death = false;
    private Body swordBody;
    private World world;
    private float swingAngle;

    public Player(float width, float height, Body body, float initialX, float initialY, World world) {
        super(width, height, body);
        this.speed = 3f;
        this.jumpCounter = 0;
        this.animationManager = new AnimationManager();
        this.initialX = initialX;
        this.initialY = initialY;
        this.world = world;
        this.swordBody = null;
    }
    
    public void respawn() {
    	removeSword();
        body.setTransform(initialX / 100f, initialY / 100f, 0);
        isDead = false;
        death = false;
        jumpCounter = 0;
        getAnimationManager().setState(AnimationManager.State.IDLE);
    }

    @Override
    public void update() {
        x = body.getPosition().x * 100.0f;
        y = body.getPosition().y * 100.0f;

        checkUserInput();
        updateAnimationState();
        getAnimationManager().update(Gdx.graphics.getDeltaTime());
        
        if (getAnimationManager().getState() == AnimationManager.State.ATTACKING) {
            rotateSword(Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void render(SpriteBatch batch) {
    	if(!death) {
    		batch.begin();
            batch.draw(getAnimationManager().getCurrentFrame(), 
                        x - width * 1.25f, y - height / 1.65f,
                        width * 2.5f, height * 1.3f);
            batch.end();
    	}     
    }

    private void checkUserInput() {
    	if(!isDead) {
    		velX = 0;

            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                velX = 1;
                getAnimationManager().setFacingRight(true, "Player");
                isRunning = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                velX = -1;
                getAnimationManager().setFacingRight(false, "Player");
                isRunning = true;
            }

            if (Gdx.input.isTouched(Input.Buttons.LEFT)) {
                getAnimationManager().setState(State.ATTACKING);
                createSword();  
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
    
    private void createSword() {
        if (swordBody != null) {
            return;
        }

        BodyDef swordBodyDef = new BodyDef();
        swordBodyDef.type = BodyDef.BodyType.KinematicBody;
        swordBodyDef.position.set(body.getPosition().x, body.getPosition().y);

        swordBody = world.createBody(swordBodyDef);
        
        PolygonShape swordShape = new PolygonShape();

        float swordOffsetX = getAnimationManager().isFacingRight() ? 0.3f : -0.3f;
        swordShape.setAsBox(0.25f, 0.05f, new Vector2(swordOffsetX, 0), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = swordShape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = true;

        swordBody.createFixture(fixtureDef);
        swordShape.dispose();

        swingAngle = getAnimationManager().isFacingRight() ? 80f : -80f;
    }

    private void rotateSword(float deltaTime) {
        if (swordBody != null) {
            float rotationSpeed = 180f;
            swingAngle += getAnimationManager().isFacingRight() ? -rotationSpeed * deltaTime : rotationSpeed * deltaTime;

            if (getAnimationManager().isFacingRight()) {
                if (swingAngle >= -20) {
                    swordBody.setTransform(body.getPosition(), (float) Math.toRadians(swingAngle));
                } else {
                    removeSword();
                }
            } else {
                if (swingAngle <= 20) {
                    swordBody.setTransform(body.getPosition(), (float) Math.toRadians(swingAngle));
                } else {
                    removeSword();
                }
            }
        }
    }

    private void removeSword() {
        if (swordBody != null) {
            world.destroyBody(swordBody);
            swordBody = null;
        }
    }

    private void updateAnimationState() {
        if (isDead()) {
            if (getAnimationManager().isAnimationFinished()) {
                death = true;
            }
            return;
        }

        if (getAnimationManager().getState() == AnimationManager.State.ATTACKING) {
            if (getAnimationManager().isAnimationFinished()) {
                getAnimationManager().setState(AnimationManager.State.IDLE);
                isRunning = false;
                removeSword();
            }
        } else if (body.getLinearVelocity().y != 0) {
            getAnimationManager().setState(AnimationManager.State.JUMPING);
        } else if (velX != 0) {
            getAnimationManager().setState(AnimationManager.State.RUNNING);
        } else {
            getAnimationManager().setState(AnimationManager.State.IDLE);
            isRunning = false;
        }
    }


    public void die() {
        if (!isDead) {
            isDead = true;
            getAnimationManager().setState(AnimationManager.State.DYING);
            body.setLinearVelocity(0, 0);
        }
    }
    
    public void checkRespawn() {
        if (isDead && getAnimationManager().isAnimationFinished()) {
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
