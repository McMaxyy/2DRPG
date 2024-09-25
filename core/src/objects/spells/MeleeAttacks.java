package objects.spells;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import objects.AttackEntity;

public class MeleeAttacks extends AttackEntity {
    public enum WeaponType {
        SWORD
    }

    private WeaponType type;
    private Body weaponBody;
    private World world;
    private float swingAngle;
    private boolean facingRight;
    
    public MeleeAttacks(WeaponType type, World world, Body playerBody, boolean facingRight) {
    	super(40);
    	this.type = type;
        this.world = world;
        this.facingRight = facingRight;
        createWeapon(playerBody);
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
