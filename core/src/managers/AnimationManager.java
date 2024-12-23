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
    private Animation<TextureRegion> pedroAttackingAnimation;
    private Animation<TextureRegion> pedroIdleAnimation;
    private Animation<TextureRegion> mlemRunningAnimation;
    private Animation<TextureRegion> mlemDyingAnimation;
    private Animation<TextureRegion> lightningAnimation;
    private Animation<TextureRegion> fireballAnimation;
    private Animation<TextureRegion> peepeeRunningAnimation;
    private Animation<TextureRegion> peepeeDyingAnimation;
    private Animation<TextureRegion> archerIdleAnimation;
    private Animation<TextureRegion> archerRunningAnimation;
    private Animation<TextureRegion> archerJumpingAnimation;
    private Animation<TextureRegion> archerAttackingAnimation;
    private Animation<TextureRegion> archerDyingAnimation;
    private Animation<TextureRegion> dogIdleAnimation;
    private Animation<TextureRegion> dogRunningAnimation;
    private Animation<TextureRegion> dogJumpingAnimation;
    private Animation<TextureRegion> dogAttackingAnimation;
    private Animation<TextureRegion> dogDyingAnimation;
    private Animation<TextureRegion> boarBossIdleAnimation;
    private Animation<TextureRegion> boarBossRunningAnimation;
    private Animation<TextureRegion> boarBossHurtAnimation;
    private Animation<TextureRegion> boarBossAttackingChargeAnimation;
    private Animation<TextureRegion> boarBossAttackingThrowAnimation;
    private Animation<TextureRegion> boarBossDyingAnimation;

    private float animationTime = 0f;
    private float pedroAnimationTime = 0f;
    private float peepeeAnimationTime = 0f;
    private float mlemAnimationTime = 0f;
    private float vfxAnimationTime = 0f;
    private float archerAnimationTime = 0f;
    private float dogAnimationTime = 0f;
    private float boarBossAnimationTime = 0f;
    private boolean facingRight, pedroFacingRight, mlemFacingRight, peepeeFacingRight, 
    archerFacingRight, dogFacingRight, boarBossFacingRight;   
    public enum State {
        IDLE, RUNNING, JUMPING, ATTACKING, DYING, HURT, ATTACK_CHARGE, ATTACK_THROW
    }
    public enum vfxState {
    	NULL, LIGHTNING, FIREBALL
    }
    private State currentState = State.IDLE;
    private State pedroCurrentState = State.RUNNING;
    private State mlemCurrentState = State.RUNNING;
    private State peepeeCurrentState = State.RUNNING;
    private State archerCurrentState = State.IDLE;
    private State dogCurrentState = State.IDLE;
    private State boarBossCurrentState = State.IDLE;
    private vfxState vfxCurrentState = vfxState.NULL;

    public AnimationManager() {
    	loadEnemyAnimations();
        loadPlayerAnimations();   
        loadFXAnimations();
    }

    private void loadFXAnimations() {
    	Texture lightningTex = Storage.assetManager.get("effects/Lightning.png", Texture.class);
    	lightningTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
        TextureRegion[][] lightningFrames = TextureRegion.split(lightningTex, lightningTex.getWidth() / 5, lightningTex.getHeight());
        Array<TextureRegion> lightningFrame = new Array<>();
        for (int i = 0; i < 5; i++) {
        	lightningFrame.add(lightningFrames[0][i]);
        }
        lightningAnimation = new Animation<>(0.05f, lightningFrame, Animation.PlayMode.NORMAL);
        
        Texture fireballTex = Storage.assetManager.get("effects/Fireball.png", Texture.class);
    	fireballTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
        TextureRegion[][] fireballFrames = TextureRegion.split(fireballTex, fireballTex.getWidth() / 8, fireballTex.getHeight());
        Array<TextureRegion> fireballFrame = new Array<>();
        for (int i = 0; i < 8; i++) {
        	fireballFrame.add(fireballFrames[0][i]);
        }
        fireballAnimation = new Animation<>(0.1f, fireballFrame, Animation.PlayMode.NORMAL);
	}

	private void loadEnemyAnimations() {
    	try {         
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
            
            // Peepee running
    		Texture peepeeRunningTex = Storage.assetManager.get("enemies/Peepee/Walking.png", Texture.class);
    		peepeeRunningTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] peepeeRunningFrames = TextureRegion.split(peepeeRunningTex, peepeeRunningTex.getWidth() / 9, peepeeRunningTex.getHeight());
            Array<TextureRegion> peepeeRunningFrame = new Array<>();
            for (int i = 0; i < 9; i++) {
            	peepeeRunningFrame.add(peepeeRunningFrames[0][i]);
            }
            peepeeRunningAnimation = new Animation<>(0.06f, peepeeRunningFrame, Animation.PlayMode.LOOP);
            
            // Peepee dying
            Texture peepeeDyingTex = Storage.assetManager.get("enemies/Peepee/Dying.png", Texture.class);
            peepeeDyingTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] peepeeDyingFrames = TextureRegion.split(peepeeDyingTex, peepeeDyingTex.getWidth() / 2, peepeeDyingTex.getHeight());
            Array<TextureRegion> peepeeDyingFrame = new Array<>();
            for (int i = 0; i < 2; i++) {
            	peepeeDyingFrame.add(peepeeDyingFrames[0][i]);
            }
            peepeeDyingAnimation = new Animation<>(0.07f, peepeeDyingFrame, Animation.PlayMode.NORMAL);

            // Boar Boss running
    		Texture boarBossRunningTex = Storage.assetManager.get("enemies/BoarBoss/Walking.png", Texture.class);
    		boarBossRunningTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] boarBossRunningFrames = TextureRegion.split(boarBossRunningTex, boarBossRunningTex.getWidth() / 6, boarBossRunningTex.getHeight());
            Array<TextureRegion> boarBossRunningFrame = new Array<>();
            for (int i = 0; i < 6; i++) {
            	boarBossRunningFrame.add(boarBossRunningFrames[0][i]);
            }
            boarBossRunningAnimation = new Animation<>(0.08f, boarBossRunningFrame, Animation.PlayMode.LOOP);
            
            // Boar Boss dying
            Texture boarBossDyingTex = Storage.assetManager.get("enemies/BoarBoss/Dying.png", Texture.class);
            boarBossDyingTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] boarBossDyingFrames = TextureRegion.split(boarBossDyingTex, boarBossDyingTex.getWidth() / 4, boarBossDyingTex.getHeight());
            Array<TextureRegion> boarBossDyingFrame = new Array<>();
            for (int i = 0; i < 4; i++) {
            	boarBossDyingFrame.add(boarBossDyingFrames[0][i]);
            }
            boarBossDyingAnimation = new Animation<>(0.1f, boarBossDyingFrame, Animation.PlayMode.NORMAL);

            // Boar Boss attacking charge
            Texture boarBossAttackingTex = Storage.assetManager.get("enemies/BoarBoss/AttackCharge.png", Texture.class);
            boarBossAttackingTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] boarBossAttackFrames = TextureRegion.split(boarBossAttackingTex, boarBossAttackingTex.getWidth() / 6, boarBossAttackingTex.getHeight());
            Array<TextureRegion> boarBossAttackingFrame = new Array<>();
            for (int i = 0; i < 6; i++) {
            	boarBossAttackingFrame.add(boarBossAttackFrames[0][i]);
            }
            boarBossAttackingChargeAnimation = new Animation<>(0.075f, boarBossAttackingFrame, Animation.PlayMode.NORMAL);
            
            // Boar Boss idle
            Texture boarBossIdleTex = Storage.assetManager.get("enemies/BoarBoss/Idle.png", Texture.class);
            boarBossIdleTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] boarBossIdleFrames = TextureRegion.split(boarBossIdleTex, boarBossIdleTex.getWidth() / 4, boarBossAttackingTex.getHeight());
            Array<TextureRegion> boarBossIdleFrame = new Array<>();
            for (int i = 0; i < 4; i++) {
            	boarBossIdleFrame.add(boarBossIdleFrames[0][i]);
            }
            boarBossIdleAnimation = new Animation<>(0.1f, boarBossIdleFrame, Animation.PlayMode.LOOP);
            
            // Boar Boss attacking throw
            Texture boarBossAttack2Tex = Storage.assetManager.get("enemies/BoarBoss/AttackThrow.png", Texture.class);
            boarBossAttack2Tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] boarBossAttack2Frames = TextureRegion.split(boarBossAttack2Tex, boarBossAttack2Tex.getWidth() / 4, boarBossAttack2Tex.getHeight());
            Array<TextureRegion> boarBossAttack2Frame = new Array<>();
            for (int i = 0; i < 4; i++) {
            	boarBossAttack2Frame.add(boarBossAttack2Frames[0][i]);
            }
            boarBossAttackingThrowAnimation = new Animation<>(0.075f, boarBossAttackingFrame, Animation.PlayMode.NORMAL);
            
            // Boar Boss hurt
            Texture boarBossHurtTex = Storage.assetManager.get("enemies/BoarBoss/Hurt.png", Texture.class);
            boarBossHurtTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] boarBossHurtFrames = TextureRegion.split(boarBossHurtTex, boarBossHurtTex.getWidth() / 4, boarBossAttackingTex.getHeight());
            Array<TextureRegion> boarBossHurtFrame = new Array<>();
            for (int i = 0; i < 4; i++) {
            	boarBossHurtFrame.add(boarBossHurtFrames[0][i]);
            }
            boarBossHurtAnimation = new Animation<>(0.1f, boarBossHurtFrame, Animation.PlayMode.NORMAL);
            
        } catch (GdxRuntimeException e) {
            System.out.println("Failed to load Pedro animation frames.");
        }		
	}

	private void loadPlayerAnimations() {
        try {
        	// Pedro running
    		Texture pedroRunningTex = Storage.assetManager.get("enemies/Pedro/Walking.png", Texture.class);
    		pedroRunningTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] pedroRunningFrames = TextureRegion.split(pedroRunningTex, pedroRunningTex.getWidth() / 12, pedroRunningTex.getHeight());
            Array<TextureRegion> pedroRunningFrame = new Array<>();
            for (int i = 0; i < 12; i++) {
            	pedroRunningFrame.add(pedroRunningFrames[0][i]);
            }
            pedroRunningAnimation = new Animation<>(0.06f, pedroRunningFrame, Animation.PlayMode.LOOP);
            
            // Pedro dying
            Texture pedroDyingTex = Storage.assetManager.get("enemies/Pedro/Dying.png", Texture.class);
            pedroDyingTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] pedroDyingFrames = TextureRegion.split(pedroDyingTex, pedroDyingTex.getWidth() / 15, pedroDyingTex.getHeight());
            Array<TextureRegion> pedroDyingFrame = new Array<>();
            for (int i = 0; i < 15; i++) {
            	pedroDyingFrame.add(pedroDyingFrames[0][i]);
            }
            pedroDyingAnimation = new Animation<>(0.05f, pedroDyingFrame, Animation.PlayMode.NORMAL);

            // Pedro attacking
            Texture pedroAttackingTex = Storage.assetManager.get("enemies/Pedro/Attacking.png", Texture.class);
            pedroAttackingTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] pedroAttackFrames = TextureRegion.split(pedroAttackingTex, pedroAttackingTex.getWidth() / 12, pedroAttackingTex.getHeight());
            Array<TextureRegion> pedroAttackingFrame = new Array<>();
            for (int i = 0; i < 12; i++) {
            	pedroAttackingFrame.add(pedroAttackFrames[0][i]);
            }
            pedroAttackingAnimation = new Animation<>(0.05f, pedroAttackingFrame, Animation.PlayMode.NORMAL);
            
            // Pedro idle
            Texture pedroIdleTex = Storage.assetManager.get("enemies/Pedro/Idle.png", Texture.class);
            pedroIdleTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] pedroIdleFrames = TextureRegion.split(pedroIdleTex, pedroIdleTex.getWidth() / 12, pedroAttackingTex.getHeight());
            Array<TextureRegion> pedroIdleFrame = new Array<>();
            for (int i = 0; i < 12; i++) {
            	pedroIdleFrame.add(pedroIdleFrames[0][i]);
            }
            pedroIdleAnimation = new Animation<>(0.075f, pedroIdleFrame, Animation.PlayMode.LOOP);
        	
        	// Dog running
        	Texture dogRunningTex = Storage.assetManager.get("character/Dog/Running.png", Texture.class);
    		dogRunningTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] dogRunningFrames = TextureRegion.split(dogRunningTex, dogRunningTex.getWidth() / 6, dogRunningTex.getHeight());
            Array<TextureRegion> dogRunningFrame = new Array<>();
            for (int i = 0; i < 6; i++) {
            	dogRunningFrame.add(dogRunningFrames[0][i]);
            }
            dogRunningAnimation = new Animation<>(0.08f, dogRunningFrame, Animation.PlayMode.LOOP);
        	
            // Dog idle
            Texture dogIdleTex = Storage.assetManager.get("character/Dog/Idle.png", Texture.class);
    		dogIdleTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] dogIdleFrames = TextureRegion.split(dogIdleTex, dogIdleTex.getWidth() / 6, dogIdleTex.getHeight());
            Array<TextureRegion> dogIdleFrame = new Array<>();
            for (int i = 0; i < 6; i++) {
            	dogIdleFrame.add(dogIdleFrames[0][i]);
            }
            dogIdleAnimation = new Animation<>(0.1f, dogIdleFrame, Animation.PlayMode.LOOP);
            
            // Dog dying
            Texture dogDyingTex = Storage.assetManager.get("character/Dog/Dying.png", Texture.class);
    		dogDyingTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] dogDyingFrames = TextureRegion.split(dogDyingTex, dogDyingTex.getWidth() / 6, dogDyingTex.getHeight());
            Array<TextureRegion> dogDyingFrame = new Array<>();
            for (int i = 0; i < 6; i++) {
            	dogDyingFrame.add(dogDyingFrames[0][i]);
            }
            dogDyingAnimation = new Animation<>(0.09f, dogDyingFrame, Animation.PlayMode.NORMAL);
            
            // Dog attacking
            Texture dogAttackingTex = Storage.assetManager.get("character/Dog/Attacking.png", Texture.class);
    		dogAttackingTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] dogAttackingFrames = TextureRegion.split(dogAttackingTex, dogAttackingTex.getWidth() / 5, dogAttackingTex.getHeight());
            Array<TextureRegion> dogAttackingFrame = new Array<>();
            for (int i = 0; i < 5; i++) {
            	dogAttackingFrame.add(dogAttackingFrames[0][i]);
            }
            dogAttackingAnimation = new Animation<>(0.08f, dogAttackingFrame, Animation.PlayMode.NORMAL);
            
            // Dog jumping
            Texture dogJumpingTex = Storage.assetManager.get("character/Dog/Jumping.png", Texture.class);
    		dogJumpingTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] dogJumpingFrames = TextureRegion.split(dogJumpingTex, dogJumpingTex.getWidth() / 7, dogJumpingTex.getHeight());
            Array<TextureRegion> dogJumpingFrame = new Array<>();
            for (int i = 0; i < 7; i++) {
            	dogJumpingFrame.add(dogJumpingFrames[0][i]);
            }
            dogJumpingAnimation = new Animation<>(0.08f, dogJumpingFrame, Animation.PlayMode.NORMAL);
        	
        	// Archer running
        	Texture archerRunningTex = Storage.assetManager.get("character/Archer/Running.png", Texture.class);
    		archerRunningTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] archerRunningFrames = TextureRegion.split(archerRunningTex, archerRunningTex.getWidth() / 12, archerRunningTex.getHeight());
            Array<TextureRegion> archerRunningFrame = new Array<>();
            for (int i = 0; i < 12; i++) {
            	archerRunningFrame.add(archerRunningFrames[0][i]);
            }
            archerRunningAnimation = new Animation<>(0.06f, archerRunningFrame, Animation.PlayMode.LOOP);
        	
            // Archer idle
            Texture archerIdleTex = Storage.assetManager.get("character/Archer/Idle.png", Texture.class);
    		archerIdleTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] archerIdleFrames = TextureRegion.split(archerIdleTex, archerIdleTex.getWidth() / 18, archerIdleTex.getHeight());
            Array<TextureRegion> archerIdleFrame = new Array<>();
            for (int i = 0; i < 18; i++) {
            	archerIdleFrame.add(archerIdleFrames[0][i]);
            }
            archerIdleAnimation = new Animation<>(0.05f, archerIdleFrame, Animation.PlayMode.LOOP);
            
            // Archer dying
            Texture archerDyingTex = Storage.assetManager.get("character/Archer/Dying.png", Texture.class);
    		archerDyingTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] archerDyingFrames = TextureRegion.split(archerDyingTex, archerDyingTex.getWidth() / 15, archerDyingTex.getHeight());
            Array<TextureRegion> archerDyingFrame = new Array<>();
            for (int i = 0; i < 15; i++) {
            	archerDyingFrame.add(archerDyingFrames[0][i]);
            }
            archerDyingAnimation = new Animation<>(0.06f, archerDyingFrame, Animation.PlayMode.NORMAL);
            
            // Archer attacking
            Texture archerAttackingTex = Storage.assetManager.get("character/Archer/Attacking.png", Texture.class);
    		archerAttackingTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] archerAttackingFrames = TextureRegion.split(archerAttackingTex, archerAttackingTex.getWidth() / 9, archerAttackingTex.getHeight());
            Array<TextureRegion> archerAttackingFrame = new Array<>();
            for (int i = 0; i < 9; i++) {
            	archerAttackingFrame.add(archerAttackingFrames[0][i]);
            }
            archerAttackingAnimation = new Animation<>(0.05f, archerAttackingFrame, Animation.PlayMode.NORMAL);
            
            // Archer jumping
            Texture archerJumpingTex = Storage.assetManager.get("character/Archer/Jumping.png", Texture.class);
    		archerJumpingTex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);           
            TextureRegion[][] archerJumpingFrames = TextureRegion.split(archerJumpingTex, archerJumpingTex.getWidth() / 6, archerJumpingTex.getHeight());
            Array<TextureRegion> archerJumpingFrame = new Array<>();
            for (int i = 0; i < 6; i++) {
            	archerJumpingFrame.add(archerJumpingFrames[0][i]);
            }
            archerJumpingAnimation = new Animation<>(0.075f, archerJumpingFrame, Animation.PlayMode.LOOP);
            
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
        peepeeAnimationTime += delta;
        vfxAnimationTime += delta;
        archerAnimationTime += delta;
        dogAnimationTime += delta;
        boarBossAnimationTime += delta;
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
    	case "Peepee":
    		this.peepeeFacingRight = isFacingRight;
    		break;
    	case "PlayerArcher":
    		this.archerFacingRight = isFacingRight;
    		break;
    	case "DogFollower":
    		this.dogFacingRight = isFacingRight;
    		break;
    	case "BoarBoss":
    		this.boarBossFacingRight = isFacingRight;
    		break;
    	}       
    }

    public void setState(State newState, String entity) {
    	switch(entity) {
    	case "PlayerMelee":
    		if (newState != currentState) {
                currentState = newState;
                animationTime = 0f;
            }
    		break;
    	case "Pedro":
    		if (newState != pedroCurrentState) {
                pedroCurrentState = newState;
                pedroAnimationTime = 0f;
            }
    		break;
    	case "Mlem":
    		if (newState != mlemCurrentState) {
                mlemCurrentState = newState;
                mlemAnimationTime = 0f;
            }
    		break;
    	case "Peepee":
    		if (newState != peepeeCurrentState) {
    			peepeeCurrentState = newState;
                peepeeAnimationTime = 0f;
            }
    		break;
    	case "PlayerArcher":
    		if (newState != archerCurrentState) {
                archerCurrentState = newState;
                archerAnimationTime = 0f;
            }
    		break;
    	case "DogFollower":
    		if (newState != dogCurrentState) {
    			dogCurrentState = newState;
                dogAnimationTime = 0f;
            }
    		break;
    	case "BoarBoss":
    		if (newState != boarBossCurrentState) {
    			boarBossCurrentState = newState;
                boarBossAnimationTime = 0f;
            }
    		break;
    	}
    }

    public State getState(String entity) {
    	switch(entity) {
    	case "PlayerMelee":
    		return currentState;
    	case "Pedro":
    		return pedroCurrentState;
    	case "Mlem":
    		return mlemCurrentState;
    	case "Peepee":
    		return peepeeCurrentState;
    	case "PlayerArcher":
    		return archerCurrentState;
    	case "DogFollower":
    		return dogCurrentState;
    	case "BoarBoss":
    		return boarBossCurrentState;
    	default:
    		return null;
    	}
    }
    
    public void setState(vfxState newState) {
    	vfxAnimationTime = 0f;
    	if (newState != vfxCurrentState) {
    		vfxCurrentState = newState;
            vfxAnimationTime = 0f;
        }
    }
    
    public vfxState getState() {
    	return vfxCurrentState;
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
    
    public TextureRegion getDogCurrentFrame() {
        Animation<TextureRegion> currentDogAnimation;

        switch (dogCurrentState) {
        	case DYING:
        		currentDogAnimation = dogDyingAnimation;
        		break;
            case ATTACKING:
                currentDogAnimation = dogAttackingAnimation;
                break;
            case RUNNING:
                currentDogAnimation = dogRunningAnimation;
                break;
            case JUMPING:
                currentDogAnimation = dogJumpingAnimation;
                break;
            case IDLE:
            default:
                currentDogAnimation = dogIdleAnimation;
                break;
        }

        TextureRegion currentDogFrame = currentDogAnimation.getKeyFrame(dogAnimationTime);

        if (dogFacingRight && currentDogFrame.isFlipX()) {
            currentDogFrame.flip(true, false);
        } else if (!dogFacingRight && !currentDogFrame.isFlipX()) {
            currentDogFrame.flip(true, false);
        }

        return currentDogFrame;
    }
    
    public TextureRegion getArcherCurrentFrame() {
        Animation<TextureRegion> currentArcherAnimation;

        switch (archerCurrentState) {
        	case DYING:
        		currentArcherAnimation = archerDyingAnimation;
        		break;
            case ATTACKING:
                currentArcherAnimation = archerAttackingAnimation;
                break;
            case RUNNING:
                currentArcherAnimation = archerRunningAnimation;
                break;
            case JUMPING:
                currentArcherAnimation = archerJumpingAnimation;
                break;
            case IDLE:
            default:
                currentArcherAnimation = archerIdleAnimation;
                break;
        }

        TextureRegion currentArcherFrame = currentArcherAnimation.getKeyFrame(archerAnimationTime);

        if (archerFacingRight && currentArcherFrame.isFlipX()) {
        	currentArcherFrame.flip(true, false);
        } else if (!archerFacingRight && !currentArcherFrame.isFlipX()) {
        	currentArcherFrame.flip(true, false);
        }

        return currentArcherFrame;
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
            case ATTACKING:
                currentPedroAnimation = pedroAttackingAnimation;
                break;
            case IDLE:
                currentPedroAnimation = pedroIdleAnimation;
                break;
            default:
                currentPedroAnimation = pedroIdleAnimation;
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
    
    public TextureRegion getBoarBossCurrentFrame() {
        Animation<TextureRegion> currentBoarBossAnimation;

        switch (boarBossCurrentState) {
        	case DYING:
        		currentBoarBossAnimation = boarBossDyingAnimation;
        		break;
            case ATTACK_CHARGE:
            	currentBoarBossAnimation = boarBossAttackingChargeAnimation;
                break;
            case ATTACK_THROW:
            	currentBoarBossAnimation = boarBossAttackingThrowAnimation;
                break;
            case RUNNING:
            	currentBoarBossAnimation = boarBossRunningAnimation;
                break;
            case HURT:
            	currentBoarBossAnimation = boarBossHurtAnimation;
                break;
            case IDLE:
            default:
            	currentBoarBossAnimation = boarBossIdleAnimation;
                break;
        }

        TextureRegion currentFrame = currentBoarBossAnimation.getKeyFrame(boarBossAnimationTime);

        if (boarBossFacingRight && currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        } else if (!boarBossFacingRight && !currentFrame.isFlipX()) {
            currentFrame.flip(true, false);
        }

        return currentFrame;
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
    
    public TextureRegion getPeepeeCurrentFrame() {
        Animation<TextureRegion> currentPeepeeAnimation;

        switch (peepeeCurrentState) {
            case DYING:
            	currentPeepeeAnimation = peepeeDyingAnimation;
                break;
            case RUNNING:
            	currentPeepeeAnimation = peepeeRunningAnimation;
                break;
            default:
            	currentPeepeeAnimation = peepeeRunningAnimation;
                break;
        }

        TextureRegion currentPeepeeFrame = currentPeepeeAnimation.getKeyFrame(peepeeAnimationTime);

        if (peepeeFacingRight && currentPeepeeFrame.isFlipX()) {
            currentPeepeeFrame.flip(true, false);
        } else if (!peepeeFacingRight && !currentPeepeeFrame.isFlipX()) {
        	currentPeepeeFrame.flip(true, false);
        }

        return currentPeepeeFrame;
    }
    
    public TextureRegion getVfxCurrentFrame() {
        Animation<TextureRegion> currentVfxAnimation;

        switch (vfxCurrentState) {
            case LIGHTNING:
            	currentVfxAnimation = lightningAnimation;
                break;
            case FIREBALL:
            	currentVfxAnimation = fireballAnimation;
                break;
            default:
            	currentVfxAnimation = lightningAnimation;
                break;
        }

        TextureRegion currentVfxFrame = currentVfxAnimation.getKeyFrame(vfxAnimationTime);

        return currentVfxFrame;
    }

    public boolean isAnimationFinished(String entity) {
    	if(entity.equals("PlayerMelee")) {
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
    	else if(entity.equals("Pedro")) {
    		switch (pedroCurrentState) {
	        case DYING:
	            return pedroDyingAnimation.isAnimationFinished(pedroAnimationTime);
	        case RUNNING:
	            return pedroRunningAnimation.isAnimationFinished(pedroAnimationTime);
	        case ATTACKING:
	            return pedroAttackingAnimation.isAnimationFinished(pedroAnimationTime);
	        case IDLE:
	            return pedroIdleAnimation.isAnimationFinished(pedroAnimationTime);
	        default:
	        	return pedroIdleAnimation.isAnimationFinished(pedroAnimationTime);
    		}
    	}
    	else if(entity.equals("Mlem")) {
    		switch (mlemCurrentState) {
	        case DYING:
	            return mlemDyingAnimation.isAnimationFinished(mlemAnimationTime);
	        case RUNNING:
	            return mlemRunningAnimation.isAnimationFinished(mlemAnimationTime);
	        default:
            return mlemRunningAnimation.isAnimationFinished(mlemAnimationTime);
    		}
    	}
    	else if(entity.equals("Peepee")) {
    		switch (peepeeCurrentState) {
	        case DYING:
	            return peepeeDyingAnimation.isAnimationFinished(peepeeAnimationTime);
	        case RUNNING:
	            return peepeeRunningAnimation.isAnimationFinished(peepeeAnimationTime);
	        default:
            return peepeeRunningAnimation.isAnimationFinished(peepeeAnimationTime);
    		}
    	}
    	else if(entity.equals("VFX")) {
    		switch(vfxCurrentState) {
    		case LIGHTNING:
    			return lightningAnimation.isAnimationFinished(vfxAnimationTime);
    		case FIREBALL:
    			return fireballAnimation.isAnimationFinished(vfxAnimationTime);
			default:
				return false;
    		}
    	}
    	else if(entity.equals("PlayerArcher")) {
    		switch (archerCurrentState) {
            case ATTACKING:
                return archerAttackingAnimation.isAnimationFinished(archerAnimationTime);
            case DYING:
                return archerDyingAnimation.isAnimationFinished(archerAnimationTime);
            case RUNNING:
                return archerRunningAnimation.isAnimationFinished(archerAnimationTime);
            case JUMPING:
                return archerJumpingAnimation.isAnimationFinished(archerAnimationTime);
            case IDLE:
            default:
                return archerIdleAnimation.isAnimationFinished(archerAnimationTime);
    		}
    	}
    	else if(entity.equals("DogFollower")) {
    		switch (dogCurrentState) {
            case ATTACKING:
                return dogAttackingAnimation.isAnimationFinished(dogAnimationTime);
            case DYING:
                return dogDyingAnimation.isAnimationFinished(dogAnimationTime);
            case RUNNING:
                return dogRunningAnimation.isAnimationFinished(dogAnimationTime);
            case JUMPING:
                return dogJumpingAnimation.isAnimationFinished(dogAnimationTime);
            case IDLE:
            default:
                return dogIdleAnimation.isAnimationFinished(dogAnimationTime);
    		}
    	}
    	else if(entity.equals("BoarBoss")) {
    		switch (boarBossCurrentState) {
	        case DYING:
	            return boarBossDyingAnimation.isAnimationFinished(boarBossAnimationTime);
	        case RUNNING:
	            return boarBossRunningAnimation.isAnimationFinished(boarBossAnimationTime);
	        case ATTACK_CHARGE:
	            return boarBossAttackingChargeAnimation.isAnimationFinished(boarBossAnimationTime);
	        case ATTACK_THROW:
	            return boarBossAttackingThrowAnimation.isAnimationFinished(boarBossAnimationTime);
	        case HURT:
                return boarBossHurtAnimation.isAnimationFinished(boarBossAnimationTime);
	        case IDLE:
	            return boarBossIdleAnimation.isAnimationFinished(boarBossAnimationTime);
	        default:
	        	return boarBossIdleAnimation.isAnimationFinished(boarBossAnimationTime);
    		}
    	}
    	else
    		return false;
    }

	public boolean isFacingRight(String entity) {
		switch(entity) {
		case "PlayerMelee":
			return facingRight;
		case "Pedro":
			return pedroFacingRight;
		case "Mlem":
			return mlemFacingRight;
		case "Peepee":
			return peepeeFacingRight;
		case "PlayerArcher":
			return archerFacingRight;
		case "DogFollower":
			return dogFacingRight;
		case "BoarBoss":
			return boarBossFacingRight;
		default:
			return false;
		}
	}
}