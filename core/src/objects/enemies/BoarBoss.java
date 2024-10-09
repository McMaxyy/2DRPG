package objects.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import config.Storage;
import managers.AnimationManager;
import objects.GameEntity;

public class BoarBoss extends GameEntity {
    private boolean movingLeft = true;
    private float speed = 1.5f;
    private AnimationManager animationManager;
    private boolean isDead;
    public boolean death = false;
    private float deathTimer = 0f;
    private final float RESPAWN_DELAY = 2f;
    public boolean shouldDestroy = false;
    private float deathX, deathY;
    private Storage storage;
    private boolean isMarkedForRemoval = false;

	// Dash and attack variables
    private boolean isDashing = false;
    private float dashCooldown = 5f;  // Dash every 5 seconds
    private float dashTimer = 0f;
    private float dashSpeed = 8f;  // Speed during dash
    private boolean isThrowing = false;
    private Body projectile;  // Custom projectile for BoarBoss
    private World world;  // BoarBoss needs access to the physics world to create the projectile

    public BoarBoss(float width, float height, Body body, World world) {
        super(width, height, body);
        this.animationManager = new AnimationManager();
        this.storage = Storage.getInstance();
        this.world = world;  // Assign world for projectile creation
        death = false;
        setHealth(150, 150);
    }

    @Override
    public void onDeath() {
        die();
    }

    @Override
    public void update(float delta) {
        if (shouldDestroy) {
            return;
        }

        x = body.getPosition().x * 100.0f;
        y = body.getPosition().y * 100.0f;

        // Dash Timer: trigger dash every 5 seconds
        dashTimer += delta;
        if (dashTimer >= dashCooldown && !isDashing && !death) {
            startDash();
        }

        // Handle throwing after dash if applicable
        if (isThrowing) {
            performThrowAttack();
        }

        if (!isDashing && !isThrowing && !death) {
            if (movingLeft) {
                body.setLinearVelocity(new Vector2(-speed, body.getLinearVelocity().y));
                animationManager.setFacingRight(true, "BoarBoss");
            } else {
                body.setLinearVelocity(new Vector2(speed, body.getLinearVelocity().y));
                animationManager.setFacingRight(false, "BoarBoss");
            }
        }
        
        if(projectile != null)
        	if(isMarkedForRemoval)
        		removeProjectile();

        updateStopTimer(delta);

        if (isStopped() && !death && !isDashing) {
            body.setLinearVelocity(velX * speed, velY * speed);
        }

        updateAnimationState();
        animationManager.update(Gdx.graphics.getDeltaTime());
    }

    public void reverseDirection() {
        movingLeft = !movingLeft;
        if (isDashing) {
            stopDash();  // Stop dash when hitting a wall
            startThrowAttack();  // Start the throw attack after the dash
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();

        if (isDead) {
            batch.draw(animationManager.getBoarBossCurrentFrame(),
                    deathX - width * 0.45f, deathY - height / 2f,
                    width * 1f, height * 1f);

        } else {
            batch.draw(animationManager.getBoarBossCurrentFrame(),
                    x - width * 0.45f, y - height / 2f,
                    width * 1f, height * 1.2f);
            drawHealthBar(batch);
        }

        if (projectile != null) {
            batch.draw(Storage.assetManager.get("enemies/BoarBoss/Ball.png", Texture.class),
                    projectile.getPosition().x * 100f - 10f,
                    projectile.getPosition().y * 100f - 10f,
                    20f, 20f);
        }

        batch.end();
    }

    private void updateAnimationState() {
        if (isDead()) {
            if (getAnimationManager().isAnimationFinished("BoarBoss")) {
                deathTimer += Gdx.graphics.getDeltaTime();
                if (deathTimer >= RESPAWN_DELAY) {
                    shouldDestroy = true;
                }
            }
            return;
        } else if (isDashing) {
            getAnimationManager().setState(AnimationManager.State.ATTACK_CHARGE, "BoarBoss");
        } else if (isThrowing) {
            getAnimationManager().setState(AnimationManager.State.ATTACK_THROW, "BoarBoss");
        } else {
            getAnimationManager().setState(AnimationManager.State.RUNNING, "BoarBoss");
        }
    }

    private void startDash() {
        isDashing = true;
        dashTimer = 0f;
        Vector2 dashVelocity = new Vector2(movingLeft ? -dashSpeed : dashSpeed, body.getLinearVelocity().y);
        body.setLinearVelocity(dashVelocity);

        // Set animation to attack charge
        getAnimationManager().setState(AnimationManager.State.ATTACK_CHARGE, "BoarBoss");
    }

    private void stopDash() {
        isDashing = false;
        body.setLinearVelocity(0, body.getLinearVelocity().y);  // Stop movement
        getAnimationManager().setState(AnimationManager.State.RUNNING, "BoarBoss");  // Reset animation to running
    }

    private void startThrowAttack() {
        isThrowing = true;
        getAnimationManager().setState(AnimationManager.State.ATTACK_THROW, "BoarBoss");
    }

    private void performThrowAttack() {
        isThrowing = false;
        createProjectile();
    }

    private void createProjectile() {
        Vector2 projectileDirection;
        float projectileSpeed = 10f;

        if (movingLeft) {
            projectileDirection = new Vector2(-projectileSpeed, 0);
        } else {
            projectileDirection = new Vector2(projectileSpeed, 0);
        }

        // Create a projectile body
        BodyDef projectileBodyDef = new BodyDef();
        projectileBodyDef.type = BodyDef.BodyType.KinematicBody;

        // Offset from the BoarBoss body
        float offsetX = movingLeft ? -1.0f : 1.0f;
        projectileBodyDef.position.set(body.getPosition().x + offsetX, body.getPosition().y);
        projectileBodyDef.bullet = true;

        projectile = world.createBody(projectileBodyDef);

        PolygonShape projectileShape = new PolygonShape();
        projectileShape.setAsBox(0.2f, 0.2f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = projectileShape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = true;  // Ensure the projectile is a sensor so it doesn't collide with the boss itself

        projectile.createFixture(fixtureDef);
        projectileShape.dispose();
        
        projectile.setUserData(this);

        // Set projectile velocity
        projectile.setLinearVelocity(projectileDirection);        
    }

    public void die() {
        if (!isDead) {
            isDead = true;
            death = true;
            getAnimationManager().setState(AnimationManager.State.DYING, "BoarBoss");
            body.setLinearVelocity(0, 0);

            deathX = x;
            deathY = y;
        }
    }

    public boolean isDead() {
        return isDead;
    }

    public AnimationManager getAnimationManager() {
        return animationManager;
    }
    
    public void removeProjectile() {
        if (projectile != null) {
            world.destroyBody(projectile);
            projectile = null;
            this.isMarkedForRemoval = false;
        }
    }
    
    public boolean getProjectile() {
    	if(projectile == null)
    		return true;
    	else
    		return false;
    }

    public void markForRemoval() {
    	this.isMarkedForRemoval = true;
    }
}
