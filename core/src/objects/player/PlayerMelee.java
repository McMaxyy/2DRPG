// PlayerMelee.java (refactored)

package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import managers.AnimationManager;
import managers.AnimationManager.State;
import objects.GameEntity;
import objects.spells.MeleeAttacks;

public class PlayerMelee extends GameEntity {
    private float initialX, initialY;
    private int jumpCounter;
    private AnimationManager animationManager;
    private boolean isRunning, isDead;
    public static boolean death = false;
    private MeleeAttacks weapon;
    private World world;

    public PlayerMelee(float width, float height, Body body, float initialX, float initialY, World world) {
        super(width, height, body);
        this.speed = 3f;
        this.jumpCounter = 0;
        this.animationManager = new AnimationManager();
        this.initialX = initialX;
        this.initialY = initialY;
        this.world = world;
        this.weapon = null;
        setHealth(100, 100);
    }
    
    @Override
    protected void onDeath() {
        die();
        setHealth(100, 100);
    }
    
    public void respawn() {
        if (weapon != null) {
            weapon.removeWeapon();
        }
        body.setTransform(initialX / 100f, initialY / 100f, 0);
        isDead = false;
        death = false;
        jumpCounter = 0;
        getAnimationManager().setState(AnimationManager.State.IDLE, "PlayerMelee");
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * 100.0f;
        y = body.getPosition().y * 100.0f;

        checkUserInput();
        updateAnimationState();
        getAnimationManager().update(Gdx.graphics.getDeltaTime());

        if (getAnimationManager().getState("PlayerMelee") == AnimationManager.State.ATTACKING && weapon != null) {
            weapon.rotateWeapon(Gdx.graphics.getDeltaTime(), body);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!death) {
            batch.begin();
            
            drawHealthBar(batch);
            batch.draw(getAnimationManager().getCurrentFrame(), 
                        x - width * 1.25f, y - height / 1.65f,
                        width * 2.5f, height * 1.3f);
            batch.end();
        }
    }

    private void checkUserInput() {
        if (!isDead) {
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
                getAnimationManager().setState(State.ATTACKING, "PlayerMelee");
                createWeapon();  
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

    private void createWeapon() {
        if (weapon == null) {
            weapon = new MeleeAttacks(MeleeAttacks.WeaponType.SWORD, world, body, getAnimationManager().isFacingRight("PlayerMelee"));
        }
    }

    private void updateAnimationState() {
        if (isDead()) {
            if (getAnimationManager().isAnimationFinished("PlayerMelee")) {
                death = true;
            }
            return;
        }

        if (getAnimationManager().getState("PlayerMelee") == AnimationManager.State.ATTACKING) {
            if (getAnimationManager().isAnimationFinished("PlayerMelee")) {
                getAnimationManager().setState(AnimationManager.State.IDLE, "PlayerMelee");
                isRunning = false;
                if (weapon != null) {
                    weapon.removeWeapon();
                    weapon = null;
                }
            }
        } else if (body.getLinearVelocity().y != 0) {
            getAnimationManager().setState(AnimationManager.State.JUMPING, "PlayerMelee");
        } else if (velX != 0) {
            getAnimationManager().setState(AnimationManager.State.RUNNING, "PlayerMelee");
        } else {
            getAnimationManager().setState(AnimationManager.State.IDLE, "PlayerMelee");
            isRunning = false;
        }
    }

    public void die() {
        if (!isDead) {
            isDead = true;
            getAnimationManager().setState(AnimationManager.State.DYING, "PlayerMelee");
            body.setLinearVelocity(0, 0);
        }
    }

    public void checkRespawn() {
        if (isDead && getAnimationManager().isAnimationFinished("PlayerMelee")) {
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
