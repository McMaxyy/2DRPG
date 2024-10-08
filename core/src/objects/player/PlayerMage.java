// PlayerMage.java (refactored)

package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import config.Storage;
import managers.AnimationManager;
import managers.AnimationManager.State;
import managers.AnimationManager.vfxState;
import objects.GameEntity;
import objects.attacks.SpellAttacks;

public class PlayerMage extends GameEntity {
    private float initialX, initialY;
    private int jumpCounter;
    private AnimationManager animationManager;
    private boolean isRunning, isDead;
    public static boolean death = false;
    private World world;
    private SpellAttacks spell;
    private int mana = 50, maxMana = 50;

    public PlayerMage(float width, float height, Body body, float initialX, float initialY, World world) {
        super(width, height, body);
        this.speed = 3f;
        this.jumpCounter = 0;
        this.animationManager = new AnimationManager();
        this.initialX = initialX;
        this.initialY = initialY;
        this.world = world;
        setHealth(100, 100);
        setMana(mana, maxMana);
    }
    
    public void respawn() {
        if (spell != null) {
            spell.removeSpell();
        }
        body.setTransform(initialX / 100f, initialY / 100f, 0);
        isDead = false;
        death = false;
        jumpCounter = 0;
        getAnimationManager().setState(AnimationManager.State.IDLE, "Pedro");
    }
    
    @Override
    protected void onDeath() {
        die();        
    }
    
    public void resetMana() {
    	setMana(mana, maxMana);
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * 100.0f;
        y = body.getPosition().y * 100.0f;

        checkUserInput();
        updateAnimationState();
        getAnimationManager().update(Gdx.graphics.getDeltaTime());

        if (spell != null) {
            spell.update(Gdx.graphics.getDeltaTime());

            if (getAnimationManager().isAnimationFinished("VFX")) {
                spell.removeSpell();
                spell = null;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!death) {
            batch.begin();
            batch.draw(getAnimationManager().getPedroCurrentFrame(), 
                        x - width * 1.25f, y - height / 1.4f,
                        width * 2.5f, height * 1.3f);
            
            if(Storage.getLevelNum() != 0) {
            	drawHealthBar(batch);
                drawManaBar(batch);
            }           
            
            if (getAnimationManager().getState() == vfxState.LIGHTNING && spell != null) {
                TextureRegion lightningFrame = getAnimationManager().getVfxCurrentFrame();
                if (lightningFrame != null && spell.getBody() != null) {
                    float lightningX = spell.getBody().getPosition().x * 100f;
                    float lightningY = spell.getBody().getPosition().y * 100f;
                    batch.draw(lightningFrame, 
                               lightningX - 40f / 2, lightningY - 50f / 2, 
                               40f, 220f);
                }
            }
            
            if (getAnimationManager().getState() == vfxState.FIREBALL && spell != null) {
                TextureRegion fireballFrame = getAnimationManager().getVfxCurrentFrame();
                if (fireballFrame != null && spell.getBody() != null) {
                    float fireballX = spell.getBody().getPosition().x * 100f;
                    float fireballY = spell.getBody().getPosition().y * 100f;
                    
                    // Flip the fireball texture if the player is facing left
                    boolean flipX = !spell.isFacingRight();
                    if (fireballFrame.isFlipX() != flipX) {
                        fireballFrame.flip(true, false);
                    }

                    batch.draw(fireballFrame, 
                               fireballX - 45f, fireballY - 35f, 
                               80f, 80f);
                }
            }
            
            batch.end();
        }     
    }

    private void checkUserInput() {
        if (!isDead) {
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
            
            if (Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && spell == null && this.getMana() >= SpellAttacks.getSpellCost("Fireball")) {
                getAnimationManager().setState(State.ATTACKING, "Pedro");
                castFireball();   
                this.loseMana(SpellAttacks.getSpellCost("Fireball"));
                getAnimationManager().setState(vfxState.FIREBALL);
            }

            if (Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT) && spell == null && this.getMana() >= SpellAttacks.getSpellCost("Lightning")) {
                getAnimationManager().setState(State.ATTACKING, "Pedro");                           
                castLightning();
                this.loseMana(SpellAttacks.getSpellCost("Lightning"));
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
    
    private void castFireball() {
        Vector2 targetPosition = body.getPosition();
        spell = new SpellAttacks(SpellAttacks.SpellType.FIREBALL, world, body, targetPosition, getAnimationManager());
    }

    private void castLightning() {
        Body nearestEnemyBody = findNearestDynamicBodyInRange(5f);
        Vector2 targetPosition = body.getPosition();

        if (nearestEnemyBody != null) {
            targetPosition = nearestEnemyBody.getPosition().cpy().add(0, -0.15f);
        } else {
            float lightningOffsetX = getAnimationManager().isFacingRight("Pedro") ? 0.5f : -0.5f;
            targetPosition.add(lightningOffsetX, -0.15f);
        }

        spell = new SpellAttacks(SpellAttacks.SpellType.LIGHTNING, world, body, targetPosition, getAnimationManager());
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
            if (body.getType() == BodyDef.BodyType.DynamicBody && body != this.body) {
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
                if (spell != null) {
                    spell.removeSpell();
                    spell = null;
                }
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
            
            if (spell != null) {
                spell.removeSpell();
                spell = null;
            }
            
            setHealth(100, 100);
            setMana(50, 50);
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
