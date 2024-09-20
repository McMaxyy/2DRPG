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
    private float animationTime = 0f;
    private boolean facingRight = true;   
    public enum State {
        IDLE, RUNNING, JUMPING
    }
    private State currentState = State.IDLE;

    public AnimationManager() {
        loadAnimations();
    }

    private void loadAnimations() {
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
    }

    public void setFacingRight(boolean isFacingRight) {
        this.facingRight = isFacingRight;
    }
    
    public void setState(State newState) {
        if (newState != currentState) {
            currentState = newState;
            resetAnimationTime();
        }
    }
    
    public State getState() {
    	return currentState;
    }

    public TextureRegion getCurrentFrame() {
        Animation<TextureRegion> currentAnimation;
        
        switch (currentState) {
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

    public void resetAnimationTime() {
        animationTime = 0f;
    }

	public boolean isFacingRight() {
		return facingRight;
	}
}
