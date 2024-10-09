package objects.attacks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import managers.AnimationManager;
import objects.AttackEntity;

public class SpellAttacks extends AttackEntity{
    public enum SpellType {
        LIGHTNING, FIREBALL
    }

    private SpellType type;
    private Body spellBody;
    private World world;
    private Vector2 velocity;
    private float distanceTraveled;
    private float maxDistance = 500f;
    private AnimationManager animationManager;
    private boolean isMarkedForRemoval = false;
    private boolean facingRight;
    private static int lightningCost = 20, fireballCost = 10;

    public SpellAttacks(SpellType type, World world, Body playerBody, Vector2 targetPosition, AnimationManager animationManager) {
    	super(type == SpellType.FIREBALL ? 30 : 50);
    	this.animationManager = animationManager;
        this.type = type;
        this.world = world;
        this.facingRight = getAnimationManager().isFacingRight("Pedro");
        if (type == SpellType.LIGHTNING) {
            createLightningSpell(playerBody, targetPosition);
        } else if (type == SpellType.FIREBALL) {
            createFireballSpell(playerBody);
        }
    }
    
    public static int getSpellCost(String spell) {
    	switch(spell) {
    	case "Lightning":
    		return lightningCost;
    	case "Fireball":
    		return fireballCost;
    	default:
    		return 0;
    	}
    }

	private void createFireballSpell(Body playerBody) {
        if (spellBody != null) {
            return;
        }

        BodyDef spellBodyDef = new BodyDef();
        spellBodyDef.type = BodyDef.BodyType.KinematicBody;
        
        float offsetX = facingRight ? 0.5f : -0.5f; 
        
        spellBodyDef.position.set(playerBody.getPosition().x + offsetX, playerBody.getPosition().y);
        spellBodyDef.bullet = true;

        spellBody = world.createBody(spellBodyDef);

        PolygonShape fireballShape = new PolygonShape();
        fireballShape.setAsBox(0.2f, 0.2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = fireballShape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = true;

        spellBody.createFixture(fixtureDef);
        fireballShape.dispose();

        float fireballSpeed = 10f;
        velocity = new Vector2(facingRight ? fireballSpeed : -fireballSpeed, 0);
        spellBody.setLinearVelocity(velocity);
        spellBody.setUserData(this);
        distanceTraveled = 0f;
    }

    public AnimationManager getAnimationManager() {
        return animationManager;
    }

	private void createLightningSpell(Body playerBody, Vector2 targetPosition) {
        if (spellBody != null) {
            return;
        }

        BodyDef spellBodyDef = new BodyDef();
        spellBodyDef.type = BodyDef.BodyType.KinematicBody;
        spellBodyDef.position.set(targetPosition);

        spellBody = world.createBody(spellBodyDef);

        PolygonShape spellShape = new PolygonShape();
        spellShape.setAsBox(0.2f, 0.2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = spellShape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = true;

        spellBody.createFixture(fixtureDef);
        spellShape.dispose();
        
        spellBody.setUserData(this);
    }

    public void update(float deltaTime) {
        if (spellBody != null && type == SpellType.FIREBALL) {
            distanceTraveled += velocity.len() * deltaTime;

            if (distanceTraveled >= maxDistance || isMarkedForRemoval) {
                removeSpell();
            }
        }
    }
    
    public void markForRemoval() {
        this.isMarkedForRemoval = true;
    }

    public void removeSpell() {
        if (spellBody != null) {
            world.destroyBody(spellBody);
            spellBody = null;           
        }
    }

    public SpellType getType() {
        return type;
    }

    public Body getBody() {
        return spellBody;
    }
    
    public boolean isFacingRight() {
    	return facingRight;
    }
}
