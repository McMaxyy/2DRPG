package objects.spells;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;

import objects.AttackEntity;
import objects.player.PlayerMelee;

public class MeleeAttacks extends AttackEntity {
    public enum WeaponType {
        SWORD, CHARGE
    }

    private WeaponType type;
    private Body weaponBody, dashBody;
    private World world;
    private float swingAngle;
    private boolean facingRight;
    private float dashSpeed = 20f;
    private PlayerMelee player;
    
    public MeleeAttacks(WeaponType type, World world, Body playerBody, boolean facingRight, PlayerMelee player) {
    	super(type == WeaponType.SWORD ? 40 : 10);
    	this.type = type;
        this.world = world;
        this.facingRight = facingRight;
        this.player = player;
        if(type == WeaponType.SWORD)
        	createWeapon(playerBody);
        else
        	createDash(playerBody);
    }
    
    public void createDash(Body playerBody) {
        Vector2 dashVelocity = new Vector2(facingRight ? dashSpeed : -dashSpeed, playerBody.getLinearVelocity().y);
        playerBody.setLinearVelocity(dashVelocity);

        BodyDef dashBodyDef = new BodyDef();
        dashBodyDef.type = BodyDef.BodyType.KinematicBody;
        
        float offset = 0.3f; 
        dashBodyDef.position.set(playerBody.getPosition().x + (facingRight ? offset : -offset), playerBody.getPosition().y);

        dashBody = world.createBody(dashBodyDef);

        PolygonShape dashShape = new PolygonShape();
        float dashWidth = 0.3f;
        float dashHeight = 0.3f;
        dashShape.setAsBox(dashWidth, dashHeight);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = dashShape;
        fixtureDef.isSensor = true;
        fixtureDef.density = 1f;

        dashBody.createFixture(fixtureDef);
        dashShape.dispose();

        dashBody.setUserData(this);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (dashBody != null && world != null) {
                    world.destroyBody(dashBody);
                    dashBody = null;
                }
            }
        }, 0.15f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (dashBody != null && playerBody != null) {
                    Vector2 playerPosition = playerBody.getPosition();
                    dashBody.setTransform(new Vector2(
                        playerPosition.x + (facingRight ? offset : -offset),
                        playerPosition.y), dashBody.getAngle());
                }
            }
        }, 0, 1 / 60f); 
    }

    public void createWeapon(Body playerBody) {
        if (weaponBody != null) {
            return;
        }

        BodyDef weaponBodyDef = new BodyDef();
        weaponBodyDef.type = BodyDef.BodyType.KinematicBody;
        weaponBodyDef.position.set(playerBody.getPosition().x, playerBody.getPosition().y);

        weaponBody = world.createBody(weaponBodyDef);
        
        PolygonShape weaponShape = new PolygonShape();
        float weaponOffsetX = facingRight ? 0.5f : -0.5f;
        weaponShape.setAsBox(0.25f, 0.05f, new Vector2(weaponOffsetX, -0.1f), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = weaponShape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = true;

        weaponBody.createFixture(fixtureDef);
        weaponShape.dispose();

        swingAngle = facingRight ? 80f : -80f;
        
        weaponBody.setUserData(this);
    }

    public void rotateWeapon(float deltaTime, Body playerBody) {
        if (weaponBody != null) {
            float rotationSpeed = 180f;
            swingAngle += facingRight ? -rotationSpeed * deltaTime : rotationSpeed * deltaTime;

            if (facingRight) {
                if (swingAngle >= -20) {
                    weaponBody.setTransform(playerBody.getPosition(), (float) Math.toRadians(swingAngle));
                } else {
                    removeWeapon();
                }
            } else {
                if (swingAngle <= 20) {
                    weaponBody.setTransform(playerBody.getPosition(), (float) Math.toRadians(swingAngle));
                } else {
                    removeWeapon();
                }
            }
        }
    }

    public void removeWeapon() {
        if (weaponBody != null) {
            world.destroyBody(weaponBody);
            weaponBody = null;
        }
    }

    public WeaponType getType() {
        return type;
    }
}
