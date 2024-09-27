package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
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
import objects.GameEntity;
import objects.attacks.ArcherAttacks;
import objects.attacks.SpellAttacks;

public class PlayerArcher extends GameEntity {
    private float initialX, initialY;
    private int jumpCounter;
    private AnimationManager animationManager;
    private boolean isRunning, isDead, isShooting, canShoot = true;
    public static boolean death = false;
    private World world;
    private ArcherAttacks arrow;
    private int mana = 50, maxMana = 50;
    private final float ARROW_DELAY = 0.2f;
    private float arrowDelayTimer = 0f;

    public PlayerArcher(float width, float height, Body body, float initialX, float initialY, World world) {
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
    	if (arrow != null) {
    		arrow.removeArrow();
        }
        body.setTransform(initialX / 100f, initialY / 100f, 0);
        isDead = false;
        death = false;
        jumpCounter = 0;
        getAnimationManager().setState(AnimationManager.State.IDLE, "PlayerArcher");
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
        
        if (arrow != null) {
        	arrow.update(Gdx.graphics.getDeltaTime());
        	
        	if(arrow.getBody() == null)
        		arrow = null;
        }
        
        if(isShooting) {
        	delayShot();
        }
        
        if(getAnimationManager().isAnimationFinished("PlayerArcher"))
        	canShoot = true;
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!death) {
            batch.begin();
            batch.draw(getAnimationManager().getArcherCurrentFrame(), 
                        x - width * 1.2f, y - height / 1.3f,
                        width * 2.3f, height * 1.5f);
            
            drawHealthBar(batch);
            drawManaBar(batch);
            
            if (arrow != null) {
                Texture arrowTex = Storage.assetManager.get("character/Archer/Arrow.png");
                
                if (arrowTex != null && arrow.getBody() != null) {
                    TextureRegion arrowRegion = new TextureRegion(arrowTex);
                    
                    float arrowX = arrow.getBody().getPosition().x * 100f;
                    float arrowY = arrow.getBody().getPosition().y * 100f;
                    
                    boolean flipX = !arrow.isFacingRight();
                    
                    if (arrowRegion.isFlipX() != flipX) {
                        arrowRegion.flip(true, false);
                    }

                    batch.draw(arrowRegion, arrowX - 45f, arrowY - 25f, 20f, 20f);
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
                getAnimationManager().setFacingRight(true, "PlayerArcher");
                isRunning = true;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                velX = -1;
                getAnimationManager().setFacingRight(false, "PlayerArcher");
                isRunning = true;
            }
            
            if (Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                getAnimationManager().setState(State.ATTACKING, "PlayerArcher");
            }

            if (Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT) && arrow == null && canShoot) {
                getAnimationManager().setState(State.ATTACKING, "PlayerArcher"); 
                isShooting = true; 
                canShoot = false;               
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
    
    private void shootArrow() {
    	isShooting = false;
    	Vector2 targetPosition = body.getPosition();
        arrow = new ArcherAttacks(ArcherAttacks.ArrowType.BASIC, world, body, targetPosition, getAnimationManager());
        arrowDelayTimer = 0f;
    }
    
    private void delayShot() {
    	arrowDelayTimer += Gdx.graphics.getDeltaTime();
        if (arrowDelayTimer >= ARROW_DELAY) {
        	shootArrow();
        } 
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
            if (getAnimationManager().isAnimationFinished("PlayerArcher")) {
                death = true;
            }
            return;
        }

        if (getAnimationManager().getState("PlayerArcher") == AnimationManager.State.ATTACKING) {
            if (getAnimationManager().isAnimationFinished("PlayerArcher")) {
                getAnimationManager().setState(AnimationManager.State.IDLE, "PlayerArcher");
                isRunning = false;
                if(arrow != null) {
                	arrow.removeArrow();
                	arrow = null;
                }
            }
        } else if (body.getLinearVelocity().y != 0) {
            getAnimationManager().setState(AnimationManager.State.JUMPING, "PlayerArcher");
        } else if (velX != 0) {
            getAnimationManager().setState(AnimationManager.State.RUNNING, "PlayerArcher");
        } else {
            getAnimationManager().setState(AnimationManager.State.IDLE, "PlayerArcher");
            isRunning = false;
        }
    }

    public void die() {
        if (!isDead) {
            isDead = true;
            getAnimationManager().setState(AnimationManager.State.DYING, "PlayerArcher");
            body.setLinearVelocity(0, 0);
            
            setHealth(100, 100);
            setMana(50, 50);
        }
    }

    public void checkRespawn() {
        if (isDead && getAnimationManager().isAnimationFinished("PlayerArcher")) {
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
