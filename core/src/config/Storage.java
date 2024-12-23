package config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class Storage {
    private static Storage instance = null;
    public Skin skin;
    public TextButton.TextButtonStyle buttonStyle;
    public LabelStyle labelStyle;
    public TextFieldStyle textStyle;
    public BitmapFont font;
    public static AssetManager assetManager = new AssetManager();
    private static boolean newLoad = true, playerDead = false, invulnerable = false; 
	private static int levelNum = 0, playerChar = 1, playerCoins = 0;

	public static synchronized Storage getInstance()  {
        if (instance == null) {
            instance = new Storage();
        }
        return instance;
    }
    
    public Storage() {
        skin = new Skin(Gdx.files.internal("buttons/newskin/newskin.json"));    
        if(newLoad) {
            newLoad = false;
            loadPlayerAssets();
            loadEnemyAssets();
            loadItems();
            loadEffects();
        }        
    }  
    
    private static void loadItems() {
    	assetManager.load("items/Coin.png", Texture.class);
    	assetManager.load("items/Coins.png", Texture.class);
    	assetManager.load("items/CoinHUD.png", Texture.class);
    }
    
    private static void loadEffects() {
    	assetManager.load("effects/Lightning.png", Texture.class);
    	assetManager.load("effects/Fireball.png", Texture.class);
    	
    	assetManager.finishLoading();
	}

	private static void loadEnemyAssets() {
    	assetManager.load("enemies/Mlem/Walking.png", Texture.class);
    	assetManager.load("enemies/Mlem/Dying.png", Texture.class);
    	
    	assetManager.load("enemies/Pedro/Attacking.png", Texture.class);
    	assetManager.load("enemies/Pedro/Idle.png", Texture.class);
    	assetManager.load("enemies/Pedro/Walking.png", Texture.class);
    	assetManager.load("enemies/Pedro/Dying.png", Texture.class);
    	
    	assetManager.load("enemies/Peepee/Walking.png", Texture.class);
    	assetManager.load("enemies/Peepee/Dying.png", Texture.class);
    	
    	assetManager.load("enemies/BoarBoss/Dying.png", Texture.class);
    	assetManager.load("enemies/BoarBoss/AttackCharge.png", Texture.class);
    	assetManager.load("enemies/BoarBoss/AttackThrow.png", Texture.class);
    	assetManager.load("enemies/BoarBoss/Hurt.png", Texture.class);
    	assetManager.load("enemies/BoarBoss/Idle.png", Texture.class);
    	assetManager.load("enemies/BoarBoss/Walking.png", Texture.class);
    	assetManager.load("enemies/BoarBoss/Ball.png", Texture.class);
	}

	public static void loadPlayerAssets() {   
		// Archer
    	assetManager.load("character/Archer/Running.png", Texture.class);
    	assetManager.load("character/Archer/Dying.png", Texture.class);
    	assetManager.load("character/Archer/Attacking.png", Texture.class);
    	assetManager.load("character/Archer/Jumping.png", Texture.class);
    	assetManager.load("character/Archer/Idle.png", Texture.class);
    	assetManager.load("character/Archer/Arrow.png", Texture.class);
    	
    	// Dog follower
    	assetManager.load("character/Dog/Running.png", Texture.class);
    	assetManager.load("character/Dog/Dying.png", Texture.class);
    	assetManager.load("character/Dog/Attacking.png", Texture.class);
    	assetManager.load("character/Dog/Jumping.png", Texture.class);
    	assetManager.load("character/Dog/Idle.png", Texture.class);
		
        // Walking animation
        assetManager.load("character/Walking/Minotaur_01_Walking_000.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_001.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_002.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_003.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_004.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_005.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_006.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_007.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_008.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_009.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_010.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_011.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_012.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_013.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_014.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_015.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_016.png", Texture.class);
        assetManager.load("character/Walking/Minotaur_01_Walking_017.png", Texture.class);
       
        // Idle animation
        assetManager.load("character/Idle/Minotaur_01_Idle_000.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_001.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_002.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_003.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_004.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_005.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_006.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_007.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_008.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_009.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_010.png", Texture.class);
        assetManager.load("character/Idle/Minotaur_01_Idle_011.png", Texture.class);

        // Jumping animation
        assetManager.load("character/Jump Loop/Minotaur_01_Jump Loop_000.png", Texture.class);
        assetManager.load("character/Jump Loop/Minotaur_01_Jump Loop_001.png", Texture.class);
        assetManager.load("character/Jump Loop/Minotaur_01_Jump Loop_002.png", Texture.class);
        assetManager.load("character/Jump Loop/Minotaur_01_Jump Loop_003.png", Texture.class);
        assetManager.load("character/Jump Loop/Minotaur_01_Jump Loop_004.png", Texture.class);
        assetManager.load("character/Jump Loop/Minotaur_01_Jump Loop_005.png", Texture.class);
        
        // Attack animation
        assetManager.load("character/Attacking/Minotaur_01_Attacking_000.png", Texture.class);
        assetManager.load("character/Attacking/Minotaur_01_Attacking_001.png", Texture.class);
        assetManager.load("character/Attacking/Minotaur_01_Attacking_002.png", Texture.class);
        assetManager.load("character/Attacking/Minotaur_01_Attacking_003.png", Texture.class);
        assetManager.load("character/Attacking/Minotaur_01_Attacking_004.png", Texture.class);
        assetManager.load("character/Attacking/Minotaur_01_Attacking_005.png", Texture.class);
        assetManager.load("character/Attacking/Minotaur_01_Attacking_006.png", Texture.class);
        assetManager.load("character/Attacking/Minotaur_01_Attacking_007.png", Texture.class);
        assetManager.load("character/Attacking/Minotaur_01_Attacking_008.png", Texture.class);
        assetManager.load("character/Attacking/Minotaur_01_Attacking_009.png", Texture.class);
        assetManager.load("character/Attacking/Minotaur_01_Attacking_010.png", Texture.class);
        assetManager.load("character/Attacking/Minotaur_01_Attacking_011.png", Texture.class);

        // Dying animation
        assetManager.load("character/Dying/Minotaur_01_Dying_000.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_001.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_002.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_003.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_004.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_005.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_006.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_007.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_008.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_009.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_010.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_011.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_012.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_013.png", Texture.class);
        assetManager.load("character/Dying/Minotaur_01_Dying_014.png", Texture.class);

        // Load pre-generated bitmap font files (.fnt and .png)
        assetManager.load("fonts/Cascadia.fnt", BitmapFont.class);    
    }
    
	public void createFont() {
	    if (assetManager.isLoaded("fonts/Cascadia.fnt", BitmapFont.class)) {
	        font = assetManager.get("fonts/Cascadia.fnt", BitmapFont.class);

	        Texture borderTextureUp = new Texture(Gdx.files.internal("buttons/newskin/newskin_data/textbutton.9.png"));
	        Texture borderTextureDown = new Texture(Gdx.files.internal("buttons/newskin/newskin_data/textbutton-down.9.png"));

	        NinePatch borderPatchUp = new NinePatch(borderTextureUp, 1, 1, 1, 1);
	        NinePatch borderPatchDown = new NinePatch(borderTextureDown, 1, 1, 1, 1);

	        buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
	        buttonStyle.up = new NinePatchDrawable(borderPatchUp);
	        buttonStyle.down = new NinePatchDrawable(borderPatchDown);
	        buttonStyle.font = font;

	        labelStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
	        labelStyle.font = font;

	        textStyle = new TextField.TextFieldStyle(skin.get(TextField.TextFieldStyle.class));
	        textStyle.font = font;
	    } else {
	        Gdx.app.log("Storage", "Font 'fonts/Cascadia.fnt' not loaded yet!");
	    }
	}
    
    public BitmapFont getFont() {
    	return font;
    }

	public static int getLevelNum() {
		return levelNum;
	}

	public static void setLevelNum(int levelNum) {
		Storage.levelNum = levelNum;
	}

	public static int getPlayerChar() {
		return playerChar;
	}
	
	public static void setPlayerChar(int playerChar) {
		Storage.playerChar = playerChar;
	}

	public static int getPlayerCoins() {
		return playerCoins;
	}

	public static void setPlayerCoins(int playerCoins) {
		Storage.playerCoins = playerCoins;
	}

	public static boolean isPlayerDead() {
		return playerDead;
	}

	public static void setPlayerDead(boolean playerDead) {
		Storage.playerDead = playerDead;
	}
	
	public static boolean isInvulnerable() {
		return invulnerable;
	}

	public static void setInvulnerable(boolean invulnerable) {
		Storage.invulnerable = invulnerable;
	}
}
