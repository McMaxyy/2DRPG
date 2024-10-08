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
		BASIC, DOG, DOG_ATTACK
	}
	
	private ArrowType type;
	private Body arrowBody, dogFollower, dogAttack;
    private World world;
	private Vector2 velocity;
    private float distanceTraveled;
    private float maxDistance = 650f;
    private AnimationManager animationManager;
    private boolean isMarkedForRemoval = false;
    private boolean facingRight;

	public ArcherAttacks(ArrowType type, World world, Body playerBody, Vector2 targetPosition, AnimationManager animationManager) {
		super(type == ArrowType.BASIC ? 10 : type == ArrowType.DOG ? 0 : 20);
		this.animationManager = animationManager;
        this.type = type;
        this.world = world;
        this.facingRight = getAnimationManager().isFacingRight("PlayerArcher");
        switch(type) {
        case BASIC:
        	createArrow(playerBody);
        	break;
        case DOG:
        	setDogFollower(createDog(playerBody));
        	break;
        case DOG_ATTACK:
        	dogAttack(playerBody, targetPosition);
        	break;
        }
	}
	
	private void dogAttack(Body playerBody, Vector2 targetPosition) {
        if (dogAttack != null)
            return;

        BodyDef dogAttackBodyDef = new BodyDef();
        dogAttackBodyDef.type = BodyDef.BodyType.KinematicBody;
        dogAttackBodyDef.position.set(targetPosition);

        dogAttack = world.createBody(dogAttackBodyDef);

        PolygonShape attackShape = new PolygonShape();
        attackShape.setAsBox(0.4f, 0.3f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = attackShape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = true;

        dogAttack.createFixture(fixtureDef);
        attackShape.dispose();

        dogAttack.setUserData(this);
    }
	
	private Body createDog(Body body) {
		if(getDogFollower() != null)
			return null;
		
		BodyDef dogBodyDef = new BodyDef();
		dogBodyDef.type = BodyDef.BodyType.DynamicBody;
		
		float offsetX = getAnimationManager().isFacingRight("PlayerArcher") ? 0.4f : -0.4f;
		
		dogBodyDef.position.set(body.getPosition().x + offsetX, body.getPosition().y);
		
		setDogFollower(world.createBody(dogBodyDef));
		
		PolygonShape dogShape = new PolygonShape();
		dogShape.setAsBox(0.3f, 0.2f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = dogShape;
		fixtureDef.density = 1f;
		fixtureDef.isSensor = true;
		
		getDogFollower().createFixture(fixtureDef);
		dogShape.dispose();
		
		getDogFollower().setUserData(this.getDogFollower());
		
		return getDogFollower();
	}

	private void createArrow(Body playerBody) {
        if (arrowBody != null) {
            return;
        }

        BodyDef arrowBodyDef = new BodyDef();
        arrowBodyDef.type = BodyDef.BodyType.KinematicBody;
        
        float offsetX = facingRight ? 0.5f : -0.5f;
        
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
                removeArrow("Arrow");
            }
        }
        else if(dogAttack != null && type == ArrowType.DOG_ATTACK) {
        	if(isMarkedForRemoval)
        		removeArrow("DogAttack");
        }
    }
	
	public AnimationManager getAnimationManager() {
        return animationManager;
    }

	public void markForRemoval() {
        this.isMarkedForRemoval = true;
    }

    public void removeArrow(String attack) {
    	switch(attack) {
    	case "Arrow":
    		if (arrowBody != null) {
            	world.destroyBody(arrowBody);
            	arrowBody = null;
            }
    		break;
    	case "DogAttack":
    		if (dogAttack != null) {
            	world.destroyBody(dogAttack);
            	dogAttack = null;
            }
    		break;
    	}
    }

    public ArrowType getType() {
        return type;
    }

    public Body getBody(String body) {
        switch (body) {
            case "Arrow":
                return arrowBody;
            case "Dog":
                return dogFollower;
            case "DogAttack":
                return dogAttack;
            default:
                return null;
        }
    }
    
    public boolean isFacingRight() {
    	return facingRight;
    }

	public Body getDogFollower() {
		return dogFollower;
	}

	public void setDogFollower(Body dogFollower) {
		this.dogFollower = dogFollower;
	}
}
