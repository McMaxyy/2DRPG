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
import objects.Coin;
import objects.GameEntity;
import objects.attacks.ArcherAttacks;

public class PlayerArcher extends GameEntity {
    private float initialX, initialY;
    private int jumpCounter;
    private AnimationManager animationManager;
    private boolean isRunning, isDead, isShooting, canShoot = true;
    public static boolean death = false;
    private World world;
    private ArcherAttacks arrow, dog, dogAttack;
    private int mana = 50, maxMana = 50;
    private final float ARROW_DELAY = 0.2f, DOG_DELAY = 0.7f;
    private float arrowDelayTimer = 0f, dogDelayTimer = 0f;
    private Body dogFollower;
    private boolean dogAttacking = false;
    private Body nearestEnemyBody;

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
        
        if(dogFollower == null) {
        	dog = new ArcherAttacks(ArcherAttacks.ArrowType.DOG, world, body, body.getPosition(), getAnimationManager());
        }
    }

	public void respawn() {
    	if (arrow != null) {
    		arrow.removeArrow("Arrow");
        }
        body.setTransform(initialX / 100f, initialY / 100f, 0);
        isDead = false;
        death = false;
        jumpCounter = 0;
        getAnimationManager().setState(AnimationManager.State.IDLE, "PlayerArcher");
	}
    
    @Override
    protected void onDeath() {
    	Storage.setPlayerDead(true);          
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
        
        nearestEnemyBody = findNearestDynamicBodyInRange(5f);
        
        if(dogAttacking) {
        	dogAttack();
        }
        
        updateDogPosition();
        
        if (arrow != null) {
        	arrow.update(Gdx.graphics.getDeltaTime());
        	
        	if(arrow.getBody("Arrow") == null)
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
            
            if(dog.getDogFollower() != null) {
            	batch.draw(getAnimationManager().getDogCurrentFrame(), 
            			dog.getDogFollower().getPosition().x * 100 - width * 0.9f, 
            			dog.getDogFollower().getPosition().y * 100 - height / 2f, 
                        width * 1.7f, height * 1.1f);
            }  
            
            if(Storage.getLevelNum() != 0) {
            	drawHealthBar(batch);
                drawManaBar(batch);
            }  
            
            if (arrow != null) {
                Texture arrowTex = Storage.assetManager.get("character/Archer/Arrow.png");
                
                if (arrowTex != null && arrow.getBody("Arrow") != null) {
                    TextureRegion arrowRegion = new TextureRegion(arrowTex);
                    
                    float arrowX = arrow.getBody("Arrow").getPosition().x * 100f;
                    float arrowY = arrow.getBody("Arrow").getPosition().y * 100f;
                    
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
    
    private void updateDogPosition() {
        float offsetX = getAnimationManager().isFacingRight("PlayerArcher") ? 0.4f : -0.4f;      

        getAnimationManager().setFacingRight(getAnimationManager().isFacingRight("PlayerArcher"), "DogFollower");

        if (!dogAttacking) {
            dog.getDogFollower().setTransform(body.getPosition().x + offsetX, body.getPosition().y, 0);

            AnimationManager.State playerState = getAnimationManager().getState("PlayerArcher");

            switch (playerState) {
                case RUNNING:
                    getAnimationManager().setState(AnimationManager.State.RUNNING, "DogFollower");
                    break;
                case JUMPING:
                    getAnimationManager().setState(AnimationManager.State.JUMPING, "DogFollower");
                    break;
                case DYING:
                    getAnimationManager().setState(AnimationManager.State.DYING, "DogFollower");
                    break;
                case IDLE:
                default:
                    getAnimationManager().setState(AnimationManager.State.IDLE, "DogFollower");
                    break;
            }
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
            
            if (Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && nearestEnemyBody != null && !dogAttacking && this.getMana() >= ArcherAttacks.getAttackCost("DogAttack")) {
            	if(Storage.getLevelNum() != 0)
                	this.loseMana(ArcherAttacks.getAttackCost("DogAttack"));
            	dogAttack = new ArcherAttacks(ArcherAttacks.ArrowType.DOG_ATTACK, world, body, nearestEnemyBody.getPosition(), getAnimationManager());
            	dogAttacking = true;              
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
    
    private void returnDog() {
        dogDelayTimer += Gdx.graphics.getDeltaTime();
        if (dogDelayTimer >= DOG_DELAY) {
            dogAttacking = false;
            nearestEnemyBody = null;
            dogDelayTimer = 0f;
            dogAttack.removeArrow("DogAttack");
        }       
    }
    
    private void dogAttack() {           	
        if (dogAttacking && nearestEnemyBody != null) {          
            getAnimationManager().setState(AnimationManager.State.ATTACKING, "DogFollower");
            if(getAnimationManager().isFacingRight("PlayerArcher"))
                dog.getDogFollower().setTransform(nearestEnemyBody.getPosition().x - 0.5f, nearestEnemyBody.getPosition().y + 0.1f, 0);
            else
                dog.getDogFollower().setTransform(nearestEnemyBody.getPosition().x + 0.4f, nearestEnemyBody.getPosition().y + 0.1f, 0);

            returnDog();
        }
        else {
        	dogAttacking = false;    
        	dogDelayTimer = 0f;
        	dogAttack.removeArrow("DogAttack");
        }   
    }
    
    private Body findNearestDynamicBodyInRange(float range) {
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);

        Body nearestBody = null;
        float nearestDistance = range * range;

        float playerX = this.body.getPosition().x;
        float playerY = this.body.getPosition().y;
        float playerHeight = this.height;
        boolean playerFacingRight = getAnimationManager().isFacingRight("PlayerArcher");
        float heightTolerance = playerHeight / 300f;

        for (Body body : bodies) {
            if (body.getType() == BodyDef.BodyType.DynamicBody && body != this.body 
            		&& body != dog.getDogFollower() && !(body.getUserData() instanceof Coin)) {
                Vector2 bodyPos = body.getPosition();
                Vector2 playerPos = this.body.getPosition();

                boolean isInFacingDirection = (playerFacingRight && bodyPos.x > playerX) || (!playerFacingRight && bodyPos.x < playerX);

                if (isInFacingDirection && Math.abs(bodyPos.y - playerY) <= heightTolerance) {
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
                	arrow.removeArrow("Arrow");
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
            
            if (arrow != null) {
            	arrow.removeArrow("Arrow");
            	arrow = null;
            }
            
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
