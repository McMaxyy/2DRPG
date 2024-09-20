package managers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import config.Storage;

public class AnimationManager {
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runningAnimation;
    private Animation<TextureRegion> jumpingAnimation;
    private Animation<TextureRegion> attackingAnimation;
    private Animation<TextureRegion> dyingAnimation;
    private Animation<TextureRegion> pedroRunningAnimation;
    private float animationTime = 0f;
    private float pedroAnimationTime = 0f;
    private boolean facingRight = true, pedroFacingRight = true;   
    public enum State {
        IDLE, RUNNING, JUMPING, ATTACKING, DYING
    }
    public enum PedroState {
    	RUNNING
    }
    private State currentState = State.IDLE;
    private PedroState pedroCurrentState = PedroState.RUNNING;

    public AnimationManager() {
    	loadEnemyAnimations();
        loadPlayerAnimations();       
    }

    private void loadEnemyAnimations() {
    	try {
            Array<TextureRegion> runningFrames = new Array<>();
            for (int i = 0; i < 12; i++) {
                Texture runningFrame;
                if (i < 10) {
                    runningFrame = Storage.assetManager.get("enemies/Pedro/Walking/Wraith_02_Moving Forward_00" + i + ".png", Texture.class);
                } else {
                    runningFrame = Storage.assetManager.get("enemies/Pedro/Walking/Wraith_02_Moving Forward_0" + i + ".png", Texture.class);
                }
                runningFrame.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                runningFrames.add(new TextureRegion(runningFrame));
            }
            pedroRunningAnimation = new Animation<>(0.06f, runningFrames, Animation.PlayMode.LOOP);

        } catch (GdxRuntimeException e) {
            System.out.println("Failed to load Pedro animation frames.");
        }		
	}

	private void loadPlayerAnimations() {
        try {
            // Idle
            Array<TextureRegion> idleFrames = new Array<>();
            for (int i = 0; i < 12; i++) {
                Texture idleFrame;
                if (i < 10) {
                    idleFrame = Storage.assetManager.get("character/Idle/Minotaur_01_Idle_00" + i + ".png", Texture.class);
                } else {
                    idleFrame = Storage.assetManager.get("character/Idle/Minotaur_01_Idle_0" + i + ".png", Texture.class);
                }
                idleFrame.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                idleFrames.add(new TextureRegion(idleFrame));
            }
            idleAnimation = new Animation<>(0.09f, idleFrames, Animation.PlayMode.LOOP);

            // Running
            Array<TextureRegion> runningFrames = new Array<>();
            for (int i = 0; i < 18; i++) {
                Texture runningFrame;
                if (i < 10) {
                    runningFrame = Storage.assetManager.get("character/Walking/Minotaur_01_Walking_00" + i + ".png", Texture.class);
                } else {
                    runningFrame = Storage.assetManager.get("character/Walking/Minotaur_01_Walking_0" + i + ".png", Texture.class);
                }
                runningFrame.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                runningFrames.add(new TextureRegion(runningFrame));
            }
            runningAnimation = new Animation<>(0.06f, runningFrames, Animation.PlayMode.LOOP);
            
            // Attacking	
            Array<TextureRegion> attackingFrames = new Array<>();
            for (int i = 0; i < 12; i++) {
                Texture attackingFrame;
                if (i < 10) {
                	attackingFrame = Storage.assetManager.get("character/Attacking/Minotaur_01_Attacking_00" + i + ".png", Texture.class);
                } else {
                	attackingFrame = Storage.assetManager.get("character/Attacking/Minotaur_01_Attacking_0" + i + ".png", Texture.class);
                }
                attackingFrame.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                attackingFrames.add(new TextureRegion(attackingFrame));
            }
            attackingAnimation = new Animation<>(0.05f, attackingFrames, Animation.PlayMode.NORMAL);
            
         // Dying	
            Array<TextureRegion> dyingFrames = new Array<>();
            for (int i = 0; i < 15; i++) {
                Texture dyingFrame;
                if (i < 10) {
                	dyingFrame = Storage.assetManager.get("character/Dying/Minotaur_01_Dying_00" + i + ".png", Texture.class);
                } else {
                	dyingFrame = Storage.assetManager.get("character/Dying/Minotaur_01_Dying_0" + i + ".png", Texture.class);
                }
                dyingFrame.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                dyingFrames.add(new TextureRegion(dyingFrame));
            }
            dyingAnimation = new Animation<>(0.075f, dyingFrames, Animation.PlayMode.NORMAL);

            // Jumping
            Array<TextureRegion> jumpingFrames = new Array<>();
            for (int i = 0; i < 6; i++) {
                Texture jumpingFrame = Storage.assetManager.get("character/Jump Loop/Minotaur_01_Jump Loop_00" + i + ".png", Texture.class);
                jumpingFrame.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                jumpingFrames.add(new TextureRegion(jumpingFrame));
            }
            jumpingAnimation = new Animation<>(0.075f, jumpingFrames, Animation.PlayMode.LOOP);

        } catch (GdxRuntimeException e) {
            System.out.println("Failed to load animation frames.");
        }
    }


	public void update(float delta) {
        // Update both player and Pedro animation times
        animationTime += delta;
        pedroAnimationTime += delta;
    }

    public void setFacingRight(boolean isFacingRight) {
        this.facingRight = isFacingRight;
    }

    public void setPedroFacingRight(boolean isFacingRight) {
        this.pedroFacingRight = isFacingRight;
    }

    public void setState(State newState) {
        if (newState != currentState) {
            currentState = newState;
            resetAnimationTime();
        }
    }

    public void setPedroState(PedroState newState) {
        if (newState != pedroCurrentState) {
            pedroCurrentState = newState;
            resetPedroAnimationTime();
        }
    }

    public State getState() {
        return currentState;
    }

    public PedroState getPedroState() {
        return pedroCurrentState;
    }

    public TextureRegion getCurrentFrame() {
        Animation<TextureRegion> currentAnimation;

        switch (currentState) {
        	case DYING:
        		currentAnimation = dyingAnimation;
        		break;
            case ATTACKING:
                currentAnimation = attackingAnimation;
                break;
            case RUNNING:
                currentAnimation = runningAnimation;
                break;
            case JUMPING:
                currentAnimation = jumpingAnimation;
                break;
            case IDLE:
            default:
                currentAnimation = idleAnimation;
                break;
        }

        TextureRegion currentFrame = currentAnimation.getKeyFrame(animationTime);

        if (facingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!facingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        return currentFrame;
    }

    public TextureRegion getPedroCurrentFrame() {
        TextureRegion currentFrame = pedroRunningAnimation.getKeyFrame(pedroAnimationTime);

        if (pedroFacingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!pedroFacingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        return currentFrame;
    }

    public void resetAnimationTime() {
        animationTime = 0f;
    }

    public void resetPedroAnimationTime() {
        pedroAnimationTime = 0f;
    }

    public boolean isAnimationFinished() {
        switch (currentState) {
            case ATTACKING:
                return attackingAnimation.isAnimationFinished(animationTime);
            case DYING:
                return dyingAnimation.isAnimationFinished(animationTime);
            case RUNNING:
                return runningAnimation.isAnimationFinished(animationTime);
            case JUMPING:
                return jumpingAnimation.isAnimationFinished(animationTime);
            case IDLE:
            default:
                return idleAnimation.isAnimationFinished(animationTime);
        }
    }

    public boolean isPedroAnimationFinished() {
        return pedroRunningAnimation.isAnimationFinished(pedroAnimationTime);
    }
}