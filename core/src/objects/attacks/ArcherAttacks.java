package objects.attacks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import managers.AnimationManager;
import objects.AttackEntity;

public class ArcherAttacks extends AttackEntity{
	public enum ArrowType {
		BASIC, DOG
	}
	
	private ArrowType type;
	private Body arrowBody;
    private World world;
	private Vector2 velocity;
    private float distanceTraveled;
    private float maxDistance = 650f;
    private AnimationManager animationManager;
    private boolean isMarkedForRemoval = false;
    private boolean facingRight;

	public ArcherAttacks(ArrowType type, World world, Body playerBody, Vector2 targetPosition, AnimationManager animationManager) {
		super(10);
		this.animationManager = animationManager;
        this.type = type;
        this.world = world;
        this.facingRight = getAnimationManager().isFacingRight("PlayerArcher");
        createArrow(playerBody);
	}
	
	private void createArrow(Body playerBody) {
        if (arrowBody != null) {
            return;
        }

        BodyDef arrowBodyDef = new BodyDef();
        arrowBodyDef.type = BodyDef.BodyType.KinematicBody;
        
        float offsetX = facingRight ? 0.5f : -0.5f;  // Positive offset for right, negative for left
        
        arrowBodyDef.position.set(playerBody.getPosition().x + offsetX, playerBody.getPosition().y);
        arrowBodyDef.bullet = true;

        arrowBody = world.createBody(arrowBodyDef);

        PolygonShape arrowShape = new PolygonShape();
        arrowShape.setAsBox(0.2f, 0.2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = arrowShape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = true;

        arrowBody.createFixture(fixtureDef);
        arrowShape.dispose();

        float arrowSpeed = 15f;
        velocity = new Vector2(facingRight ? arrowSpeed : -arrowSpeed, 0);
        arrowBody.setLinearVelocity(velocity);
        arrowBody.setUserData(this);
        distanceTraveled = 0f;
    }
	
	public void update(float deltaTime) {
        if (arrowBody != null && type == ArrowType.BASIC) {
            distanceTraveled += velocity.len() * deltaTime;

            if (distanceTraveled >= maxDistance || isMarkedForRemoval) {
                removeArrow();
            }
        }
    }
	
	public AnimationManager getAnimationManager() {
        return animationManager;
    }

	public void markForRemoval() {
        this.isMarkedForRemoval = true;
    }

    public void removeArrow() {
        if (arrowBody != null) {
        	world.destroyBody(arrowBody);
        	arrowBody = null;
        }
    }

    public ArrowType getType() {
        return type;
    }

    public Body getBody() {
        return arrowBody;
    }
    
    public boolean isFacingRight() {
    	return facingRight;
    }
}
