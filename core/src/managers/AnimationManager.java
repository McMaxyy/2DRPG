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
    private Animation<TextureRegion> pedroDyingAnimation;
    private Animation<TextureRegion> mlemRunningAnimation;
    private Animation<TextureRegion> mlemDyingAnimation;

    private float animationTime = 0f;
    private float pedroAnimationTime = 0f;
    private float mlemAnimationTime = 0f;
    private boolean facingRight = true, pedroFacingRight = true, mlemFacingRight = true;   
    public enum State {
        IDLE, RUNNING, JUMPING, ATTACKING, DYING
    }
    public enum PedroState {
    	RUNNING, DYING
    }
    public enum MlemState {
    	RUNNING, DYING
    }
    private State currentState = State.IDLE;
    private PedroState pedroCurrentState = PedroState.RUNNING;
    private MlemState mlemCurrentState = MlemState.RUNNING;

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
            
            Array<TextureRegion> dyingFrames = new Array<>();
            for (int i = 0; i < 15; i++) {
                Texture dyingFrame;
                if (i < 10) {
                	dyingFrame = Storage.assetManager.get("enemies/Pedro/Dying/Wraith_02_Dying_00" + i + ".png", Texture.class);
                } else {
                	dyingFrame = Storage.assetManager.get("enemies/Pedro/Dying/Wraith_02_Dying_0" + i + ".png", Texture.class);
                }
                dyingFrame.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                dyingFrames.add(new TextureRegion(dyingFrame));
            }
            pedroDyingAnimation = new Animation<>(0.06f, dyingFrames, Animation.PlayMode.NORMAL);

            // Mlem walking
            Texture mlemWalkingTexture = Storage.assetManager.get("enemies/Mlem/Walking.png", Texture.class);
            mlemWalkingTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] mlemWalkFrames = TextureRegion.split(mlemWalkingTexture, mlemWalkingTexture.getWidth() / 8, mlemWalkingTexture.getHeight());
            Array<TextureRegion> mlemWalkingFrames = new Array<>();
            for (int i = 0; i < 8; i++) {
                mlemWalkingFrames.add(mlemWalkFrames[0][i]);
            }
            mlemRunningAnimation = new Animation<>(0.1f, mlemWalkingFrames, Animation.PlayMode.LOOP);
            
            // Mlem dying
            Texture mlemDyingTexture = Storage.assetManager.get("enemies/Mlem/Dying.png", Texture.class);
            mlemDyingTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] mlemDieFrames = TextureRegion.split(mlemDyingTexture, mlemDyingTexture.getWidth() / 3, mlemDyingTexture.getHeight());
            Array<TextureRegion> mlemDyingFrames = new Array<>();
            for (int i = 0; i < 3; i++) {
            	mlemDyingFrames.add(mlemDieFrames[0][i]);
            }
            mlemDyingAnimation = new Animation<>(0.05f, mlemDyingFrames, Animation.PlayMode.NORMAL);
            
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
        animationTime += delta;
        pedroAnimationTime += delta;
        mlemAnimationTime += delta;
    }

    public void setFacingRight(boolean isFacingRight, String entity) {
    	switch(entity) {
    	case "Player":
    		this.facingRight = isFacingRight;
    		break;
    	case "Pedro":
    		this.pedroFacingRight = isFacingRight;
    		break;
    	case "Mlem":
    		this.mlemFacingRight = isFacingRight;
    		break;
    	}       
    }

    public void setState(State newState) {
        if (newState != currentState) {
            currentState = newState;
            animationTime = 0f;
        }
    }

    public void setPedroState(PedroState newState) {
        if (newState != pedroCurrentState) {
            pedroCurrentState = newState;
            pedroAnimationTime = 0f;
        }
    }
    
    public void setMlemState(MlemState newState) {
        if (newState != mlemCurrentState) {
            mlemCurrentState = newState;
            mlemAnimationTime = 0f;
        }
    }

    public State getState() {
        return currentState;
    }

    public PedroState getPedroState() {
        return pedroCurrentState;
    }
    
    public MlemState getMlemState() {
    	return mlemCurrentState;
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
        Animation<TextureRegion> currentPedroAnimation;

        switch (pedroCurrentState) {
            case DYING:
                currentPedroAnimation = pedroDyingAnimation;
                break;
            case RUNNING:
                currentPedroAnimation = pedroRunningAnimation;
                break;
            default:
                currentPedroAnimation = pedroRunningAnimation;
                break;
        }

        TextureRegion currentPedroFrame = currentPedroAnimation.getKeyFrame(pedroAnimationTime);

        if (pedroFacingRight && currentPedroFrame.isFlipX()) {
            currentPedroFrame.flip(true, false);
        } else if (!pedroFacingRight && !currentPedroFrame.isFlipX()) {
            currentPedroFrame.flip(true, false);
        }

        return currentPedroFrame;
    }
    
    public TextureRegion getMlemCurrentFrame() {
        Animation<TextureRegion> currentMlemAnimation;

        switch (mlemCurrentState) {
            case DYING:
            	currentMlemAnimation = mlemDyingAnimation;
                break;
            case RUNNING:
            	currentMlemAnimation = mlemRunningAnimation;
                break;
            default:
            	currentMlemAnimation = mlemRunningAnimation;
                break;
        }

        TextureRegion currentMlemFrame = currentMlemAnimation.getKeyFrame(mlemAnimationTime);

        if (mlemFacingRight && currentMlemFrame.isFlipX()) {
            currentMlemFrame.flip(true, false);
        } else if (!mlemFacingRight && !currentMlemFrame.isFlipX()) {
        	currentMlemFrame.flip(true, false);
        }

        return currentMlemFrame;
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
        switch (pedroCurrentState) {
	        case DYING:
	            return pedroDyingAnimation.isAnimationFinished(pedroAnimationTime);
	        case RUNNING:
	            return pedroRunningAnimation.isAnimationFinished(pedroAnimationTime);
	        default:
            return pedroRunningAnimation.isAnimationFinished(pedroAnimationTime);
        }
    }
    
    public boolean isMlemAnimationFinished() {
        switch (mlemCurrentState) {
	        case DYING:
	            return mlemDyingAnimation.isAnimationFinished(mlemAnimationTime);
	        case RUNNING:
	            return mlemRunningAnimation.isAnimationFinished(mlemAnimationTime);
	        default:
            return mlemRunningAnimation.isAnimationFinished(mlemAnimationTime);
        }
    }

	public boolean isFacingRight() {
		return facingRight;
	}
}